package al.franzis.osgi.weaving.core.equinox.adaptors;

import java.net.URL;

import org.eclipse.osgi.baseadaptor.loader.BaseClassLoader;

import al.franzis.osgi.weaving.core.equinox.WeavingCacheEntry;

public interface IEquinoxWeavingAdaptor {

    public WeavingCacheEntry findClass(String name, URL sourceFileURL);

    public void initialize();

    public void setBaseClassLoader(BaseClassLoader baseClassLoader);

    public boolean storeClass(String name, URL sourceFileURL, Class<?> clazz,
            byte[] classbytes);

    public byte[] weaveClass(String name, byte[] bytes);

}
