package al.franzis.lucene.header.serversource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class FetchTest  extends TestCase
{
	
    public FetchTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( FetchTest.class );
    }

    public void testStudyFetching() throws FileNotFoundException
    {
    	OutputStreamFactory streamFactory = new FileOutputStreamFactory();
    	
    	StudyUIDRetriever retriever = new StudyUIDRetriever();
		retriever.setRemoteAE("DCM4CHEE@localhost:11112");
		retriever.setMatchingKeys( new String[] { "PatientName", "Hörmandinger, Karl" } );
		retriever.setStoreTCs( new String[] { "MR" } );
		retriever.fetch(streamFactory);
    }
    
    
    public static class FileOutputStreamFactory implements OutputStreamFactory {
		private static final String OUTPUT_DIR = "/home/alex/dev/tmp5";

		public FileOutputStreamFactory() {
			File f = new File(OUTPUT_DIR);
			if (!f.exists())
				f.mkdirs();
		}
		
		public FileOutputStream getOutputStream(String instanceUID) {
			try {
				File f = new File(OUTPUT_DIR, instanceUID);
				FileOutputStream fos = new FileOutputStream(f);
				return fos;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
    }
}
