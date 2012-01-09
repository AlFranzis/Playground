package al.franzis.akka.stm;

import akka.stm.Atomic;
import akka.stm.Ref;
import akka.stm.StmUtils;

public class TransactionalObject {
	Ref<String> stringRef = new Ref<String>();
	
	public Object transactional(final String s) {
		return new Atomic<Object>() {
			public Object atomically() {
				StmUtils.deferred(new Runnable() {
					public void run() {
						// executes when transaction commits
//						System.out.println("Committed");
					}
				});
				
				StmUtils.compensating(new Runnable() {
					public void run() {
						// executes when transaction aborts
						System.out.println("Aborted");
					}
				});
				
				String oldValue = stringRef.get();
				if ( oldValue != null )
					stringRef.set(oldValue.substring(0));
				else
					stringRef.set(s);
				return s;
			}
		}.execute();
	}
}
