package al.franzis.osgi.weaving;

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
	private static final String METHODS_ARRAY_NAME = "amethods";
	private static final String HANDLER_FIELD_NAME = "handler";
	
	private ClassPool classPool;
	private CtClass methodHandlerCtClass;
	private CtClass methodArrayCtClass;
	
	private Matcher matcher;
	
	public AOPWeavingHook() {
		
	}
	
	
	@Override
	public void weave(WovenClass wovenClass) {
//		if ( matcher == null )
//			matcher = new Matcher();
		
		String className = wovenClass.getClassName();

		// System.out.println("Loading " + className);

		boolean handlerDefined = false;

		try {
			// load class bytecode
			// byte[] byteCode = wovenClass.getBytes();
			if (classPool == null) {
				classPool = ClassPool.getDefault();
				classPool.insertClassPath(new ClassClassPath(MethodHandler.class));
				// classPool.insertClassPath(new
				// ClassClassPath(wovenClass.getDefinedClass()));
				// classPool.insertClassPath(new ByteArrayClassPath(className,
				// byteCode));

				ClassLoader loader = wovenClass.getBundleWiring().getClassLoader();
				classPool.insertClassPath(new LoaderClassPath(loader));

				methodHandlerCtClass = classPool
						.get(IMethodInvocationHandler.class.getName());
				methodArrayCtClass = classPool
						.get("java.lang.reflect.Method[]");
			}

			CtClass ctClass = classPool.get(className);

			List<HandlerDefinition<CtClass, CtMethod>> matchingHandlers;
			if ((matchingHandlers = matcher.match(ctClass)) != null) {

				int handlerIndex = getHandlerIndexForClass(className);
				int methodIndex = 0;

				CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
				int methodCount = declaredMethods.length;
				for (CtMethod ctMethod : declaredMethods) {
					if (ctMethod.hasAnnotation(Profile.class)) {

						instrumentMethod(ctClass, ctMethod, methodCount,
								handlerIndex, methodIndex, handlerDefined);
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
			addStaticMethodsArrayField(ctClass, METHODS_ARRAY_NAME, methodCount);
			
			handlerDefined = true;
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
		String arrayForwarderMethodCall = METHODS_ARRAY_NAME + "[" + (methodIndex+1) + "]";
		String arrayOriginalMethodCall = METHODS_ARRAY_NAME + "[" + methodIndex + "]";
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
				  + "al.franzis.osgi.weaving.Helpers.find2Methods(thisClass,\"" 
						+ originalMethodName + "\",\"" + forwarderMethodName + "\"," 
						+ methodIndex + ",\"" + methodDescription + "\"," + METHODS_ARRAY_NAME + ");"
				  + handlerInvoke
				+"}";
		forwarderMethod.setBody(forwarderMethodBody);
		
		return forwarderMethod;
	}
	
	private void createStaticHandlerInitializer(CtClass ctClass, int handlerIndex) throws CannotCompileException {
		CtConstructor staticInitializer = ctClass.makeClassInitializer();
		staticInitializer.insertBefore( HANDLER_FIELD_NAME + " =  al.franzis.osgi.weaving.MethodHandlerProvider.getInstance().getHandler(" + handlerIndex + ");");
	}
	
	private void addStaticMethodsArrayField(CtClass ctClass, String methodsArrayName, int declaredMethodsCount) throws CannotCompileException, NotFoundException {
		CtField methodsArrayCtField = new CtField( methodArrayCtClass, methodsArrayName, ctClass);
		methodsArrayCtField.setModifiers(Modifier.STATIC);
		ctClass.addField(methodsArrayCtField, CtField.Initializer.byNewArray(methodArrayCtClass, declaredMethodsCount * 2));
	}
	
	private CtField createStaticHandlerField(CtClass ctClass, CtClass methodHandlerCtClass ) throws CannotCompileException {
		CtField handlerCtField = new CtField(methodHandlerCtClass , HANDLER_FIELD_NAME, ctClass);
		handlerCtField.setModifiers(Modifier.STATIC);
		return handlerCtField;
	}
	
	private int getHandlerIndexForClass( String className )
	{
		return 0;
	}

}
