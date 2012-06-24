package al.franzis.lucence.header.extract;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.dcm4che2.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class DicomTransformerHandler implements TransformerHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(DicomTransformerHandler.class);
	
	private static final String TAG_XMLELEMENT = "attr"; 
	private static final String TAG_XMLATTRIBUTE = "tag";
	private static final String VR_XMLATTRIBUTE = "vr"; 
	
	private Transformer transformer;
	private Document document;
	private DicomResult result;
	private String value;
	private final Stack<StackElement> tagStack = new Stack<StackElement>();
	
	
	private static class StackElement {
		private List<String> path;
		private boolean isSequenceTag;
		private String vr;
		
		private String getFieldName() {
			if ( path.size() == 1) {
				return path.get(0) + ":" + vr;
			}
			
			boolean first = true;
			StringBuffer buf = new StringBuffer();
			for ( int i = 0; i < path.size(); i++ )
			{
				if (!first)
					buf.append("#");
				buf.append(path.get(0));
				first = false;
			}
			buf.append(":");
			buf.append(vr);
			return buf.toString();
		}
		
	}
	
	public DicomTransformerHandler() {
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch ( Exception e) {
			LOGGER.error("Error during creation of transformer", e);
		}
			
	}
	
	@Override
	public void setDocumentLocator(Locator locator) {
		LOGGER.debug("setDocumentLocator()");
		
	}

	@Override
	public void startDocument() throws SAXException {
		LOGGER.debug("startDocument()");
		document = new Document();
	}

	@Override
	public void endDocument() throws SAXException {
		LOGGER.debug("endDocument()");
		result.setDocument(document);
		document = null;
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		LOGGER.debug("startPrefixMapping()");
		
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		LOGGER.debug("endPrefixMapping()");
		
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		StringBuffer buf = new StringBuffer();
		for( int i = 0; i < atts.getLength(); i++)
			buf.append( atts.getQName(i) + " : " + atts.getValue(i) + ", ");
		LOGGER.debug("startElement( {} attributes: {} )", qName, buf.toString());
	
		
		if (TAG_XMLELEMENT.equals(qName)) {
			String tag = atts.getValue(TAG_XMLATTRIBUTE);
			String vr = atts.getValue(VR_XMLATTRIBUTE);
			
			List<String> path = tagStack.isEmpty() ? new LinkedList<String>() : new LinkedList<String>(tagStack.lastElement().path);
			path.add(tag);
			StackElement se = new StackElement();
			se.path = path;
			se.vr = vr;
			se.isSequenceTag = "SQ".equals(vr);
			tagStack.push(se);
			
		}	
			
			
	}
			
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		LOGGER.debug("endElement({})", qName);

		if (TAG_XMLELEMENT.equals(qName)) {
			StackElement se = tagStack.pop();
			if (!se.isSequenceTag) {
				Fieldable field = createFieldForTag(se, value);
				if (field != null)
					document.add(field);
			}
		}
		
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		value = new String(ch, start, length);
		LOGGER.debug("characters({})", value);
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		LOGGER.debug("ignorableWhitespace({})", new String(ch, start, length));
		
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
	}

	@Override
	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
	}

	@Override
	public void endDTD() throws SAXException {
	}

	@Override
	public void startEntity(String name) throws SAXException {
	}

	@Override
	public void endEntity(String name) throws SAXException {
	}

	@Override
	public void startCDATA() throws SAXException {
	}

	@Override
	public void endCDATA() throws SAXException {
	}

	@Override
	public void comment(char[] ch, int start, int length) throws SAXException {
		LOGGER.debug("comment(\"{}\")", new String(ch, start,length));
	}

	@Override
	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
	}

	@Override
	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) throws SAXException {
	}

	@Override
	public void setResult(Result result) throws IllegalArgumentException {
		this.result = (DicomResult)result;
	}

	@Override
	public void setSystemId(String systemID) {
		LOGGER.debug("setSystemID()");
		
	}

	@Override
	public String getSystemId() {
		return null;
	}

	@Override
	public Transformer getTransformer() {
		return transformer;
	}
	
	private static Fieldable createFieldForTag(StackElement se, String stringValue) {
		String vr = se.vr;
		String fieldname = se.getFieldName();
		Fieldable field = null;
		
		// int convertibles
		if ("US".equals(vr) || "UL".equals(vr) || "SL".equals(vr) || "SS".equals(vr)) {
			try {
				int intValue = Integer.parseInt(stringValue);
				field = new NumericField(fieldname, 4).setIntValue(intValue);
			} catch (NumberFormatException e) {
				LOGGER.error("Error while converting tag {} [{}], value: {} to integer",
						new Object[] { fieldname, vr, stringValue }, e);
			}
			// decimal string, integer string
		} else if ("DS".equals(vr) || "IS".equals(vr)) {
			field = new Field(fieldname, stringValue, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
			// string convertibles
		} else if ("SH".equals(vr) || "UT".equals(vr) || "UI".equals(vr) || "CS".equals(vr) || "LT".equals(vr)
				|| "LO".equals(vr) || "PN".equals(vr)) {
			field = new Field(fieldname, stringValue, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
		} else if ("OB".equals(vr) || "OW".equals(vr)) {
			field = new Field(fieldname, stringValue, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
			// date / time convertibles
		} else if ("DT".equals(vr)) {
			try {
				Date dateValue = DateUtils.parseDT(stringValue, true);
				field = new NumericField(fieldname, 4).setLongValue(dateValue.getTime());
			} catch (NumberFormatException e) {
				LOGGER.error("Error while converting tag {} [{}], value: {} to date/time",
						new Object[] { fieldname, vr, stringValue }, e);
			}
		} else if ("DA".equals(vr)) {
			try {
				Date dateValue = DateUtils.parseDA(stringValue, true);
				field = new NumericField(fieldname, 4).setLongValue(dateValue.getTime());
			} catch (NumberFormatException e) {
				LOGGER.error("Error while converting tag {} [{}], value: {} to date/time",
						new Object[] { fieldname, vr, stringValue }, e);
			}
			// time converibles
		} else if ("TM".equals(vr)) {
			try {
				Date dateValue = DateUtils.parseTM(stringValue, true);
				field = new NumericField(fieldname, 4).setLongValue(dateValue.getTime());
			} catch (NumberFormatException e) {
				LOGGER.error("Error while converting tag {} [{}], value: {} to date/time",
						new Object[] {fieldname, vr, stringValue }, e);
			}
			// unknown
		} else if ("UN".equals(vr)) {
			field = new Field(fieldname, stringValue, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
		} else {
			LOGGER.error("Error while converting tag {} [{}], value: {}", new Object[] { fieldname, vr,
					stringValue });
		}

		return field;
	}
	
	

}
