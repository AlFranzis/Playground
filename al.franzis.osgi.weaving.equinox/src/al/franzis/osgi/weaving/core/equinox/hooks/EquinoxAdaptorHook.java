package al.franzis.osgi.weaving.core.equinox.hooks;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Properties;

import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.baseadaptor.hooks.AdaptorHook;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class EquinoxAdaptorHook implements AdaptorHook, HookConfigurator {
	private static BundleContext bundleContext;
	private static BaseAdaptor baseAdaptor;
	
	public static BundleContext getBundleContext() {
		return bundleContext;
	}
	
	@Override
	public void addProperties(Properties props) {
		// empty
	}

	@Override
	public FrameworkLog createFrameworkLog() {
		return null;
	}

	@Override
	public void frameworkStart(BundleContext bundleContext) throws BundleException {
		EquinoxAdaptorHook.bundleContext = bundleContext;
		
		bundleContext.registerService(
				MethodHandlerRegistrationService.class.getName(),
				new MethodHandlerRegistrationServiceImpl(), 
				new Hashtable<String,String>());
		System.out.println("Registered MethodHandlerRegistrationService for Equinox AOP");
	}

	@Override
	public void frameworkStop(BundleContext arg0) throws BundleException {
		// empty
	}

	@Override
	public void frameworkStopping(BundleContext arg0) {
		// empty
	}

	@Override
	public void handleRuntimeError(Throwable arg0) {
		// empty
	}

	@Override
	public void initialize(BaseAdaptor baseAdaptor) {
		EquinoxAdaptorHook.baseAdaptor = baseAdaptor;
	}

	@Override
	public URLConnection mapLocationToURLConnection(String arg0)
			throws IOException {
		return null;
	}

	@Override
	public void addHooks(HookRegistry hookRegistry) {
		hookRegistry.addAdaptorHook(new EquinoxAdaptorHook());
	}

	// OSGI_LEGACY: Method contained in AdaptorHook interface of bundle 'org.eclipse.osgi_3.4.2'
	// Method does not exist in later versions -> Do not tag wirh @Override
	public boolean matchDNChain(String pattern, String[] dnChain) {
		return false;
	}
	
	public static FrameworkLog getFrameworkLog() {
		return baseAdaptor.getFrameworkLog();
	}
	

}
