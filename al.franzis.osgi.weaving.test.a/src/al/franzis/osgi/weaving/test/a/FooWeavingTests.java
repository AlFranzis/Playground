package al.franzis.osgi.weaving.test.a;

import org.junit.Test;



public class FooWeavingTests {

	@Test
	public void testWeaving() {
		int RUNS = 1;
		for ( int i = 0; i < RUNS; i++ )
		{
			Foo foo = new Foo();
			foo.publicFoobar(new ClassA());
//			foo.protectedFoobar("Alex", 1000);
			Foo.staticFoobar( "Alex" );
			
		}
	}
}
