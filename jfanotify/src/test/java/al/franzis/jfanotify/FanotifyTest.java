package al.franzis.jfanotify;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class FanotifyTest {
	private static final String TEST_DIR = "/tmp/test_fanotify";
	
	@Test
	public void test() {
		Fanotify fanotify = Fanotify.getInstance();
		
		FanotifyMark mark = new FanotifyMark();
		mark.setFilename(TEST_DIR);
		fanotify.addMark(mark);
		
		DummyListener listener = new DummyListener();
		fanotify.addListener(listener, null);
		
		fanotify.start();
		
		try {
			asyncTouchNewFile(TEST_DIR, "test.txt").join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertTrue(listener.hasReceivedEvent());
		
	}
	
	private class DummyListener implements IFanotifyListener {
		private boolean receivedEvent = false;
		
		
		public FanotifyResponse notify(FanotifyEvent event) {
			System.out.println("Received fanotify event");
			Assert.assertNotNull(event);
			receivedEvent = true;
			return null;
		}
		
		public boolean hasReceivedEvent() {
			return receivedEvent;
		}
		
	}
	
	private Thread asyncTouchNewFile(final String dirname, final String filename) {
		Runnable runnable = new Runnable() {

			public void run() {
				if ( !touchNewFile(dirname, filename) )
					System.out.println("error while touching file");
			}
			
		};
		
		Thread t = new Thread(runnable);
		t.start();
		return t;
	}
	
	private static boolean touchNewFile(String dirname, String filename) {
		try {
			File dir = new File(dirname);
			if ( !dir.exists() )
				dir.mkdirs();
			
			File file = new File(dirname, filename);
			if ( file.exists() )
				return false;
			FileWriter writer = new FileWriter(file);
			writer.append("test");
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	

}
