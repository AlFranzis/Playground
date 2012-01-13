package al.franzis.osgi.weaving.test;



public class Foo {
	
	@Profile
	public int publicFoobar(String s) {
		System.out.println("publicFoobar() called");
		return 0;
	}
	
	@Profile
	public static String staticFoobar( String s) {
		System.out.println( String.format("staticFoobar(%s) called", s));
		return "string";
	}
	
	@Profile
	private void privateFoobar( String s, int i) {
		System.out.println( String.format("privateFoobar(%s,%s) called", s, i));
	}
	
	@Profile
	protected void protectedFoobar( String s, int i) {
		System.out.println( String.format("protectedFoobar(%s,%s) called", s, i));
//		privateFoobar(s, i);
	}
	
}
