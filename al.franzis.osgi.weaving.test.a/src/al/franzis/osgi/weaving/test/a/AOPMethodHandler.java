package al.franzis.osgi.weaving.test.a;

import java.lang.reflect.Method;

import al.franzis.osgi.weaving.core.IMethodInvocationHandler;

public class AOPMethodHandler implements IMethodInvocationHandler {

	@Override
	public Object invoke(Object self, Method m, Method proceed, Object[] args)
			throws Throwable {
		System.out.println("Before aspect");
		Object result = proceed.invoke(self, args);
		System.out.println("After aspect");
		return result;
	}

}
