package al.franzis.osgi.weaving.core.equinox.hooks;

import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleFile;
import org.eclipse.osgi.baseadaptor.hooks.BundleFileWrapperFactoryHook;
import org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook;
import org.eclipse.osgi.baseadaptor.hooks.ClassLoadingStatsHook;
import org.eclipse.osgi.baseadaptor.loader.BaseClassLoader;
import org.eclipse.osgi.baseadaptor.loader.ClasspathEntry;
import org.eclipse.osgi.baseadaptor.loader.ClasspathManager;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;

import al.franzis.osgi.weaving.core.equinox.EquinoxWeaver;
import al.franzis.osgi.weaving.core.equinox.adaptors.EquinoxWeavingAdaptor;
import al.franzis.osgi.weaving.core.equinox.adaptors.EquinoxWeavingAdaptorFactory;
import al.franzis.osgi.weaving.core.equinox.adaptors.IEquinoxWeavingAdaptor;

public class EquinoxWeavingHook implements ClassLoadingHook, 
	ClassLoadingStatsHook, BundleFileWrapperFactoryHook, HookConfigurator, 
	IEquinoxWeavingAdaptorProvider
{
	private final EquinoxWeavingAdaptorFactory adaptorFactory;
	private final Map<Long, IEquinoxWeavingAdaptor> adaptors;
	
	
	public EquinoxWeavingHook() {
		System.out.println("Equinox Weaving Hook created");
		adaptorFactory = new EquinoxWeavingAdaptorFactory();
		adaptors = new HashMap<Long, IEquinoxWeavingAdaptor>();
	}

    @Override
    public byte[] processClass( String name, byte[] classbytes, ClasspathEntry classpathEntry, BundleEntry entry, ClasspathManager manager ) {
//    	System.out.println("Start: Equinox weaving hook called on loading " + name);
    	EquinoxWeaver weaver = EquinoxWeaver.getWeaver();
    	byte[] wovenBytecode = weaver.weave(name, classbytes, classpathEntry, entry, (ClassLoader)manager.getBaseClassLoader());
//    	System.out.println("End: Equinox weaving hook called on loading " + name);
    	return wovenBytecode;
    }

    @Override
    public String findLibrary( BaseData data, String libName )
    {
        return null;
    }

    @Override
    public ClassLoader getBundleClassLoaderParent()
    {
        return null;
    }

    @Override
    public BaseClassLoader createClassLoader( ClassLoader parent, ClassLoaderDelegate delegate, BundleProtectionDomain domain, BaseData data, String[] bundleclasspath )
    {
        return null;
    }

    @Override
    public void initializedClassLoader( BaseClassLoader baseClassLoader, BaseData data )
    {
    	 IEquinoxWeavingAdaptor adaptor = createWeavingAdaptor(data);
         adaptor.setBaseClassLoader(baseClassLoader);
         adaptor.initialize();
         this.adaptors.put(data.getBundleID(), adaptor);
    }

    @Override
    public void addHooks( HookRegistry hookRegistry )
    {
        hookRegistry.addClassLoadingHook(this);
        hookRegistry.addClassLoadingStatsHook(this);
        hookRegistry.addBundleFileWrapperFactoryHook(this);
    }

	@Override
	// OSGI_LEGACY: Method contained in ClassLoadingHook interface of bundle 'org.eclipse.osgi_3.4.2' does 
	// not support version of cpEntries ArrayList with generics
	public boolean addClassPathEntry(@SuppressWarnings("rawtypes") ArrayList cpEntries, String cp,
			ClasspathManager hostmanager, BaseData sourcedata,
			ProtectionDomain sourcedomain) {
		return false;
	}

	@Override
	public void postFindLocalResource(String arg0, URL arg1,
			ClasspathManager arg2) {
	}

	@Override
	public void preFindLocalClass(String arg0, ClasspathManager arg1)
			throws ClassNotFoundException {
	}

	@Override
	public void preFindLocalResource(String arg0, ClasspathManager arg1) {
	}

	@Override
	// OSGI_LEGACY: Method contained in ClassLoadingHook interface of bundle 'org.eclipse.osgi_3.4.2' does 
	// not support version of cpEntries ArrayList with generics
	public void recordClassDefine(String name,
			@SuppressWarnings("rawtypes") Class clazz, byte[] classbytes,
			ClasspathEntry classpathEntry, BundleEntry entry,
			ClasspathManager manager) {
		if (entry instanceof EquinoxWeavingBundleEntry) {
			EquinoxWeavingBundleEntry ajBundleEntry = (EquinoxWeavingBundleEntry) entry;
			if (!ajBundleEntry.dontWeave()) {
				IEquinoxWeavingAdaptor adaptor = ajBundleEntry.getAdaptor();
				URL sourceFileURL = ajBundleEntry.getBundleFileURL();
				adaptor.storeClass(name, sourceFileURL, clazz, classbytes);
			}
		}
	}

	@Override
	// OSGI_LEGACY: Method contained in ClassLoadingHook interface of bundle 'org.eclipse.osgi_3.4.2' does 
	// not support version of cpEntries ArrayList with generics
	public void postFindLocalClass(String name, @SuppressWarnings("rawtypes") Class clazz,
			ClasspathManager manager) throws ClassNotFoundException {
	}

	@Override
	public BundleFile wrapBundleFile(BundleFile bundleFile, Object content,
			BaseData data, boolean base) throws IOException {
		BundleFile wrapped = null;
		if (base) {
            wrapped = new BaseEquinoxWeavingBundleFile(new EquinoxBundleAdaptorProvider(data,
                    this), bundleFile);
        } else {
            wrapped = new EquinoxWeavingBundleFile(new EquinoxBundleAdaptorProvider(data,
                    this), bundleFile);
        }
		return wrapped;
	}

	@Override
	public IEquinoxWeavingAdaptor getAdaptor(long bundleID) {
		return null;
	}

	@Override
	public IEquinoxWeavingAdaptor getHostBundleAdaptor(long bundleID) {
		return null;
	}

	@Override
	public void resetAdaptor(long bundleID) {
	}
	
	private IEquinoxWeavingAdaptor createWeavingAdaptor(BaseData baseData) {
        IEquinoxWeavingAdaptor adaptor = null;

        if (adaptorFactory != null) {
            adaptor = new EquinoxWeavingAdaptor(baseData, adaptorFactory, null, null,
                    null);
        } else {
        }

        return adaptor;
    }
	
}
