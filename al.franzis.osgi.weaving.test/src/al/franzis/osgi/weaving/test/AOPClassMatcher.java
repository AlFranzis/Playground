package al.franzis.osgi.weaving.test;

import javassist.CtClass;
import al.franzis.osgi.weaving.core.matching.Matcher;

public class AOPClassMatcher implements Matcher<CtClass> {

	@Override
	public boolean matches(CtClass clazz) {
		return "al.franzis.osgi.weaving.test.Foo".equals(clazz.getName());
	}

	@Override
	public Matcher<CtClass> and(Matcher<? super CtClass> other) {
		return null;
	}

	@Override
	public Matcher<CtClass> or(Matcher<? super CtClass> other) {
		return null;
	}

}
