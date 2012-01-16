package al.franzis.osgi.weaving.core.equinox;

import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;


public abstract class MethodHandlerProvider {
	private final static MethodHandlerProvider INSTANCE = new OSGiMethodHandlerProvider();
	
	public static MethodHandlerProvider getInstance() {
		return INSTANCE;
	}
	
	public abstract List<HandlerDefinition<CtClass,CtMethod>> getHandlerDefinitions();
	
	public abstract IMethodInvocationHandler getHandler(int index);
	
}
