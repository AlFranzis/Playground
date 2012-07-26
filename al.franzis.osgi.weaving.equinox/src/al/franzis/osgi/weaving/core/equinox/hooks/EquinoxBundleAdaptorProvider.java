package al.franzis.osgi.weaving.core.equinox.hooks;

import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.framework.internal.core.BundleFragment;

import al.franzis.osgi.weaving.core.equinox.adaptors.IEquinoxWeavingAdaptor;

public class EquinoxBundleAdaptorProvider {

    private final IEquinoxWeavingAdaptorProvider adaptorProvider;

    private final BaseData baseData;

    public EquinoxBundleAdaptorProvider(final BaseData data,
            final IEquinoxWeavingAdaptorProvider adaptorProvider) {
        this.baseData = data;
        this.adaptorProvider = adaptorProvider;
    }

    public IEquinoxWeavingAdaptor getAdaptor() {
        if (this.baseData.getBundle() instanceof BundleFragment) {
            return this.adaptorProvider.getHostBundleAdaptor(this.baseData
                    .getBundleID());
        } else {
            return this.adaptorProvider.getAdaptor(this.baseData.getBundleID());
        }
    }

}
