package al.franzis.osgi.weaving.equinox.test.a;

import javassist.CtMethod;
import al.franzis.osgi.weaving.core.equinox.matching.Matcher;

final class AOPEquinoxMethodMatcher implements Matcher<CtMethod> {
	@Override
	public boolean matches(CtMethod ctMethod) {
		return "publicFoobar".equals(ctMethod.getName());
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