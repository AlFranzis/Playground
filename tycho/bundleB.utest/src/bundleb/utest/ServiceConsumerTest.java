package bundleb.utest;

import junit.framework.Assert;

import org.junit.Test;

import bundleb.BundleBServiceConsumer;

public class ServiceConsumerTest {
	
	@Test
	public void testServiceConsumer() {
		BundleBServiceConsumer serviceConsumer = new BundleBServiceConsumer();
		String msg = serviceConsumer.getMessage("Alex");
		Assert.assertNotNull(msg);
	}
}
