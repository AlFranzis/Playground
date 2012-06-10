package al.franzis.lucence.header.extract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.dcm4che2.data.Tag;

public class ExtractionTest extends TestCase
{
	private static final Logger LOGGER = Logger.getLogger(ExtractionTest.class);
	
    public ExtractionTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( ExtractionTest.class );
    }

	public void testTextExtraction() {

		ExtDcm2Txt dcm2txt = new ExtDcm2Txt();
		dcm2txt.setWithNames(true);
		dcm2txt.setMaxWidth(ExtDcm2Txt.MAX_MAX_WIDTH);
		dcm2txt.setMaxValLen(ExtDcm2Txt.MAX_MAX_VAL_LEN);

		File infile = new File("/home/alex/dev/git-repos/Playground/al.franzis.lucene.header.extract/src/test/resources/MR000001");
		FileInputStream inputStream = null;
		
		File outputFile = new File("/home/alex/dev/git-repos/Playground/al.franzis.lucene.header.extract/src/test/resources/MR000001.txt");
		FileWriter outputWriter = null;
		
		try {
			inputStream = new FileInputStream(infile);
			outputWriter = new FileWriter(outputFile);
			
			dcm2txt.dump(inputStream, outputWriter);
		} catch (IOException e) {
			LOGGER.error("Failed to dump " + infile + ": " + e.getMessage(), e);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (outputWriter != null)
					outputWriter.close();
			} catch (IOException e) {
				LOGGER.error("Error when closing input / output stream ", e);
			}
		}
	}
	
	 public void testXmlExtraction() {
	        ExtDcm2Xml dcm2xml = new ExtDcm2Xml();
	        dcm2xml.setExclude(new int[] {Tag.PixelData});
	        dcm2xml.setComments(true);
	        dcm2xml.setIndent(true);
	        
//	        String xslurl = null;
//	        if (xslurl != null) {
//	            try {
//	                dcm2xml.setXslt(new URL(xslurl));
//	            } catch (MalformedURLException e) {
//	                LOGGER.error("Invalid xsl URL: " + xslurl);
//	                System.exit(1);
//	            }
//	            boolean incremental = false;
//	            dcm2xml.setXsltInc(incremental);
//	            String[] xsltParameters = null;
//	            dcm2xml.setXsltParams(xsltParameters);
//	        }
	        
	        File inputFile = new File("/home/alex/dev/git-repos/Playground/al.franzis.lucene.header.extract/src/test/resources/MR000001");
	        FileInputStream inputStream = null;
	        
	        File outputFile = new File("/home/alex/dev/git-repos/Playground/al.franzis.lucene.header.extract/src/test/resources/MR000001.xml");
	        if ( outputFile != null ) {
	            dcm2xml.setBaseDir(outputFile.getAbsoluteFile().getParentFile());
	        }
//	        else {
//	        	File baseDirFile = new File("");
//	            dcm2xml.setBaseDir(baseDirFile);
//	        }
	        FileWriter outputWriter = null;
	        
	        long t1 = System.currentTimeMillis();
	        try {
	        	inputStream = new FileInputStream(inputFile);
	        	outputWriter = new FileWriter(outputFile);
	            dcm2xml.convert(inputStream, outputWriter);
	        } catch (TransformerConfigurationException e) {
	            LOGGER.error("Configuration Error: " + e.getMessage());
	            System.exit(1);
	        } catch (IOException e) {
	            LOGGER.error("dcm2xml: Failed to convert " + inputFile + ": " + e.getMessage(), e);
	            System.exit(1);
	        }
	        finally
	        {
	        	try {
					if (inputStream != null)
						inputStream.close();
					if (outputWriter != null)
						outputWriter.close();
				} catch (IOException e) {
					LOGGER.error("Error when closing input / output stream ", e);
				}
	        }
	        long t2 = System.currentTimeMillis();
	        if (outputFile != null)
	            LOGGER.info("Finished conversion of " + inputFile + "to "+ outputFile + " in " + (t2 - t1) + "ms");          
	    }
    
}