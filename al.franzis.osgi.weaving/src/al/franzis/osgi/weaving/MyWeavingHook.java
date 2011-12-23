package al.franzis.osgi.weaving;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;

public class MyWeavingHook implements WeavingHook {
	
	@Override
	public void weave(WovenClass wovenClass) {
		String className = wovenClass.getClassName();
		if (className.contains("Foo")) {
			System.out.println("WEAVING " + className);

			try {
				// load class bytecode
				byte[] byteCode = wovenClass.getBytes();
				ClassPool cp = ClassPool.getDefault();
				cp.insertClassPath(new ByteArrayClassPath(className, byteCode));
				CtClass ctClass = cp.get(className);
				
				CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
				for ( CtMethod ctMethod : declaredMethods )
				{
					if ( ctMethod.hasAnnotation(Profile.class) )
					{
						ctMethod.insertBefore("System.out.println(\"before() called\");");
						ctMethod.insertAfter("System.out.println(\"after() called\");");
					}
				}
				
				// write modified (instrumented) bytecode
				if ( ctClass.isModified() )
				{
					byte[] instrumentedByteCode = ctClass.toBytecode();
					wovenClass.setBytes(instrumentedByteCode);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
	}

}
