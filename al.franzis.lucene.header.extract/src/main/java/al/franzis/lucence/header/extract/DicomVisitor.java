package al.franzis.lucence.header.extract;

import java.util.List;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;

public class DicomVisitor implements DicomObject.Visitor {
	private static int MAX_VALUE_LENGTH = 500;
	private char[] cbuf = new char[64];
	
	private DicomObject dicomObject;
	private List<Integer> tags;
	
	
	public DicomVisitor( DicomObject dicomObject, List<Integer> tags )
	{
		this.dicomObject = dicomObject;
		this.tags = tags;
	}
	
	@Override
	public boolean visit( DicomElement element ) {
		
		
		
		StringBuffer buf = new StringBuffer();
		if( tags.contains( element.tag() )) {
			element.toStringBuffer(buf, MAX_VALUE_LENGTH );
		}
		else {
			buf.append( dicomObject.nameOf(element.tag()) );
			element.vr().promptValue(element.getBytes(), 
					element.bigEndian(), 
					dicomObject.getSpecificCharacterSet(), 
					cbuf, MAX_VALUE_LENGTH, buf);
			//element.toStringBuffer(buf, MAX_VALUE_LENGTH );
		}
			
		System.out.println( buf );
		return true;
	}

}
