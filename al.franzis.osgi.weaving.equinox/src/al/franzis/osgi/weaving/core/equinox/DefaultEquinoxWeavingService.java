package al.franzis.osgi.weaving.core.equinox;

import java.io.IOException;
import java.util.Map;

public class DefaultEquinoxWeavingService implements IEquinoxWeavingService {
	private EquinoxWeaver weaver; 
	
	@Override
	public void flushGeneratedClasses(ClassLoader loader) {
	}

	@Override
	public boolean generatedClassesExistFor(ClassLoader loader, String className) {
		return false;
	}

	@Override
	public Map<String, byte[]> getGeneratedClassesFor(String className) {
		return null;
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public byte[] preProcess(String name, byte[] classbytes, ClassLoader loader)
			throws IOException {
		if ( weaver == null )
			weaver = EquinoxWeaver.getWeaver();
		return weaver.weave(name, classbytes, null, null, loader);
	}

}
