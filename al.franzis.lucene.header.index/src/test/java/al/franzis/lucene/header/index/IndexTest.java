package al.franzis.lucene.header.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;
import org.dcm4che2.data.Tag;

import al.franzis.lucence.header.extract.ExtDcm2Lucene;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class IndexTest extends TestCase
{
    public IndexTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( IndexTest.class );
    }

    public void testDICOMIndexing() throws CorruptIndexException, IOException
    {
    	String docsDir = "/home/alex/dev/git-repos/Playground/al.franzis.lucene.header.index/src/test/resources";
    	RAMDirectory ramDir = new RAMDirectory();
    	DICOMIndexer index = new DICOMIndexer(docsDir, ramDir, true);
        boolean opened = index.open();
        Assert.assertTrue(opened);
    	
        index.index();
    	
    	IndexReader reader = IndexReader.open(ramDir);
    	assertEquals(2, reader.maxDoc());
    	
    }
    
    public void testDICOMQueueIndexing() throws CorruptIndexException, IOException, TransformerConfigurationException
    {
    	Queue<Document> docQueue = new LinkedList<Document>();
    
    	ExtDcm2Lucene dcm2Document = new ExtDcm2Lucene();
		// exclude pixel data from document
		dcm2Document.setExclude(new int[] { Tag.PixelData });

		File inputFile = new File(
				"/home/alex/dev/git-repos/Playground/al.franzis.lucene.header.extract/src/test/resources/MR000001");
		FileInputStream inputStream = new FileInputStream(inputFile);
			
		dcm2Document.convert(inputStream, docQueue);
    	
    	RAMDirectory ramDir = new RAMDirectory();
    	QueueDICOMIndexer index = new QueueDICOMIndexer(docQueue, ramDir, true);
        boolean opened = index.open();
        Assert.assertTrue(opened);
    	
        index.index();
    	
    	IndexReader reader = IndexReader.open(ramDir);
    	assertEquals(1, reader.maxDoc());
    }
}
