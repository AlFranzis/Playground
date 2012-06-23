package al.franzis.lucence.header.extract;

import javax.xml.transform.Result;

import org.apache.lucene.document.Document;

public class DicomResult implements Result {
	private Document document;
	
	@Override
	public void setSystemId(String systemId) {}

	@Override
	public String getSystemId() {
		return null;
	}
	
	public void setDocument(Document document) {
		this.document = document;
	}
	
	public Document getDocument() {
		return document;
	}

}
