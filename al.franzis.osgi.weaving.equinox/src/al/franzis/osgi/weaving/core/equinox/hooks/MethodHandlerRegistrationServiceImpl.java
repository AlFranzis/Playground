package al.franzis.osgi.weaving.core.equinox.hooks;

import javassist.CtClass;
import javassist.CtMethod;
import al.franzis.osgi.weaving.core.equinox.IMethodInvocationHandler;
import al.franzis.osgi.weaving.core.equinox.MethodHandlerProvider;
import al.franzis.osgi.weaving.core.equinox.OSGiMethodHandlerProvider;
import al.franzis.osgi.weaving.core.equinox.matching.Matcher;

public class MethodHandlerRegistrationServiceImpl implements MethodHandlerRegistrationService{

	@Override
	public void registerMethodInvocationHandler(Matcher<CtClass> classMatcher,
			Matcher<CtMethod> methodMatcher, IMethodInvocationHandler handler) {
		System.out.println("registerMethodInvocationHandler() called");
		OSGiMethodHandlerProvider handlerProvider = (OSGiMethodHandlerProvider)MethodHandlerProvider.getInstance();
		handlerProvider.addHandlerDefinition(classMatcher, methodMatcher, handler);
	}

	

}
