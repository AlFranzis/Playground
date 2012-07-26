package al.franzis.osgi.weaving.core.equinox.adaptors;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleFile;
import org.eclipse.osgi.baseadaptor.loader.BaseClassLoader;
import org.eclipse.osgi.framework.internal.core.BundleFragment;
import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.osgi.framework.Bundle;

import al.franzis.osgi.weaving.core.equinox.IEquinoxCachingService;
import al.franzis.osgi.weaving.core.equinox.IEquinoxWeavingService;
import al.franzis.osgi.weaving.core.equinox.WeavingCacheEntry;
import al.franzis.osgi.weaving.core.equinox.hooks.EquinoxWeavingBundleFile;

public class EquinoxWeavingAdaptor implements IEquinoxWeavingAdaptor {

    private static class ThreadLocalSet extends ThreadLocal {

        public boolean contains(Object obj) {
            Set set = (Set) get();
            return set.contains(obj);
        }

        public void put(Object obj) {
            Set set = (Set) get();
            if (set.contains(obj)) {
                throw new RuntimeException(obj.toString());
            }
            set.add(obj);
        }

        public void remove(Object obj) {
            Set set = (Set) get();
            if (!set.contains(obj)) {
                throw new RuntimeException(obj.toString());
            }
            set.remove(obj);
        }

        @Override
        protected Object initialValue() {
            return new HashSet();
        }
    }

    private static ThreadLocalSet identifyRecursionSet = new ThreadLocalSet();

    private BaseClassLoader baseLoader;

    private Bundle bundle;

    private IEquinoxCachingService cachingService;

    private final BaseData data;

    private final EquinoxWeavingAdaptorFactory factory;

    private boolean initialized = false;

    private String symbolicName;

    private IEquinoxWeavingService weavingService;

    public EquinoxWeavingAdaptor(BaseData baseData,
            EquinoxWeavingAdaptorFactory serviceFactory,
            BaseClassLoader baseClassLoader,
            IEquinoxWeavingService weavingService,
            IEquinoxCachingService cachingService) {
        this.data = baseData;
        this.factory = serviceFactory;
        this.symbolicName = baseData.getLocation();
    }

    public WeavingCacheEntry findClass(final String name, final URL sourceFileURL) {
        WeavingCacheEntry cacheEntry = null;

        initialize();
        if (cachingService != null) {
            cacheEntry = cachingService
                    .findStoredClass("", sourceFileURL, name);
        }

        return cacheEntry;
    }

    public void initialize() {
        synchronized (this) {
            if (initialized) return;

            this.bundle = data.getBundle();
            this.symbolicName = data.getSymbolicName();
            if (!identifyRecursionSet.contains(this)) {
                identifyRecursionSet.put(this);

                if (symbolicName.startsWith("org.aspectj")) {
                    
                } else if (baseLoader != null) {
                    weavingService = factory.getWeavingService(baseLoader);
                    cachingService = factory.getCachingService(baseLoader,
                            bundle, weavingService);
                } else if (bundle instanceof BundleFragment) {
                    BundleFragment fragment = (BundleFragment) bundle;
                    BundleHost host = (BundleHost) factory.getHost(fragment);
                    
                    BaseData hostData = (BaseData) host.getBundleData();
                    BundleFile bundleFile = hostData.getBundleFile();
                    if (bundleFile instanceof EquinoxWeavingBundleFile) {
                        EquinoxWeavingBundleFile hostFile = (EquinoxWeavingBundleFile) bundleFile;
                        EquinoxWeavingAdaptor hostAdaptor = (EquinoxWeavingAdaptor) hostFile.getAdaptor();
                        weavingService = hostAdaptor.weavingService;
                        cachingService = factory.getCachingService(
                                hostAdaptor.baseLoader, bundle, weavingService);
                    }
                } else {
                }
                initialized = true;
                identifyRecursionSet.remove(this);
            }

        }
    }

    public void setBaseClassLoader(BaseClassLoader baseClassLoader) {
        this.baseLoader = baseClassLoader;
    }

    public boolean storeClass(String name, URL sourceFileURL,
            Class clazz, byte[] classbytes) {
        boolean stored = false;

        initialize();
        if (cachingService != null) {
            //have we generated a closure? 
            if (weavingService != null
                    && weavingService.generatedClassesExistFor(
                            (ClassLoader) baseLoader, name)) {
                //If so we need to ask the cache if its capable of handling generated closures
                if (cachingService.canCacheGeneratedClasses()) {
                    final Map<String, byte[]> generatedClasses = weavingService
                            .getGeneratedClassesFor(name);

                    stored = cachingService.storeClassAndGeneratedClasses("",
                            sourceFileURL, clazz, classbytes, generatedClasses);
                } else {
                    weavingService.flushGeneratedClasses((ClassLoader) baseLoader);
                }
            } else {
                stored = cachingService.storeClass("", sourceFileURL, clazz,
                        classbytes);
                if (!stored) {
                }
            }
        }
        
        return stored;
    }

    @Override
    public String toString() {
        return "AspectJAdaptor[" + symbolicName + "]";
    }

    public byte[] weaveClass(String name, byte[] bytes) {
        byte[] newBytes = null;

        initialize();
        if (/* shouldWeave(bytes) && */weavingService != null) {
            try {
                newBytes = weavingService.preProcess(name, bytes,
                        (ClassLoader) baseLoader);
            } catch (IOException ex) {
                throw new ClassFormatError(ex.toString());
            }
        }

        return newBytes;
    }

}
