package al.franzis.lucene.header.serversource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.TransformerConfigurationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.dcm4che2.data.Tag;

import al.franzis.lucence.header.extract.ExtDcm2Xml;


public class FetchAndExtractTest  extends TestCase
{
	private static Logger LOGGER = Logger.getLogger(FetchAndExtractTest.class);
	
    public FetchAndExtractTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( FetchAndExtractTest.class );
    }

    public void testStudyFetchingAndExtraction() throws FileNotFoundException
    {
    	OutputStreamFactory<?> streamFactory = new PipeStreamFactory();
    	
    	StudyUIDRetriever retriever = new StudyUIDRetriever();
		retriever.setRemoteAE("DCM4CHEE@localhost:11112");
		retriever.setMatchingKeys( new String[] { "PatientName", "Hörmandinger, Karl" } );
		retriever.setStoreTCs( new String[] { "MR" } );
		retriever.fetch((OutputStreamFactory<OutputStream>)streamFactory);
    }
    
    
    public static class PipeStreamFactory implements OutputStreamFactory<ByteArrayOutputStream> {
		
		public ByteArrayOutputStream getOutputStream(String instanceUID) {
			ByteArrayOutputStream out = new ByteArrayOutputStream(20480);
			return out;
		}
		
		public void close( String instanceUID, ByteArrayOutputStream outputStream ) {
			ByteArrayInputStream in = new ByteArrayInputStream( outputStream.toByteArray() );
			extract(in, instanceUID, "/home/alex/dev/tmp7");
		}
		
		private void extract(InputStream inputStream, String sopInstanceUID, String targetDir )
		{
			try {
				File outputFile = new File( targetDir, sopInstanceUID + ".xml");
				FileWriter targetWriter = new FileWriter(outputFile);
				
				ExtDcm2Xml dcm2xml = new ExtDcm2Xml();
		        dcm2xml.setExclude(new int[] {Tag.PixelData});
		        dcm2xml.setComments(true);
		        dcm2xml.setIndent(true);
		        dcm2xml.convert(inputStream, targetWriter);
			} catch (IOException e) {
				LOGGER.error(e);
			} catch (TransformerConfigurationException e) {
				LOGGER.error(e);
			}
		}
		
    }
}
