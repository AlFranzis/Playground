package al.franzis.osgi.weaving.core.equinox.hooks;

import java.io.IOException;
import java.net.URL;

import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleFile;

import al.franzis.osgi.weaving.core.equinox.WeavingCacheEntry;
import al.franzis.osgi.weaving.core.equinox.adaptors.IEquinoxWeavingAdaptor;

/**
 * This is a wrapper for bundle files that allows the weaving runtime to create
 * wrapped instances of bundle entry objects.
 * 
 * Those bundle entry objects are used to return class bytes from the cache
 * instead of the bundle itself.
 */
public class EquinoxWeavingBundleFile extends AbstractEquinoxWeavingBundleFile {

    private final URL url;

    /**
     * Create a new wrapper for a bundle file
     * 
     * @param adaptorProvider A provider that allows this wrapper to gain access
     *            to the adaptor of this bundle
     * @param bundleFile The wrapped bundle file
     * @throws IOException
     */
    public EquinoxWeavingBundleFile(EquinoxBundleAdaptorProvider adaptorProvider, 
    		BundleFile bundleFile) throws IOException {
        super(adaptorProvider, bundleFile);
        this.url = delegate.getBaseFile().toURL();
    }

    @Override
    public BundleEntry getEntry(String path) {
        BundleEntry entry = delegate.getEntry(path);

        if (path.endsWith(".class") && entry != null) {
            final int offset = path.lastIndexOf('.');
            final String name = path.substring(0, offset).replace('/', '.');
            final IEquinoxWeavingAdaptor adaptor = getAdaptor();
            if (adaptor != null) {
                final WeavingCacheEntry cacheEntry = adaptor.findClass(name, url);
                if (cacheEntry == null) {
                    entry = new EquinoxWeavingBundleEntry(adaptor, entry, url, false);
                    
                } else if (cacheEntry.getCachedBytes() != null) {
                    entry = new CachedClassEquinoxBundleEntry(adaptor, entry, path,
                            cacheEntry.getCachedBytes(), url);
                } else {
                    entry = new EquinoxWeavingBundleEntry(adaptor, entry, url,
                            cacheEntry.dontWeave());
                }
            }
        } else if (path.endsWith(".class") && entry == null) {
            final int offset = path.lastIndexOf('.');
            final String name = path.substring(0, offset).replace('/', '.');
            final IEquinoxWeavingAdaptor adaptor = getAdaptor();
            if (adaptor != null) {
                final WeavingCacheEntry cacheEntry = adaptor.findClass(name, url);
                if (cacheEntry != null && cacheEntry.getCachedBytes() != null) {
                    entry = new CachedGeneratedClassEquinoxBundleEntry(adaptor, path,
                            cacheEntry.getCachedBytes(), url);
                }
            }
        }

        return entry;
    }

}
