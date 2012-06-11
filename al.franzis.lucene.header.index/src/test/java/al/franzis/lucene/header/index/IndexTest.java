package al.franzis.lucene.header.index;

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

    public void testDICOMIndexing()
    {
    	String docsDir = "";
    	String indexDir = "";
    	DICOMIndexer index = new DICOMIndexer(docsDir, indexDir, true);
        index.index();
    }
}
