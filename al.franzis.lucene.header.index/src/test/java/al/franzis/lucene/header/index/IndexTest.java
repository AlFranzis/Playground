package al.franzis.lucene.header.index;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;

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
}
