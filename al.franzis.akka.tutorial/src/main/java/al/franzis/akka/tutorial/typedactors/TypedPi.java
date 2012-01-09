package al.franzis.akka.tutorial.typedactors;

import java.util.concurrent.CountDownLatch;

import akka.actor.TypedActor;
import akka.actor.TypedActorFactory;
import al.franzis.akka.tutorial.messages.Calculate;

public class TypedPi {

	/**
	 * Bootstrap application
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		TypedPi pi = new TypedPi();
		pi.calculate(4, 10000, 10000);
	}
	
	public void calculate(final int nrOfWorkers, final int nrOfElements,
			final int nrOfMessages) throws Exception {

		// this latch is only plumbing to know when the calculation is completed
		final CountDownLatch latch = new CountDownLatch(1);

		IMaster master = TypedActor.newInstance(IMaster.class, new TypedActorFactory() {
			  public TypedActor create() {
			    return new MasterImpl(nrOfWorkers, nrOfMessages, nrOfElements, latch);
			  }
			});
		
		// start the calculation
		master.triggerCalculation(new Calculate());

		// wait for master to shut down
		latch.await();
	}
	
	
}
