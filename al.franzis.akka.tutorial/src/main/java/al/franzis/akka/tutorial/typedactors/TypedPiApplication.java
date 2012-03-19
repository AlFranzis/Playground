package al.franzis.akka.tutorial.typedactors;

import java.util.concurrent.CountDownLatch;

import akka.actor.TypedActor;
import akka.actor.TypedActorFactory;
import al.franzis.akka.tutorial.messages.Calculate;

public class TypedPiApplication {

	/**
	 * Bootstrap application
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		TypedPiApplication pi = new TypedPiApplication();
		pi.calculate(4, 10000, 10000);
	}
	
	public void calculate(final int nrOfWorkers, final int nrOfElements,
			final int nrOfMessages) throws Exception {

		// this latch is only plumbing to know when the calculation is completed
		final CountDownLatch latch = new CountDownLatch(1);

		// create a master actor and start it
		IMaster master = TypedActor.newInstance(IMaster.class, new TypedActorFactory() {
			  public TypedActor create() {
			    return new MasterImpl(nrOfWorkers, nrOfMessages, nrOfElements, latch);
			  }
			});
		
		boolean doSynchronous = false;
		if(doSynchronous) {
			// start the synchronous calculation
			master.triggerSyncCalculation(new Calculate());
			
		}
		else {
			// start the synchronous calculation
			master.triggerAsyncCalculation(new Calculate());
		}

		// wait for master to shut down
		latch.await();
	}
	
	
}
