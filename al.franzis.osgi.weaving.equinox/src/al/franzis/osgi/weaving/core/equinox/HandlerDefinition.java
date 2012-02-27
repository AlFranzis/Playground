package al.franzis.osgi.weaving.core.equinox;

import al.franzis.osgi.weaving.core.equinox.matching.Matcher;

public class HandlerDefinition<C,M> {
	private final int order;
	private final int index;
	final Matcher<C> classMatcher;
	private final Matcher<M> methodMatcher;
	private final IMethodInvocationHandler handler;
	
	public HandlerDefinition(int order, int index, Matcher<C> classMatcher,
			Matcher<M> methodMatcher, IMethodInvocationHandler handler) {
		this.order = order;
		this.index = index;
		this.classMatcher = classMatcher;
		this.methodMatcher = methodMatcher;
		this.handler = handler;
	}

	public int getOrder() {
		return order;
	}
	
	public Matcher<C> getClassMatcher() {
		return classMatcher;
	}

	public Matcher<M> getMethodMatcher() {
		return methodMatcher;
	}

	public IMethodInvocationHandler getHandler() {
		return handler;
	}
	
	public int getIndex() {
		return index;
	}
	
}