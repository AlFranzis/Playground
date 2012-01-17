package al.franzis.osgi.weaving.equinox.test.a;

import java.lang.reflect.Method;

import al.franzis.osgi.weaving.core.equinox.IMethodInvocationHandler;

final class AOPEquinoxMethodHandler implements
		IMethodInvocationHandler {
	@Override
	public Object invoke(Object self, Method thisMethod, Method proceed,
			Object[] args) throws Throwable {
		System.out.println("Before()");
		Object ret = proceed.invoke(self, args);
		System.out.println("After()");
		return ret;
	}
}