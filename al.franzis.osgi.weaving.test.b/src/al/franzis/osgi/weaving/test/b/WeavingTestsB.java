package al.franzis.osgi.weaving.test.b;

import org.junit.Test;

import al.franzis.osgi.weaving.test.a.ClassA;
import al.franzis.osgi.weaving.test.a.Foo;

public class WeavingTestsB {
	@Test
	public void testWeaving() {
		int RUNS = 1;
		for ( int i = 0; i < RUNS; i++ )
		{
			Foo foo = new Foo();
			foo.publicFoobar(new ClassA());
//			foo.protectedFoobar("Alex", 1000);
			Foo.staticFoobar( "Alex" );
			
			ClassC classC = new ClassC();
			classC.foobar(new ClassA());
			
		}
	}
}
