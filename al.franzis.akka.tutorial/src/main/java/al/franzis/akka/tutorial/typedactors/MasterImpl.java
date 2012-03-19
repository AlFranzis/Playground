package al.franzis.akka.tutorial.typedactors;

import static akka.actor.Actors.actorOf;

import java.util.concurrent.CountDownLatch;

import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.TypedActor;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.Routing.Broadcast;
import al.franzis.akka.tutorial.actors.routing.PiRouter;
import al.franzis.akka.tutorial.messages.Calculate;
import al.franzis.akka.tutorial.messages.Result;
import al.franzis.akka.tutorial.messages.Work;

public class MasterImpl extends TypedActor implements IMaster {
	private final int nrOfMessages;
	private final int nrOfElements;
	private final int nrOfWorkers;
	private final CountDownLatch latch;

	private double pi;
	private int nrOfResults;
	private long start;

	private IWorker[] workers;

	private ActorRef router;

	public MasterImpl(int nrOfWorkers, int nrOfMessages, int nrOfElements,
			CountDownLatch latch) {
		this.nrOfMessages = nrOfMessages;
		this.nrOfElements = nrOfElements;
		this.nrOfWorkers = nrOfWorkers;
		this.latch = latch;

		// create the worker actors and start them
		workers = new IWorker[nrOfWorkers];
		for (int i = 0; i < nrOfWorkers; i++) {
			IWorker worker = (IWorker) TypedActor.newInstance(IWorker.class,
					WorkerImpl.class);
			workers[i] = worker;
		}

		// wrap them with a load-balancing router
		router = actorOf(new UntypedActorFactory() {
			public UntypedActor create() {
				return new PiRouter(workers);
			}
		}).start();

	}

	@Override
	public void triggerSyncCalculation(Calculate calculate) {
		// schedule work
		for (int start = 0; start < nrOfMessages; start++) {
			IWorker worker = workers[start % nrOfWorkers];
			
			Work work = new Work(start, nrOfElements);
			Result result = worker.executeWorkSynchronous(work);

			pi += result.getValue();
			nrOfResults += 1;
			
			// after sending messages to worker
			// -> stop master
			if (nrOfResults == nrOfMessages)
				getContext().actorRef().stop();
		}

		// send a PoisonPill to all workers telling them to shut down
		// themselves
		router.tell(new Broadcast(Actors.poisonPill()));

		// send a PoisonPill to the router, telling him to shut himself down
		router.tell(Actors.poisonPill());

		
	}
	
	@Override
	public void triggerAsyncCalculation(Calculate calculate) {
		// schedule work
		for (int start = 0; start < nrOfMessages; start++) {
			IWorker worker = workers[start % nrOfWorkers];
			
			Work work = new Work(start, nrOfElements);
			worker.scheduleWorkAsynchronous(work);
		}

		// send a PoisonPill to all workers telling them to shut down
		// themselves
		router.tell(new Broadcast(Actors.poisonPill()));

		// send a PoisonPill to the router, telling him to shut himself down
		router.tell(Actors.poisonPill());

		
	}

	@Override
	public void receiveResult(Result result) {
		// handle result from the worker
		pi += result.getValue();
		nrOfResults += 1;
		if (nrOfResults == nrOfMessages)
			getContext().actorRef().stop();
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
