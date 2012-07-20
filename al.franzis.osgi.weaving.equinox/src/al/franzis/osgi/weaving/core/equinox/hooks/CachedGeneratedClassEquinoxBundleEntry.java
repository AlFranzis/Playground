package al.franzis.osgi.weaving.core.equinox.hooks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;

import al.franzis.osgi.weaving.core.equinox.adaptors.IEquinoxWeavingAdaptor;

public class CachedGeneratedClassEquinoxBundleEntry extends BundleEntry {

    private final IEquinoxWeavingAdaptor adaptor;

    private final URL bundleFileURL;

    private final byte[] bytes;

    private final String name;

    public CachedGeneratedClassEquinoxBundleEntry(final IEquinoxWeavingAdaptor adaptor,
            final String path, final byte[] cachedBytes, final URL url) {
        this.adaptor = adaptor;
        this.bundleFileURL = url;
        this.bytes = cachedBytes;
        this.name = path;
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
        final ByteArrayInputStream result = new ByteArrayInputStream(bytes);
        return result;
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
