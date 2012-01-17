This is an OSGi platform extension bundle to intercept classloading of the container.
To activate this bundle:
1) Copy bundle-JAR into the same directory as the org.eclipse.osgi bundle
Necessary as it does not work out of Eclipse workspace.
2) Open config.ini in configuration directory of target platform:
2a) Add bundle (bundlename) to property osgi.framework.extensions
2b) Add hook implementations classes to property osgi.hook.configurators.include

NOTE: Multiple hooks are separated using a comma.

FOR EXAMPLE:
# Sample config.ini 
osgi.framework.extensions=reference\:file\:al.franzis.osgi.weaving.equinox.jar
osgi.hook.configurators.include=al.franzis.osgi.weaving.core.equinox.hooks.EquinoxWeavingHook,al.franzis.osgi.weaving.core.equinox.hooks.EquinoxAdaptorHook



For additional information:
http://wiki.eclipse.org/Adaptor_Hooks  