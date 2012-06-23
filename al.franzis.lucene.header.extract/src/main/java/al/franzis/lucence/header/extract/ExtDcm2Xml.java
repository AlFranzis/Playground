package al.franzis.lucence.header.extract;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.SAXWriter;


public class ExtDcm2Xml {
	private URL xslt;
	private String[] xsltParams;
	private File baseDir;
	private int[] exclude;
	private boolean xsltInc = false;
	private boolean indent = true;
	private boolean comments = false;

    public final void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public final void setExclude(int[] exclude) {
        this.exclude = exclude;
    }

    public final void setIndent(boolean indent) {
        this.indent = indent;
    }

    public final void setComments(boolean comments) {
        this.comments = comments;
    }
    
    public final void setXslt(URL xslt) {
        this.xslt = xslt;
    }

    public final void setXsltInc(boolean xsltInc) {
        this.xsltInc = xsltInc;
    }

    public final void setXsltParams(String[] xsltParam) {
        xsltParams = (xsltParam != null ? xsltParam.clone() : null);
    }

    public void convert(InputStream inputStream, Writer outputWriter) throws IOException,
            TransformerConfigurationException {
        DicomInputStream dis = new DicomInputStream(inputStream);
        try {
            TransformerHandler th = getTransformerHandler();
            th.getTransformer().setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
            th.setResult(outputWriter != null ? new StreamResult(outputWriter) : new StreamResult(System.out));
            final SAXWriter writer = new SAXWriter(th, comments ? th : null);
//            writer.setBaseDir(baseDir);
            writer.setExclude(exclude);
            dis.setHandler(writer);
            dis.readDicomObject(new BasicDicomObject(), -1);
        } finally {
            dis.close();
        }
    }

    private TransformerHandler getTransformerHandler()
            throws TransformerConfigurationException, IOException {
        SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory
                .newInstance();
        if (xslt == null) {
            return tf.newTransformerHandler();
        }
        if (xsltInc) {
            tf.setAttribute("http://xml.apache.org/xalan/features/incremental", Boolean.TRUE);
        }
        TransformerHandler th = tf.newTransformerHandler(new StreamSource(xslt.openStream(),
                xslt.toExternalForm()));
        Transformer t = th.getTransformer();
        if (xsltParams != null) {
            for (int i = 0; i+1 < xsltParams.length; i++,i++) {
                 t.setParameter(xsltParams[i], xsltParams[i+1]);
            }
        }
        return th;
    }

}
