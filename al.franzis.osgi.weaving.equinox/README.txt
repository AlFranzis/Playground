This is an OSGi platform extension bundle to intercept classloading of the container.
To activate this bundle:
1) Copy bundle-JAR into the same directory as the org.eclipse.osgi bundle
Necessary as it does not work out of Eclipse workspace.
2) Open config.ini in configuration directory of target platform:
2a) Add bundle (bundlename) to property osgi.framework.extensions
2b) Add hook implementationc classes to property osgi.hook.configurators.include

For additional information:
http://wiki.eclipse.org/Adaptor_Hooks  