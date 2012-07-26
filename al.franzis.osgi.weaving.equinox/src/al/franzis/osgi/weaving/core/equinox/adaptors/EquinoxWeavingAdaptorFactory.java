package al.franzis.osgi.weaving.core.equinox.adaptors;

import org.eclipse.osgi.baseadaptor.loader.BaseClassLoader;
import org.osgi.framework.Bundle;

import al.franzis.osgi.weaving.core.equinox.DefaultEquinoxWeavingService;
import al.franzis.osgi.weaving.core.equinox.IEquinoxCachingService;
import al.franzis.osgi.weaving.core.equinox.IEquinoxWeavingService;

public class EquinoxWeavingAdaptorFactory {
	private static final DefaultEquinoxWeavingService weaver = new DefaultEquinoxWeavingService();
	
	protected IEquinoxWeavingService getWeavingService(
			final BaseClassLoader loader) {
		return weaver;
	}

	protected IEquinoxCachingService getCachingService(
			final BaseClassLoader loader, final Bundle bundle,
			final IEquinoxWeavingService weavingService) {
		return null;
	}

	public Bundle getHost(Bundle fragment) {
		return null;
	}
}
