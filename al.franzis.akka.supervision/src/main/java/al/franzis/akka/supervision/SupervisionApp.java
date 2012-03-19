package al.franzis.akka.supervision;

import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.Supervisor;
import akka.actor.SupervisorFactory;
import akka.config.Supervision;
import akka.config.Supervision.SupervisorConfig;
import akka.config.Supervision.OneForOneStrategy;
import akka.config.Supervision.Supervise;
import static akka.config.Supervision.permanent;


public class SupervisionApp 
{
    public static void main( String[] args )
    {
    	ActorRef actorARef =  Actors.actorOf(ActorA.class);
    	
    	SupervisorFactory factory = new SupervisorFactory(
    			  new SupervisorConfig(
    			    new OneForOneStrategy(new Class[]{IllegalArgumentException.class}, 3, 5000),
    			    new Supervise[] {
    			      new Supervise(
    			        actorARef,
    			        permanent())
    			   }));
    	
    	Supervisor supervisor = factory.newInstance();
    	supervisor.start();
    	
    	actorARef.tell(new Message("message1 before crash"));
    	actorARef.tell(new Message("message2 before crash"));
    	
    	// let actorA crash
    	actorARef.tell(new CrashMessage());
    	
    	actorARef.tell(new Message("message3 after crash"));

    	
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
    	
    	supervisor.shutdown();
    }
}
