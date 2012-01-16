package al.franzis.osgi.weaving.core.equinox;

import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;

public class AOPWeavingHook implements WeavingHook {
	private final Weaver weaver;
	
	
	public AOPWeavingHook() {
		weaver = new Weaver();
	}
	
	@Override
	public void weave(WovenClass wovenClass) {
		weaver.weave(wovenClass);
	}
	
	
}
