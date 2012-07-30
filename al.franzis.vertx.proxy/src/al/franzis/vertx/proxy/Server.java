package al.franzis.vertx.proxy;

import java.io.File;
import java.util.HashMap;
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
	private static String CACHEPATH = "/home/alex/dev/vertxProxyCache/"; //$NON-NLS-1$
	private static int PORT = 8070;
	private static boolean USE_MEMCACHE = false;
	
	
	private static String DELEGATE_HOST = "10.231.163.233"; //$NON-NLS-1$
    private static int DELEGATE_PORT = 8080;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Server.class);
	private final Vertx vertx = Vertx.newVertx();
	
	private final static Map<String, File> cache = new Hashtable<String, File>();
	
	public static void main(String[] args) {
		Map<String, String> params = parseParameters( args );
        
		PORT = parsePort(params, "port", 8070);
     
        String targetHost = params.get( "targetHost" ); //$NON-NLS-1$
        if ( targetHost != null )
        {
            DELEGATE_HOST = targetHost;
        }
        
        DELEGATE_PORT = parsePort(params, "targetPort", 8080);
        
        String cacheDir = params.get( "cacheDir" ); //$NON-NLS-1$
        if ( cacheDir != null )
        {
            CACHEPATH = cacheDir;
        }
        File cacheFile = new File( CACHEPATH );
        if ( !cacheFile.exists() )
            if ( !cacheFile.mkdirs() )
            	return;
        
        String useMemCache = params.get( "useMemCache" ); //$NON-NLS-1$
        if ( useMemCache != null )
            USE_MEMCACHE = Boolean.parseBoolean( useMemCache );
		
        scanCache();
        
		new Server().start(PORT);
		
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			LOGGER.info("Thread interrupted",e);
		}
	}
	
	public void start( int port ) {
		vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
			public void handle(HttpServerRequest request) {
		        LOGGER.info("A request has arrived on the server!");
		        boolean keepAlive = false;
		        
		        String headerLine = request.uri;
                if ( headerLine == null || headerLine.equals( CRLF ) || headerLine.isEmpty() )
                    return;
		        
                Map<String,String> paramsMap = request.params();
		        String getLine = headerLine;
               
		        Map<String,String> httpHeaders = request.headers();
		        String connection = httpHeaders.get("Connection" );
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
		}).listen(port);
	}
	
	 private static boolean cacheableData( String headerLine )
     {
         return headerLine.contains( "wado" ); //$NON-NLS-1$
     }
	 
	 private static String lookupCache( String headerLine )
     {
		 int requestHash = headerLine.hashCode();
         String key = Integer.toString( requestHash );
         
         if ( !cache.containsKey( key ) )
             return null;
         
         String filePath = CACHEPATH + "/" + key;
         return filePath;
     }
	 
	private void doRequest(HttpServerRequest originalRequest, String headerLine, Map<String,String> paramsMap) {
		HttpClient client = vertx.createHttpClient().setPort(DELEGATE_PORT).setHost(DELEGATE_HOST);
	
		final HttpServerRequest originalRequestRef = originalRequest;
		final Buffer responseBuffer = new Buffer(0);
		
		HttpClientRequest forwardRequest = client.get(headerLine, new Handler<HttpClientResponse>() {
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
						originalRequestRef.response.end(responseBuffer);
					}
					
				});
			}
		});

		forwardRequest.headers().putAll(paramsMap);
		forwardRequest.end();
	}
	
	private static void scanCache()
    {
        File cacheDir = new File( CACHEPATH );
        for ( File file : cacheDir.listFiles() )
        {
            Server.cache.put( file.getName(), file );
        }
    }
	
	private static Map<String, String> parseParameters( String args[] )
    {
        Map<String, String> params = new HashMap<String, String>();
        for ( String string : args )
        {
            String[] pair = string.split( "=" ); //$NON-NLS-1$
            params.put( pair[ 0 ], pair[ 1 ] );
        }
        return params;
    }
	
	private static int parsePort(Map<String,String> params, String paramName, int defaultPort) {
		String portString = params.get(paramName);
		int port = defaultPort;
		if ( portString != null )
        {
            try
            {
                port = Integer.parseInt( portString );
            }
            catch ( Exception e )
            {
            	// swallow
            }
        }
		return port;
	}
}
