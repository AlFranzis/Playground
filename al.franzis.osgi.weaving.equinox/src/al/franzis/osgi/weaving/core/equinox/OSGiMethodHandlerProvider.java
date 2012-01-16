package al.franzis.osgi.weaving.core.equinox;

import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;
import al.franzis.osgi.weaving.core.equinox.matching.Matcher;

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

	
	public void addHandlerDefinition( Matcher<CtClass> classMatcher, Matcher<CtMethod> methodMatcher, IMethodInvocationHandler handler) {
		int i = 0;
		handlerDefinitions.add(new HandlerDefinition<CtClass,CtMethod>(0, i, classMatcher, methodMatcher, handler));
		handlers[i] = handler;
	}
}
