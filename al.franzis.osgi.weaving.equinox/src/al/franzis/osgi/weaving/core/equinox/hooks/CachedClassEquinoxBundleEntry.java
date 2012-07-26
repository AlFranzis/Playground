package al.franzis.osgi.weaving.core.equinox.hooks;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;

import al.franzis.osgi.weaving.core.equinox.adaptors.IEquinoxWeavingAdaptor;

public class CachedClassEquinoxBundleEntry extends BundleEntry {

    private final IEquinoxWeavingAdaptor adaptor;

    private final URL bundleFileURL;

    private final byte[] bytes;

    private final BundleEntry delegate;

    private final String name;

    public CachedClassEquinoxBundleEntry(final IEquinoxWeavingAdaptor aspectjAdaptor,
            final BundleEntry delegate, final String name, final byte[] bytes,
            final URL url) {
        this.adaptor = aspectjAdaptor;
        this.bundleFileURL = url;
        this.delegate = delegate;
        this.name = name;
        this.bytes = bytes;
    }

    public boolean dontWeave() {
        return true;
    }

    public IEquinoxWeavingAdaptor getAdaptor() {
        return adaptor;
    }

    public URL getBundleFileURL() {
        return bundleFileURL;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return bytes;
    }

    @Override
    public URL getFileURL() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (delegate == null) {
            System.err.println("error in: " + name);
        }
        return delegate.getInputStream();
    }

    @Override
    public URL getLocalURL() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    public long getTime() {
        return 0;
    }

}
