package al.franzis.lucence.header.extract;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomInputHandler;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.util.TagUtils;

public class ExtDcm2Txt implements DicomInputHandler {
	private static final Logger LOGGER = Logger.getLogger(ExtDcm2Txt.class);
	
    private static final int DEF_MAX_WIDTH = 78;
//    private static final int MIN_MAX_WIDTH = 32;
    public static final int MAX_MAX_WIDTH = 512;
    private static final int DEF_MAX_VAL_LEN = 64;
//    private static final int MIN_MAX_VAL_LEN = 16;
    public static final int MAX_MAX_VAL_LEN = 512;

    private StringBuffer line = new StringBuffer();
    private char[] cbuf = new char[64];
    private boolean withNames = true;
    private int maxWidth = DEF_MAX_WIDTH;
    private int maxValLen = DEF_MAX_VAL_LEN;
    
    private Writer outputWriter;

    public final void setMaxValLen(int maxValLen) {
        this.maxValLen = maxValLen;
    }

    public final void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }


    public final void setWithNames(boolean withNames) {
        this.withNames = withNames;
    }
    
    public void dump(InputStream inputStream, Writer outputWriter) throws IOException {
        DicomInputStream dis = new DicomInputStream(inputStream);
        try {
            dis.setHandler(this);
            this.outputWriter = outputWriter;
            dis.readDicomObject(new BasicDicomObject(), -1);
        } finally {
            dis.close();
        }
    }

    @Override
    public boolean readValue(DicomInputStream in) throws IOException {
            switch (in.tag()) {
            case Tag.Item:
                if (in.sq().vr() != VR.SQ && in.valueLength() != -1) {
                    outFragment(in);
                } else {
                    outItem(in);                    
                }
                break;
            case Tag.ItemDelimitationItem:
            case Tag.SequenceDelimitationItem: 
                if (in.level() > 0)
                    outItem(in);
                break;
            default:
                outElement(in);
            }
        return true;
    }

    private void outElement(DicomInputStream in) throws IOException {
        outTag(in);
        outVR(in);
        outLen(in);
        if (hasItems(in)) {
            outLine(in);
            readItems(in);
        } else {
            outValue(in);
            outLine(in);
        }
    }

    private void outValue(DicomInputStream in) throws IOException {
        int tag = in.tag();
        VR vr = in.vr();
        byte[] val = in.readBytes(in.valueLength());
        DicomObject dcmobj = in.getDicomObject();
        boolean bigEndian = in.getTransferSyntax().bigEndian();
        line.append(" [");
        vr.promptValue(val, bigEndian, dcmobj.getSpecificCharacterSet(),
                cbuf, maxValLen, line);
        line.append("]");
        if (tag == Tag.SpecificCharacterSet
                || tag == Tag.TransferSyntaxUID
                || TagUtils.isPrivateCreatorDataElement(tag)) {
            dcmobj.putBytes(tag, vr, val, bigEndian);
        }
        if (tag == 0x00020000) {
            in.setEndOfFileMetaInfoPosition(
                    in.getStreamPosition() + vr.toInt(val, bigEndian));
        }
    }

    private boolean hasItems(DicomInputStream in) {
        return in.valueLength() == -1 || in.vr() == VR.SQ;
    }

    private void readItems(DicomInputStream in) throws IOException {
        in.readValue(in);
        in.getDicomObject().remove(in.tag());
    }

    private void outItem(DicomInputStream in) throws IOException {
        outTag(in);
        outLen(in);
        outLine(in);
        in.readValue(in);
    }

    private void outFragment(DicomInputStream in) throws IOException {
        outTag(in);
        outLen(in);
        in.readValue(in);
        DicomElement sq = in.sq();
        byte[] data = sq.removeFragment(0);
        boolean bigEndian = in.getTransferSyntax().bigEndian();
        line.append(" [");
        sq.vr().promptValue(data, bigEndian, null, cbuf, maxValLen, line);
        line.append("]");
        outLine(in);
    }
    
    private void outTag(DicomInputStream in) {
        line.setLength(0);
        line.append(in.tagPosition()).append(':');
        for (int i = in.level(); i > 0; --i)
            line.append('>');
        TagUtils.toStringBuffer(in.tag(), line);
    }

    private void outVR(DicomInputStream in) {
        line.append(" ").append(in.vr());
    }
    
    private void outLen(DicomInputStream in) {
        line.append(" #").append(in.valueLength());
    }

    private void outLine(DicomInputStream in) {
        if (withNames)
            line.append(" ").append(in.getDicomObject().nameOf(in.tag()));
        if (line.length() > maxWidth)
            line.setLength(maxWidth);
        try {
			outputWriter.append(line + "\n");
		} catch (IOException e) {
			LOGGER.error("I/O error");
		}
    }

}
