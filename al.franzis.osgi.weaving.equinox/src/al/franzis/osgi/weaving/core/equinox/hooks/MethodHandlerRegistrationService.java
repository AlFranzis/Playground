package al.franzis.osgi.weaving.core.equinox.hooks;

import al.franzis.osgi.weaving.core.equinox.IMethodInvocationHandler;
import al.franzis.osgi.weaving.core.equinox.matching.Matcher;

public interface MethodHandlerRegistrationService {
	public void registerMethodInvocationHandler(Matcher<String> classMatcher, Matcher<String> methodMatcher, IMethodInvocationHandler handler );
}
