package al.franzis.lucene.header.serversource;

import java.io.OutputStream;

public interface OutputStreamFactory<T extends OutputStream> {

	public T getOutputStream( String instanceUID );
	
	public void close( String instanceUID, T outputStream );
	
}
