package al.franzis.lucence.header.extract;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class DicomTransformerHandler implements TransformerHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(DicomTransformerHandler.class);
	
	private Transformer transformer;
	private Document document;
	private DicomResult result;
	private Field field;
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
	
		
		if ( "attr".equals(qName) ) {
			String tag = atts.getValue("tag");
			String vr = atts.getValue("vr");
			
			
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

		if ("attr".equals(qName)) {
			StackElement se = tagStack.pop();
			if (!se.isSequenceTag) {
				field = new Field(se.getFieldName(), value, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
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
		LOGGER.debug("ignorableWhitespace({})", Arrays.copyOfRange(ch, start, start + length));
		
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub
		
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

}
