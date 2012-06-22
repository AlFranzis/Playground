package al.franzis.lucence.header.extract;

import java.util.Arrays;

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
	private int level = 0;
	private Document document;
	private Field field;
	private String path;
	
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
		level++;
		
		if ("attr".equals(qName)) {
			String tag = atts.getValue("tag");
			String vr = atts.getValue("vr");
			if ("SQ".equals(vr)) {
				if (path == null)
					path = tag;
				else
					path += "#" + tag;

			} else {
				
			}
		}
			
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		LOGGER.debug("endElement({})", qName);
		
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		LOGGER.debug("characters({})", Arrays.copyOfRange(ch, start, start+length));
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endDTD() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startCDATA() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endCDATA() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void comment(char[] ch, int start, int length) throws SAXException {
		LOGGER.debug("comment({})", Arrays.copyOfRange(ch, start, start + length));
		
	}

	@Override
	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResult(Result result) throws IllegalArgumentException {
		
		
	}

	@Override
	public void setSystemId(String systemID) {
		LOGGER.debug("setSystemID()");
		
	}

	@Override
	public String getSystemId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transformer getTransformer() {
		return transformer;
	}

}
