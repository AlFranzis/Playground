package al.franzis.osgi.weaving.core.equinox.hooks;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;

import al.franzis.osgi.weaving.core.equinox.adaptors.IEquinoxWeavingAdaptor;

public class EquinoxWeavingBundleEntry extends BundleEntry {

    private final IEquinoxWeavingAdaptor adaptor;

    private final URL bundleFileURL;

    private final BundleEntry delegate;

    private final boolean dontWeave;

    public EquinoxWeavingBundleEntry(IEquinoxWeavingAdaptor weavingAdaptor, 
    		BundleEntry delegate, URL url, boolean dontWeave) {
        this.adaptor = weavingAdaptor;
        this.bundleFileURL = url;
        this.delegate = delegate;
        this.dontWeave = dontWeave;
    }

    public boolean dontWeave() {
        return dontWeave;
    }

    public IEquinoxWeavingAdaptor getAdaptor() {
        return adaptor;
    }

    public URL getBundleFileURL() {
        return bundleFileURL;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return delegate.getBytes();
    }

    @Override
    public URL getFileURL() {
        return delegate.getFileURL();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }

    @Override
    public URL getLocalURL() {
        return delegate.getLocalURL();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public long getSize() {
        return delegate.getSize();
    }

    @Override
    public long getTime() {
        return delegate.getTime();
    }

}
