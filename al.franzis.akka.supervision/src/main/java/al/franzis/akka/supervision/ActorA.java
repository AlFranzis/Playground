package al.franzis.akka.supervision;

import akka.actor.UntypedActor;

public class ActorA extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof Message) {
			System.out.println(String.format("ActorA[%s] received message: %s", hashCode(), ((Message) message).getMessage()));
		}
		else if( message instanceof CrashMessage) {
			// let it crash by throwing an exception
			System.out.println(String.format("ActorA[%s] crashes", hashCode()));
			throw new IllegalArgumentException();
		}
		
	}
	
	@Override
	public void preStart() {
		System.out.println(String.format("ActorA[%s] starts", hashCode()));
	}

}
