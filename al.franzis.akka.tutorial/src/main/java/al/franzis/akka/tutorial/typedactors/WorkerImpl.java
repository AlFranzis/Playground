package al.franzis.akka.tutorial.typedactors;

import akka.actor.TypedActor;
import al.franzis.akka.tutorial.messages.Result;
import al.franzis.akka.tutorial.messages.Work;

public class WorkerImpl extends TypedActor implements IWorker{

	@Override
	public Result doWork(Work work) {
		// perform the work
		double result = calculatePiFor(work.getStart(), work.getNrOfElements());
		return new Result(result);
	}
	
	private double calculatePiFor(int start, int nrOfElements) {
		double acc = 0.0;
		for (int i = start * nrOfElements; i <= ((start + 1) * nrOfElements - 1); i++) {
			acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
		}
		return acc;
	}

}
