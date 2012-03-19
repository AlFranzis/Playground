package al.franzis.osgi.weaving.core;

import java.util.LinkedList;
import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;

public class Matcher {
	private HandlerDefinition<CtClass,CtMethod>[] handlerDefinitions;
	private int handlerCount;
	
	public Matcher() {
		handlerDefinitions = MethodHandlerProvider.getInstance().getHandlerDefinitions().toArray(new HandlerDefinition[0]);
		handlerCount = handlerDefinitions.length;
	}
	
	public List<HandlerDefinition<CtClass,CtMethod>> match(CtClass ctClass) {
		
		List<HandlerDefinition<CtClass,CtMethod>> matchingHandlers = null;
		for(int i = 0; i < handlerCount; i++) {
			if (handlerDefinitions[i].getClassMatcher().matches(ctClass)) {
				if( matchingHandlers == null )
					matchingHandlers = new LinkedList<HandlerDefinition<CtClass,CtMethod>>();
				
				matchingHandlers.add(handlerDefinitions[i]);
			}
		}
		
		return matchingHandlers;
	}
}
