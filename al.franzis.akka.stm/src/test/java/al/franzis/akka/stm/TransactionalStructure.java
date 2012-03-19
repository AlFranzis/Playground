package al.franzis.akka.stm;

import akka.stm.Atomic;
import akka.stm.TransactionalMap;

public class TransactionalStructure {
	public final TransactionalMap<String,String> map = 
			new TransactionalMap<String,String>();


	public void transactionalPut() {
		// fill users map (in a transaction)
		new Atomic() {
		    public Object atomically() {
		    	map.put("1", Thread.currentThread().getName());
		        return null;
		    }
		}.execute();
	}
	
	public void testConcurrentFill() {
		final int RUNS = 100000;

		Thread thread1 = new Thread(new Runnable() {

			public void run() {
				for (int i = 0; i < RUNS; i++)
					transactionalPut();
			}

		});

		Thread thread2 = new Thread(new Runnable() {

			public void run() {
				for (int i = 0; i < RUNS; i++)
					transactionalPut();
			}

		});

		thread1.start();
		thread2.start();
	}

}
