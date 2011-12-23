package al.franzis.osgi.weaving;

public class Foo {
	
	public void foobar() {
		System.out.println("foobar() called");
	}
	
	@Profile
	public static void foobar2() {
		System.out.println("foobar2() called");
	}
	
}
