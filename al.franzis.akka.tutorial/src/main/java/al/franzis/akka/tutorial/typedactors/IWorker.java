package al.franzis.akka.tutorial.typedactors;

import al.franzis.akka.tutorial.messages.Result;
import al.franzis.akka.tutorial.messages.Work;

public interface IWorker {
	
	/**
	 * Executes a unit of work in a SYNCHRONOUS (blocking) manner.
	 * @param work Work unit to be done.
	 * @return Returns the result of the work done.
	 */
	public Result executeWorkSynchronous(Work work);
	
	/**
	 * Schedules a working unit in an ASYNCHRONOUS manner.
	 * When the work has finished the result is sent to the sender
	 * in a message.
	 * @param work Work unit to be done.
	 */
	public void scheduleWorkAsynchronous(Work work);

}
