package al.franzis.lucene.header.serversource;

import java.io.OutputStream;

public interface OutputStreamFactory {

	public OutputStream getOutputStream( String instanceUID );
	
}
