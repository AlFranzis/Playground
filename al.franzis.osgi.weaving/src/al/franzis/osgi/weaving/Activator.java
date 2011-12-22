package al.franzis.osgi.weaving;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private ServiceTracker simpleLogServiceTracker;
	private SimpleLogService simpleLogService;
	
	public void start(BundleContext context) throws Exception {
		context.registerService(WeavingHook.class.getName(), new MyWeavingHook(), new Hashtable());
		
		// register the service
		context.registerService(
				SimpleLogService.class.getName(), 
				new SimpleLogServiceImpl(), 
				new Hashtable());
		
		// create a tracker and track the log service
		simpleLogServiceTracker = 
			new ServiceTracker(context, SimpleLogService.class.getName(), null);
		simpleLogServiceTracker.open();
		
		// grab the service
		simpleLogService = (SimpleLogService) simpleLogServiceTracker.getService();

		if(simpleLogService != null)
			simpleLogService.log("Yee ha, I'm logging!");
		
	}
	
	public void stop(BundleContext context) throws Exception {
		if(simpleLogService != null)
			simpleLogService.log("Yee ha, I'm logging!");
		
		// close the service tracker
		simpleLogServiceTracker.close();
		simpleLogServiceTracker = null;
		
		simpleLogService = null;
	}

}
