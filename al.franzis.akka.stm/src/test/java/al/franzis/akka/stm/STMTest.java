package al.franzis.akka.stm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class STMTest extends TestCase
{
  
    public STMTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( STMTest.class );
    }

   
    public void testSTMCounter()
    {
    	Counter counter = new Counter();
    	
    	int value1 = counter.count();
    	// -> 1
    	int value2 = counter.count();
    	// -> 2
    	
        assertTrue( value1 == 1 );
        assertTrue( value2 == 2 );
    }
    
    public void testSTMCounterWithFailedTransactions()
    {
    	int failedTransactionCount = 0;
    	
    	Counter counter = new Counter();
    	
    	for ( int i = 0; i < 105; i++)
    	{
    		try {
    			int oldValue = counter.getCount();
				int newValue = counter.countWhichFailsIfGreaterThan100();
				
				assertTrue( oldValue < newValue);
    		} catch (IllegalStateException e) {
				int value = counter.getCount();
				assertTrue( value == 100);
				
				failedTransactionCount++;
    		}
    		
    	}   
    	
    	assertTrue(failedTransactionCount == 5);
    }
    
    public void testTransactional()
    {
    	final TransactionalObject transactional = new TransactionalObject();
    	final int RUNS = 100000;
    	
    	Thread thread1 = new Thread( new Runnable() {

			public void run() {
				for ( int i = 0; i < RUNS; i ++ )
					transactional.transactional("Alex");
			}
    		
    	});
    	
    	Thread thread2 = new Thread( new Runnable() {

			public void run() {
				for ( int i = 0; i < RUNS; i ++ )
					transactional.transactional("Stefan");
			}
    		
    	});
    	
    	
    	thread1.start();
    	thread2.start();
    
    }
    
}
