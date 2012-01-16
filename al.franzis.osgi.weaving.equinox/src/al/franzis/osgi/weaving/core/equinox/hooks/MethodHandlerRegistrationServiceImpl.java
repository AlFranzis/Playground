package al.franzis.osgi.weaving.core.equinox.hooks;

import al.franzis.osgi.weaving.core.equinox.IMethodInvocationHandler;
import al.franzis.osgi.weaving.core.equinox.matching.Matcher;

public class MethodHandlerRegistrationServiceImpl implements MethodHandlerRegistrationService{

	@Override
	public void registerMethodInvocationHandler(Matcher<String> classMatcher,
			Matcher<String> methodMatcher, IMethodInvocationHandler handler) {
		System.out.println("registerMethodInvocationHandler() called");
	}

	

}
