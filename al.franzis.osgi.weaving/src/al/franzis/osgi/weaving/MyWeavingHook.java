package al.franzis.osgi.weaving;

import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.wiring.BundleWiring;

public class MyWeavingHook implements WeavingHook {

	@Override
	public void weave(WovenClass wovenClass) {
		System.out.println("WEAVING " + wovenClass.getClassName());
		BundleWiring wiring = wovenClass.getBundleWiring();
		
		

		
	}

}
