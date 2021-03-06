package al.franzis.osgi.weaving.equinox.test.a;

import javassist.CtClass;
import al.franzis.osgi.weaving.core.equinox.matching.Matcher;

public class AOPEquinoxClassMatcher implements Matcher<CtClass> {
	
	@Override
	public boolean matches(CtClass ctClass) {
		return ctClass.hasAnnotation(EquinoxProfile.class);
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