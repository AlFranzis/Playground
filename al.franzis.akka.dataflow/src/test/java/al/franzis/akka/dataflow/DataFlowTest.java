package al.franzis.akka.dataflow;

import java.util.concurrent.Callable;

import org.junit.Test;

import junit.framework.Assert;

import akka.dispatch.Future;
import akka.dispatch.Futures;
import akka.dispatch.MessageDispatcher;

public class DataFlowTest {

	@Test
	public void testSimpleDataflow() {
		
		final Future<Integer> x = Futures.future(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				Integer xValue = 20;
				return xValue;
			}
		});
		
		final Future<Integer> y = Futures.future(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				Integer yValue = 40;
				return yValue;
			}
		});
		
		Future<Integer> z = Futures.future(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				Integer zValue = x.get() + y.get();
				return zValue;
			}
		});
		
		Integer zValue = z.get();
		Assert.assertEquals((int)60, (int)zValue);
		

	}
	
	@Test
	public void testFutureTrigger() {
		
		Futures.future(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("Executed 1 by " + Thread.currentThread().getName());
				Integer xValue = 20;
				return xValue;
			}
		});
		
		Futures.future(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("Executed 2 by " + Thread.currentThread().getName());
				Integer xValue = 20;
				return xValue;
			}
		});
		
	}

}
