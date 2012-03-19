package al.franzis.akka.tutorial.actors;

import akka.actor.UntypedActor;
import al.franzis.akka.tutorial.messages.Result;
import al.franzis.akka.tutorial.messages.Work;

/**
 * Worker Implementation.
 * @author alex
 *
 */
public class Worker extends UntypedActor {

	public void onReceive(Object message) {
		// switch on message type is quite ugly
		if (message instanceof Work) {
			Work work = (Work) message;

			// perform the work
			double result = calculatePiFor(work.getStart(),
					work.getNrOfElements());

			// reply to the sender with the result
			getContext().reply(new Result(result));

		} else
			throw new IllegalArgumentException("Unknown message [" + message + "]");
	}
	
	private double calculatePiFor(int start, int nrOfElements) {
	  double acc = 0.0;
	  for (int i = start * nrOfElements; i <= ((start + 1) * nrOfElements - 1); i++) {
	    acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
	  }
	  return acc;
	}
	
	  
}
