package al.franzis.lucence.header.extract;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.lucene.document.Document;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.SAXWriter;


public class ExtDcm2Lucene {
	private int[] exclude;

    public final void setExclude(int[] exclude) {
        this.exclude = exclude;
    }

    public void convert(InputStream inputStream, Queue<Document> documentBuffer) throws IOException,
            TransformerConfigurationException {
        DicomInputStream dis = new DicomInputStream(inputStream);
        try {
        	DicomResult result = new DicomResult();
            TransformerHandler th = getTransformerHandler();
            th.setResult(result);
            SAXWriter writer = new SAXWriter(th, null);
//            writer.setBaseDir(baseDir);
            writer.setExclude(exclude);
            dis.setHandler(writer);
            dis.readDicomObject(new BasicDicomObject(), -1);
            
            documentBuffer.add(result.getDocument());
            
        } finally {
            dis.close();
        }
    }

    private TransformerHandler getTransformerHandler()
            throws TransformerConfigurationException, IOException {
       return new DicomTransformerHandler();
    }

}
