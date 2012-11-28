package bundled.test;

import junit.framework.Assert;

import org.junit.Test;

import bundled2.DummyClass;

public class TestDummyClass {
	
	@Test
	public void test1()
	{
		DummyClass dummy1 = new DummyClass();
		String ret = dummy1.foobar(5);
		Assert.assertEquals( "foobar x 5", ret);
	}
}
