package al.franzis.osgi.weaving.equinox.test.a;


import javassist.CtClass;
import javassist.CtMethod;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import al.franzis.osgi.weaving.core.equinox.IMethodInvocationHandler;
import al.franzis.osgi.weaving.core.equinox.hooks.MethodHandlerRegistrationService;
import al.franzis.osgi.weaving.core.equinox.matching.Matcher;



public class Activator implements BundleActivator {
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("Started client of extension service");
		Activator.context = bundleContext;
		
		Matcher<CtClass> classMatcher = new AOPEquinoxClassMatcher();
		Matcher<CtMethod> methodMatcher = new AOPEquinoxMethodMatcher();
		IMethodInvocationHandler handler = new AOPEquinoxMethodHandler();
		
		registerHandler(classMatcher,methodMatcher,handler);
		
		EquinoxFoo equinoxFoo = new EquinoxFoo();
		equinoxFoo.publicFoobar();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
	// OSGI_LEGACY: : uBndle 'org.eclipse.osgi_3.4.2' does 
	// not support ServiceReferences with generics
	private void registerHandler( Matcher<CtClass> classMatcher, Matcher<CtMethod> methodMatcher, IMethodInvocationHandler handler) {
		String className = MethodHandlerRegistrationService.class.getName();
		@SuppressWarnings("rawtypes")
		ServiceReference serviceReference = getContext().getServiceReference(className);
		
		if(serviceReference != null) {
			MethodHandlerRegistrationService extensionService = (MethodHandlerRegistrationService)getContext().getService(serviceReference);
			if(extensionService != null) {
				extensionService.registerMethodInvocationHandler(classMatcher, methodMatcher, handler);
			}
			getContext().ungetService(serviceReference);
		}
		
	}

}
