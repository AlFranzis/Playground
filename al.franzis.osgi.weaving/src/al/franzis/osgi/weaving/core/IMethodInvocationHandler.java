package al.franzis.osgi.weaving.core;

import java.lang.reflect.Method;

public interface IMethodInvocationHandler {
	
	public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable; 

}
