package al.franzis.akka.tutorial.typedactors;

import al.franzis.akka.tutorial.messages.Result;
import al.franzis.akka.tutorial.messages.Work;

public interface IWorker {
	public Result doWork(Work work);
}
