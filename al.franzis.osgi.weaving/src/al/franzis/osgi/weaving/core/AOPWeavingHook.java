package al.franzis.osgi.weaving.core;

import static al.franzis.osgi.weaving.core.Constants.CORE_PACKAGE;

import java.lang.reflect.Modifier;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.util.proxy.MethodHandler;

import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;

public class AOPWeavingHook implements WeavingHook {
	private static final ThreadLocal<Boolean> threadInsideWeaving = new ThreadLocal<Boolean>() {
		public Boolean initialValue() {
			return Boolean.FALSE;
		}
	};
	
	private ClassPool classPool;
	private CtClass methodHandlerCtClass;
	private CtClass methodArrayCtClass;
	
	private Matcher matcher;
	
	public AOPWeavingHook() {}
	
	@Override
	public void weave(WovenClass wovenClass) {
		if(Boolean.TRUE == threadInsideWeaving.get())
			return;
		
		try {
			threadInsideWeaving.set(Boolean.TRUE);
		
			String className = wovenClass.getClassName();
//			System.out.println("Loading " + className);
		
			if(skipClass(className))
				return;
			
			if (classPool == null) {
				classPool = ClassPool.getDefault();
				classPool.insertClassPath(new ClassClassPath(MethodHandler.class));
				ClassLoader loader = wovenClass.getBundleWiring().getClassLoader();
				classPool.insertClassPath(new LoaderClassPath(loader));

				methodHandlerCtClass = classPool.get(IMethodInvocationHandler.class.getName());
				methodArrayCtClass = classPool.get("java.lang.reflect.Method[]");
			}

			CtClass ctClass = classPool.get(className);
			
			if(skipInterface(ctClass))
				return;
			
			if ( matcher == null )
				matcher = new Matcher();
			
			List<HandlerDefinition<CtClass, CtMethod>> matchingHandlers;
			if ((matchingHandlers = matcher.match(ctClass)) != null) {
				boolean handlerDefined = false;
				int handlerIndex = getHandlerIndexForClass(className);
				int methodIndex = 0;

				CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
				int methodCount = declaredMethods.length;
				for (CtMethod ctMethod : declaredMethods) {
					if(matchingHandlers.get(0).getMethodMatcher().matches(ctMethod)) {
						instrumentMethod(ctClass, ctMethod, methodCount,
								handlerIndex, methodIndex, handlerDefined);
						handlerDefined = true;
					}
				}

				// write modified (instrumented) bytecode
				if (ctClass.isModified()) {
					byte[] instrumentedByteCode = ctClass.toBytecode();
					wovenClass.setBytes(instrumentedByteCode);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			threadInsideWeaving.set(Boolean.FALSE);
		}
	}
	
	private void instrumentMethod(CtClass ctClass, CtMethod ctMethod, 
			int methodCount, int handlerIndex, int methodIndex, boolean handlerDefined) throws NotFoundException, CannotCompileException {
		String methodName = ctMethod.getName();
		String delegatorName = "d" + methodName;
		String methodDescription = Helpers.makeDescriptor(ctMethod);
		
		// rename original method
		ctMethod.setName(delegatorName);
		
		if (!handlerDefined) {

			// add static 'handler' field
			CtField handlerCtField = createStaticHandlerField(
					ctClass, methodHandlerCtClass);
			ctClass.addField(handlerCtField);

			// add static initializer that assigns 'handler'
			// field
			createStaticHandlerInitializer(ctClass, handlerIndex);
			
			// add static methods array field 'amethods' that caches method-lookups using reflection
			addStaticMethodsArrayField(ctClass, Constants.METHODS_ARRAY_NAME, methodCount);
		}
		
		// add forwarder method
		CtMethod forwarderMethod = createForwarderMethod(ctClass, 
				ctMethod, delegatorName, methodName, methodDescription, methodIndex);
		ctClass.addMethod(forwarderMethod);
		methodIndex += 2;
	}
	
	private CtMethod createForwarderMethod( CtClass ctClass, CtMethod originalMethod, 
			String originalMethodName, String forwarderMethodName, String methodDescription, int methodIndex ) throws CannotCompileException, NotFoundException {
		CtMethod forwarderMethod = CtNewMethod.copy(originalMethod, forwarderMethodName, ctClass, null);
		
		String handlerInvoke = null;
		String arrayForwarderMethodCall = Constants.METHODS_ARRAY_NAME + "[" + (methodIndex+1) + "]";
		String arrayOriginalMethodCall = Constants.METHODS_ARRAY_NAME + "[" + methodIndex + "]";
		if( Modifier.isStatic(forwarderMethod.getModifiers())) 
			handlerInvoke = "handler.invoke(null, " + arrayForwarderMethodCall + "," + arrayOriginalMethodCall + ", $args);";
		else 
			handlerInvoke = "handler.invoke($0, " + arrayForwarderMethodCall + "," + arrayOriginalMethodCall + ", $args);";
		
		if ( CtClass.voidType != forwarderMethod.getReturnType())
			handlerInvoke = "return ($r)" + handlerInvoke;
			
		String thisClassAssignment = null;
		if( Modifier.isStatic(forwarderMethod.getModifiers())) 
			thisClassAssignment = "java.lang.Class thisClass = Class.forName(\"" + ctClass.getName() + "\");";
		else
			thisClassAssignment = "java.lang.Class thisClass = getClass();";
		
		
		String forwarderMethodBody = 
				"{"
				  + thisClassAssignment
				  + CORE_PACKAGE + ".Helpers.find2Methods(thisClass,\"" 
						+ originalMethodName + "\",\"" + forwarderMethodName + "\"," 
						+ methodIndex + ",\"" + methodDescription + "\"," + Constants.METHODS_ARRAY_NAME + ");"
				  + handlerInvoke
				+"}";
		forwarderMethod.setBody(forwarderMethodBody);
		
		return forwarderMethod;
	}
	
	private void createStaticHandlerInitializer(CtClass ctClass, int handlerIndex) throws CannotCompileException {
		CtConstructor staticInitializer = ctClass.makeClassInitializer();
		staticInitializer.insertBefore( Constants.HANDLER_FIELD_NAME + " =  " + CORE_PACKAGE + ".MethodHandlerProvider.getInstance().getHandler(" + handlerIndex + ");");
	}
	
	private void addStaticMethodsArrayField(CtClass ctClass, String methodsArrayName, int declaredMethodsCount) throws CannotCompileException, NotFoundException {
		CtField methodsArrayCtField = new CtField( methodArrayCtClass, methodsArrayName, ctClass);
		methodsArrayCtField.setModifiers(Modifier.STATIC);
		ctClass.addField(methodsArrayCtField, CtField.Initializer.byNewArray(methodArrayCtClass, declaredMethodsCount * 2));
	}
	
	private CtField createStaticHandlerField(CtClass ctClass, CtClass methodHandlerCtClass ) throws CannotCompileException {
		CtField handlerCtField = new CtField(methodHandlerCtClass , Constants.HANDLER_FIELD_NAME, ctClass);
		handlerCtField.setModifiers(Modifier.STATIC);
		return handlerCtField;
	}
	
	private int getHandlerIndexForClass( String className )
	{
		return 0;
	}
	
	private boolean skipClass(String classname) {
		return (classname.startsWith("org.eclipse") || classname.startsWith("org.osgi") 
				|| classname.startsWith("javassist") || classname.startsWith("al.franzis.osgi.weaving.core"));
	}
	
	private static boolean skipInterface(CtClass ctClass) throws NotFoundException {
		for(CtClass ctInterface : ctClass.getInterfaces())
		{
			if( ctInterface.getName().startsWith("al.franzis.osgi.weaving.core"))
				return true;
		}
		return false;
	}

}
