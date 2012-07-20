package al.franzis.osgi.weaving.core.equinox;

import java.net.URL;
import java.util.Map;

public interface IEquinoxCachingService {

    public boolean canCacheGeneratedClasses();

    public WeavingCacheEntry findStoredClass(String namespace, URL sourceFileURL,
            String name);

    public void stop();

    public boolean storeClass(String namespace, URL sourceFileURL,
            Class<?> clazz, byte[] classbytes);

    public boolean storeClassAndGeneratedClasses(String namespace,
            URL sourceFileURL, Class<?> clazz, byte[] classbytes,
            Map<String, byte[]> generatedClasses);

}
