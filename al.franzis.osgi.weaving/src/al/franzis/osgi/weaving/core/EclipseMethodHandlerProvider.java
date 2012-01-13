package al.franzis.osgi.weaving.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;

import al.franzis.osgi.weaving.core.matching.Matcher;

public class EclipseMethodHandlerProvider extends MethodHandlerProvider {
	private static final String METHOD_HANDLER_PROVIDER_EXTENSION_POINT = "al.franzis.osgi.weaving.MethodInvocationHandlerProvider";
	
	private List<HandlerDefinition<CtClass,CtMethod>> handlerDefinitions;
	private IMethodInvocationHandler[] handlers;
	
	public EclipseMethodHandlerProvider() {
		parseExtensions();
	}
	
	
	@Override
	public IMethodInvocationHandler getHandler(int index) {
		return handlers[0];
	}
	
	public List<HandlerDefinition<CtClass,CtMethod>> getHandlerDefinitions() {
		return handlerDefinitions;
	}

	@Override
	public Integer[] getMatchingHandlersForClass(CtClass ctClass) {
		if ( handlerDefinitions == null )
			return null;
		
		List<Integer> matchingHandlerIndices = null; 
		for( HandlerDefinition<CtClass,CtMethod> handlerDefinition : handlerDefinitions) {
			
			if (handlerDefinition.classMatcher.matches(ctClass) ) {
				if ( matchingHandlerIndices == null )
					matchingHandlerIndices = new ArrayList<Integer>();
				
				matchingHandlerIndices.add(handlerDefinition.getIndex());
			}
		}
		
		return matchingHandlerIndices.toArray(new Integer[0]);
	}
	
	private void parseExtensions() {
		try {
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(METHOD_HANDLER_PROVIDER_EXTENSION_POINT);
			IExtension[] extensions = extensionPoint.getExtensions();
			
			handlerDefinitions = new ArrayList<HandlerDefinition<CtClass,CtMethod>>(extensions.length);
			handlers = new IMethodInvocationHandler[extensions.length];
			
			int i = 0;
			for(IExtension extension : extensions) {
				for(IConfigurationElement ce : extension.getConfigurationElements()) {
					Matcher classMatcher = (Matcher)ce.createExecutableExtension("classMatcher");
					Matcher methodMatcher = (Matcher)ce.createExecutableExtension("methodMatcher");
					IMethodInvocationHandler handler = (IMethodInvocationHandler)ce.createExecutableExtension("class");
					handlerDefinitions.add(new HandlerDefinition(0, i, classMatcher, methodMatcher, handler));
					handlers[i++] = handler;
				}
			}
			
			Collections.sort(handlerDefinitions, new Comparator<HandlerDefinition<?,?>>() {
				@Override
				public int compare(HandlerDefinition<?,?> def1, HandlerDefinition<?,?> def2) {
					return def1.getOrder() - def2.getOrder();
				}
			});
		} catch (InvalidRegistryObjectException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
