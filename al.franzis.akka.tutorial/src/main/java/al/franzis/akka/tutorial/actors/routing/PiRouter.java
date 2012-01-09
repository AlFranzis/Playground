package al.franzis.akka.tutorial.actors.routing;

import static java.util.Arrays.asList;
import akka.actor.ActorRef;
import akka.actor.TypedActor;
import akka.routing.CyclicIterator;
import akka.routing.InfiniteIterator;
import akka.routing.UntypedLoadBalancer;
import al.franzis.akka.tutorial.typedactors.IWorker;

public class PiRouter extends UntypedLoadBalancer {
	private final InfiniteIterator<ActorRef> workers;

	public PiRouter(ActorRef[] workers) {
		this.workers = new CyclicIterator<ActorRef>(asList(workers));
	}
	
	public PiRouter(IWorker[] workers) {
		
		ActorRef[] actorRefs = new ActorRef[workers.length];
		for( int i = 0; i < workers.length; i++ ) {
			actorRefs[i] = TypedActor.actorFor(workers[i]).get();
		}
		this.workers = new CyclicIterator<ActorRef>(asList(actorRefs));
	}

	public InfiniteIterator<ActorRef> seq() {
		return workers;
	}
}
