package al.franzis.osgi.weaving.core.equinox;

import java.util.ArrayList;
import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;

public class OSGiMethodHandlerProvider extends MethodHandlerProvider {
	
	private List<HandlerDefinition<CtClass,CtMethod>> handlerDefinitions;
	private IMethodInvocationHandler[] handlers;
	
	public OSGiMethodHandlerProvider() {
		
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
	
}
