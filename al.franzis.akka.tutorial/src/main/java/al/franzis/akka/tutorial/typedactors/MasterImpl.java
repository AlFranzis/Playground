package al.franzis.akka.tutorial.typedactors;

import static java.util.Arrays.asList;

import java.util.concurrent.CountDownLatch;

import akka.actor.ActorRef;
import akka.actor.TypedActor;
import akka.routing.CyclicIterator;
import akka.routing.InfiniteIterator;
import akka.routing.UntypedLoadBalancer;
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
	
	static class PiRouter extends UntypedLoadBalancer {
		private final InfiniteIterator<ActorRef> workers;

		public PiRouter(ActorRef[] workers) {
			this.workers = new CyclicIterator<ActorRef>(asList(workers));
		}

		public InfiniteIterator<ActorRef> seq() {
			return workers;
		}
	}
	
	public MasterImpl(int nrOfWorkers, int nrOfMessages, int nrOfElements, CountDownLatch latch) {
		this.nrOfMessages = nrOfMessages;
		this.nrOfElements = nrOfElements;
		this.nrOfWorkers = nrOfWorkers;
		this.latch = latch;

		// create the workers
		workers = new IWorker[nrOfWorkers];
		for (int i = 0; i < nrOfWorkers; i++) {
			IWorker worker = (IWorker)TypedActor.newInstance(IWorker.class, WorkerImpl.class);
			workers[i] = worker;
		}

	}
	
	@Override
	public void triggerCalculation(Calculate calculate) {
		// schedule work
		for (int start = 0; start < nrOfMessages; start++) {
			IWorker worker = workers[start % nrOfWorkers];
			Result result = worker.doWork(new Work(start, nrOfElements));
			
			pi += result.getValue();
			nrOfResults += 1;
			if (nrOfResults == nrOfMessages)
				TypedActor.stop(worker);
		}
		
		TypedActor.stop(this);
	}

	@Override
	public void receiveResult(Result result) {
		
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
