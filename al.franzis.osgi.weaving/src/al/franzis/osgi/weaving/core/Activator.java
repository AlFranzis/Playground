package al.franzis.osgi.weaving.core;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.hooks.weaving.WeavingHook;

public class Activator implements BundleActivator {
	private static BundleContext bundleContext;
	
	public void start(BundleContext context) throws Exception {
		bundleContext = context;
		
		// register class weaving hook as OSGi service
		context.registerService(
				WeavingHook.class.getName(), 
				new AOPWeavingHook(), 
				new Hashtable<String,Object>());
	}
	
	public void stop(BundleContext context) throws Exception {
		/*
		 * De-registration of weaving service happens automatically
		 * when bundle is stopped
		 */
	}
	
	public static BundleContext getBundleContext() {
		return bundleContext;
	}

}
