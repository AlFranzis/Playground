package al.franzis.vertx.proxy;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.SimpleHandler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;


public class Server {
	private final static String CRLF = "\r\n"; //$NON-NLS-1$
	private static String CACHEPATH = "c:\\TEMP\\proxycache\\"; //$NON-NLS-1$
	
	public static String DELEGATE_HOST = "10.231.163.233"; //$NON-NLS-1$
    public static final int DELEGATE_PORT = 8080;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Server.class);
	private final Vertx vertx = Vertx.newVertx();
	
	private final static Map<String, File> cache = new Hashtable<String, File>();
	
	public static void main(String[] args) {
		
		new Server().start();
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			LOGGER.info("Thread interrupted",e);
		}
	}
	
	public void start() {
		
		vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
			public void handle(HttpServerRequest request) {
		        LOGGER.info("A request has arrived on the server!");
		        boolean keepAlive = false;
		        
		        
		        String headerLine = request.uri;
                if ( headerLine == null || headerLine.equals( CRLF ) || headerLine.isEmpty() )
                    return;
		        
                Map<String,String> paramsMap = request.params();
		        String getLine = headerLine;
               
		        String connection = paramsMap.get("Connection" );
		        keepAlive |= "Connection: keep-alive".equals( connection );
               
                keepAlive = false;
                if ( cacheableData( getLine ) ) // try to get from cache...
                {
                	String cacheFilePath = lookupCache( getLine );
                	if ( cacheFilePath != null ) {
                		request.response.sendFile(cacheFilePath);
                	} else {
                		doRequest(request, getLine, paramsMap);
                	}
                }
		    }
		}).listen(8070);
	}
	
	 private static boolean cacheableData( String headerLine )
     {
         return headerLine.contains( "wado" ); //$NON-NLS-1$
     }
	 
	 private static String lookupCache( String headerLine )
     {
		 int requestHash = headerLine.hashCode();
         String key = Integer.toString( requestHash );
         
         if ( !Server.cache.containsKey( key ) )
             return null;
         
         
         File f = new File( Server.CACHEPATH, key );
         return f.getAbsolutePath();
     }
	 
	private void doRequest(final HttpServerRequest originalRequest, String headerLine, Map<String,String> paramsMap) {
		HttpClient client = vertx.createHttpClient().setPort(DELEGATE_PORT).setHost(DELEGATE_HOST);

		String reqString = headerLine;
	
		final Buffer responseBuffer = new Buffer(0);
		
		HttpClientRequest forwardRequest = client.get(reqString, new Handler<HttpClientResponse>() {
			public void handle(HttpClientResponse response) {
				LOGGER.info("Got a HTTP response for forward request: " + response.statusCode);
				
				response.dataHandler(new Handler<Buffer>() {
					@Override
					public void handle(Buffer dataBuffer) {
						LOGGER.info("Data arrived");
						responseBuffer.appendBuffer(dataBuffer);
					}

				});
				response.endHandler(new SimpleHandler() {
					@Override
					public void handle() {
						LOGGER.info("Forward request completed");
						originalRequest.response.writeBuffer(responseBuffer);
					}
					
				});
			}
		});

		forwardRequest.headers().putAll(paramsMap);
		forwardRequest.end();
	}
}
