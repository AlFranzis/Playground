package al.franzis.akka.stm;

import akka.stm.Atomic;
import akka.stm.Ref;

public class Counter {
	final Ref<Integer> ref = new Ref<Integer>(0);

	public int count() {
		return new Atomic<Integer>() {
			public Integer atomically() {
				int inc = ref.get() + 1;
				ref.set(inc);
				return inc;
			}
		}.execute();
	}
	
	public int countWhichFailsIfGreaterThan100() {
		return new Atomic<Integer>() {
			public Integer atomically() {
				int inc = ref.get() + 1;
				ref.set(inc);
				
				if ( inc > 100 )
					throw new IllegalStateException();
				
				return inc;
			}
		}.execute();
	}
	
	public int getCount() {
		return new Atomic<Integer>() {
			public Integer atomically() {
				return ref.get();
				
			}
		}.execute();
	}
}
