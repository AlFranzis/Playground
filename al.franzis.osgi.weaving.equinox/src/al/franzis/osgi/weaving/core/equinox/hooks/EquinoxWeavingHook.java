package al.franzis.osgi.weaving.core.equinox.hooks;

import java.security.ProtectionDomain;
import java.util.ArrayList;

import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook;
import org.eclipse.osgi.baseadaptor.loader.BaseClassLoader;
import org.eclipse.osgi.baseadaptor.loader.ClasspathEntry;
import org.eclipse.osgi.baseadaptor.loader.ClasspathManager;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;

import al.franzis.osgi.weaving.core.equinox.EquinoxWeaver;

public class EquinoxWeavingHook implements ClassLoadingHook, HookConfigurator
{
	
	public EquinoxWeavingHook() {
		System.out.println("Equinox Weaving Hook created");
	}

    @Override
    public byte[] processClass( String name, byte[] classbytes, ClasspathEntry classpathEntry, BundleEntry entry, ClasspathManager manager ) {
//    	System.out.println("Start: Equinox weaving hook called on loading " + name);
    	EquinoxWeaver weaver = EquinoxWeaver.getWeaver();
    	byte[] wovenBytecode = weaver.weave(name, classbytes, (ClassLoader)manager.getBaseClassLoader());
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
        // empty
    }

    @Override
    public void addHooks( HookRegistry hookRegistry )
    {
        hookRegistry.addClassLoadingHook(new EquinoxWeavingHook());
    }

	@Override
	public boolean addClassPathEntry(ArrayList<ClasspathEntry> arg0,
			String arg1, ClasspathManager arg2, BaseData arg3,
			ProtectionDomain arg4) {
		return false;
	}
}
