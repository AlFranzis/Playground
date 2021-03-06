package al.franzis.akka.tutorial.actors;

import static akka.actor.Actors.actorOf;
import static akka.actor.Actors.poisonPill;

import java.util.concurrent.CountDownLatch;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.Routing.Broadcast;
import al.franzis.akka.tutorial.actors.routing.PiRouter;
import al.franzis.akka.tutorial.messages.Calculate;
import al.franzis.akka.tutorial.messages.Result;
import al.franzis.akka.tutorial.messages.Work;

public class Master extends UntypedActor {
	private final int nrOfMessages;
	private final int nrOfElements;
	private final CountDownLatch latch;

	private double pi;
	private int nrOfResults;
	private long start;

	private ActorRef router;

	public Master(int nrOfWorkers, int nrOfMessages, int nrOfElements, CountDownLatch latch) {
		this.nrOfMessages = nrOfMessages;
		this.nrOfElements = nrOfElements;
		this.latch = latch;

		// create the worker actors and start them
		final ActorRef[] workers = new ActorRef[nrOfWorkers];
		for (int i = 0; i < nrOfWorkers; i++) {
			workers[i] = actorOf(Worker.class).start();
		}

		// wrap them with a load-balancing router
		router = actorOf(new UntypedActorFactory() {
			public UntypedActor create() {
				return new PiRouter(workers);
			}
		}).start();
	}

	public void onReceive(Object message) {
		if (message instanceof Calculate) {
			// schedule work
			for (int start = 0; start < nrOfMessages; start++) {
				router.tell(new Work(start, nrOfElements), getContext());
			}

			// send a PoisonPill to all workers telling them to shut down
			// themselves
			router.tell(new Broadcast(poisonPill()));

			// send a PoisonPill to the router, telling him to shut himself down
			router.tell(poisonPill());
		} else if (message instanceof Result) {

			// handle result from the worker
			Result result = (Result) message;
			pi += result.getValue();
			nrOfResults += 1;
			if (nrOfResults == nrOfMessages)
				getContext().stop();
		} else
			throw new IllegalArgumentException("Unknown message [" + message + "]");
	}

	@Override
	public void preStart() {
		start = System.currentTimeMillis();
	}

	@Override
	public void postStop() {
		// tell the world that the calculation is complete
		System.out.println(String.format(
				"\n\tPi estimate: \t\t%s\n\tCalculation time: \t%s millis", pi,
				(System.currentTimeMillis() - start)));
		
		latch.countDown();
	}

}
