package al.franzis.akka.tutorial.typedactors;

import al.franzis.akka.tutorial.messages.Calculate;
import al.franzis.akka.tutorial.messages.Result;

public interface IMaster {
	
	public void triggerCalculation(Calculate calculate);
	
	public void receiveResult(Result result);
}
