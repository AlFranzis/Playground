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

	@Override
	public void addProperties(Properties arg0) {
		// empty
	}

	@Override
	public FrameworkLog createFrameworkLog() {
		return null;
	}

	@Override
	public void frameworkStart(BundleContext bundleContext) throws BundleException {
		bundleContext.registerService(
				MethodHandlerRegistrationService.class.getName(),
				new MethodHandlerRegistrationServiceImpl(), 
				new Hashtable<String,String>());
		System.out.println("Registered MethodHandlerRegistrationService");
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
	public void initialize(BaseAdaptor arg0) {
		// empty
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

}
