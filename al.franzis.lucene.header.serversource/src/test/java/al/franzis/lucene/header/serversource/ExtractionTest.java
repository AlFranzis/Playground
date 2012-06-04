package al.franzis.lucene.header.serversource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import al.franzis.lucence.header.extract.StreamDcmDump;


public class ExtractionTest  extends TestCase
{
	
    public ExtractionTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( ExtractionTest.class );
    }

    public void testStudyExtraction() throws FileNotFoundException
    {
    	OutputStreamFactory<?> streamFactory = new PipeStreamFactory();
    	
    	StudyUIDRetriever retriever = new StudyUIDRetriever();
		retriever.setRemoteAE("DCM4CHEE@localhost:11112");
		retriever.setMatchingKeys( new String[] { "PatientName", "Hörmandinger, Karl" } );
		retriever.setStoreTCs( new String[] { "MR" } );
		retriever.fetch((OutputStreamFactory<OutputStream>)streamFactory);
    }
    
    
    public static class PipeStreamFactory implements OutputStreamFactory<ByteArrayOutputStream> {

		public PipeStreamFactory() {
	
		}
		
		public ByteArrayOutputStream getOutputStream(String instanceUID) {
			ByteArrayOutputStream out = new ByteArrayOutputStream(20480);
			return out;
		}
		
		public void close( String instanceUID, ByteArrayOutputStream outputStream ) {
			ByteArrayInputStream in = new ByteArrayInputStream( outputStream.toByteArray() );
			File target = new File("/home/alex/dev/tmp7");
			StreamDcmDump.dumpStream(in, target, instanceUID);
		}
		
    }
}
