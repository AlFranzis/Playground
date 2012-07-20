package al.franzis.osgi.weaving.core.equinox.hooks;

import java.io.IOException;

import org.eclipse.osgi.baseadaptor.bundlefile.BundleFile;

public class BaseEquinoxWeavingBundleFile extends EquinoxWeavingBundleFile {

    public BaseEquinoxWeavingBundleFile(EquinoxBundleAdaptorProvider adaptorProvider, 
    		BundleFile bundleFile) throws IOException {
        super(adaptorProvider, bundleFile);
    }
}
