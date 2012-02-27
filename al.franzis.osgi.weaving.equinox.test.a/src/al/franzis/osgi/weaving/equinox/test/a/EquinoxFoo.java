package al.franzis.osgi.weaving.equinox.test.a;

@EquinoxProfile
public class EquinoxFoo {

	@EquinoxProfile
	public void publicFoobar() {
		System.out.println("EquinoxFoo.publicFoobar() called");
	}
	
}
