package al.franzis.osgi.weaving;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;

import al.franzis.osgi.weaving.matching.Matcher;

public class EclipseMethodHandlerProvider extends MethodHandlerProvider {
	private static final String METHOD_HANDLER_PROVIDER_EXTENSION_POINT = "al.franzis.osgi.weaving.MethodInvocationHandlerProvider";
	
	private IMethodInvocationHandler[] handlers;
	
	public EclipseMethodHandlerProvider() {
		parseExtensions();
	}
	
	
	@Override
	public IMethodInvocationHandler getHandler(int index) {
		return handlers[0];
	}
	
	private void parseExtensions() {
		try {
			IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(METHOD_HANDLER_PROVIDER_EXTENSION_POINT).getExtensions();
			
			handlers = new IMethodInvocationHandler[extensions.length];
			
			int i = 0;
			for(IExtension extension : extensions) {
				System.out.println(extensions);
				
				for(IConfigurationElement ce : extension.getConfigurationElements()) {
					IMethodInvocationHandler handler = (IMethodInvocationHandler)ce.createExecutableExtension("class");
					Matcher classMatcher = (Matcher)ce.createExecutableExtension("classMatcher");
					Matcher methodMatcher = (Matcher)ce.createExecutableExtension("methodMatcher");
					handlers[i++] = handler;
				}
			}
		} catch (InvalidRegistryObjectException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
