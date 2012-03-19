package al.franzis.osgi.weaving.test.b;

import al.franzis.osgi.weaving.test.a.ClassA;
import al.franzis.osgi.weaving.test.a.Profile;

@Profile
public class ClassC {
	
	@Profile
	public ClassD foobar(ClassA A) {
		System.out.println("ClassC.foobar(ClassA) called");
		return new ClassD();
	}
}
