package al.franzis.osgi.weaving.core;

import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;

public class AOPWeavingHook implements WeavingHook {
	private final AOPWeaver weaver;
	
	public AOPWeavingHook() {
		weaver = AOPWeaver.getWeaver();
	}
	
	@Override
	public void weave(WovenClass wovenClass) {
		byte[] wovenBytecode = weaver.weave(wovenClass.getClassName(), wovenClass.getBytes(), wovenClass.getBundleWiring().getClassLoader());
		if(wovenBytecode != null) {
			wovenClass.setBytes(wovenBytecode);
		}
			
	}
	
}
