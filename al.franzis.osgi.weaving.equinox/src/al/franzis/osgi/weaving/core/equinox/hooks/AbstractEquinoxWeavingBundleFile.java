package al.franzis.osgi.weaving.core.equinox.hooks;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleFile;

import al.franzis.osgi.weaving.core.equinox.adaptors.IEquinoxWeavingAdaptor;

public abstract class AbstractEquinoxWeavingBundleFile extends BundleFile {

    protected BundleFile delegate;

    private final EquinoxBundleAdaptorProvider adaptorProvider;

    public AbstractEquinoxWeavingBundleFile(EquinoxBundleAdaptorProvider adaptorProvider, 
    		BundleFile bundleFile) {
        super(bundleFile.getBaseFile());
        this.adaptorProvider = adaptorProvider;
        this.delegate = bundleFile;
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public boolean containsDir(String dir) {
        return delegate.containsDir(dir);
    }

    public IEquinoxWeavingAdaptor getAdaptor() {
        return this.adaptorProvider.getAdaptor();
    }

    @Override
    public File getBaseFile() {
        final File baseFile = delegate.getBaseFile();
        return baseFile;
    }

    @Override
    public BundleEntry getEntry(String path) {
        return delegate.getEntry(path);
    }

    @Override
    public Enumeration<String> getEntryPaths(String path) {
        return delegate.getEntryPaths(path);
    }

    @Override
    public File getFile(String path, boolean nativeCode) {
        return delegate.getFile(path, nativeCode);
    }

    @Deprecated
    @Override
    public URL getResourceURL(String path, long hostBundleID) {
        return delegate.getResourceURL(path, hostBundleID);
    }
    
    @Deprecated
    @Override
    public URL getResourceURL(String path, long hostBundleID, int index) {
        return delegate.getResourceURL(path, hostBundleID, index);
    }

    @Override
    public void open() throws IOException {
        delegate.open();
    }

}
