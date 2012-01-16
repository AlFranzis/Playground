package al.franzis.osgi.weaving.core.equinox.hooks;

import javassist.CtClass;
import javassist.CtMethod;
import al.franzis.osgi.weaving.core.equinox.IMethodInvocationHandler;
import al.franzis.osgi.weaving.core.equinox.matching.Matcher;

public interface MethodHandlerRegistrationService {
	public void registerMethodInvocationHandler(Matcher<CtClass> classMatcher, Matcher<CtMethod> methodMatcher, IMethodInvocationHandler handler );
}
