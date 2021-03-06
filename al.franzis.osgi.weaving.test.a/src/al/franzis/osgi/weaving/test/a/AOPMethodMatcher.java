package al.franzis.osgi.weaving.test.a;

import javassist.CtMethod;
import al.franzis.osgi.weaving.core.matching.Matcher;

public class AOPMethodMatcher implements Matcher<CtMethod> {

	@Override
	public boolean matches(CtMethod method) {
		return method.hasAnnotation(Profile.class);
	}

	@Override
	public Matcher<CtMethod> and(Matcher<? super CtMethod> other) {
		return null;
	}

	@Override
	public Matcher<CtMethod> or(Matcher<? super CtMethod> other) {
		return null;
	}

}
