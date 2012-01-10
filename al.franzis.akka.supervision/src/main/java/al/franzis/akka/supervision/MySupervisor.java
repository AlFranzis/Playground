package al.franzis.akka.supervision;

import java.io.IOException;

import akka.actor.UntypedActor;
import akka.config.Supervision;

class MySupervisor extends UntypedActor {
	  public MySupervisor() {
	    getContext().setFaultHandler(new Supervision.AllForOneStrategy(new Class[]{Exception.class, IOException.class}, 3, 1000));
	  }

	  public void onReceive(Object message) throws Exception {
//	    if (message instanceof Register) {
//	      Register event = (Register)message;
//	      UntypedActorRef actor = event.getActor();
//	      context.link(actor);
//	    } else throw new IllegalArgumentException("Unknown message: " + message);
	  }
	}
