package al.franzis.vertx.proxy;

import java.util.ArrayList;
import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;


public class Server {
	private final static String CRLF = "\r\n"; //$NON-NLS-1$
	private static LoggerFactory loggerFactory; 
	private final Logger logger = loggerFactory.getLogger(Server.class);
	private final Vertx vertx = Vertx.newVertx();
	
	public static void main(String[] args) {
//		Server.loggerFactory = new LoggerFactory();
		
		new Server().start();
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		
		vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
			public void handle(HttpServerRequest request) {
		        logger.info("A request has arrived on the server!");
		        boolean keepAlive = false;
		        
		        
		        String headerLine = request.uri;
		        Map<String,String> paramsMap = request.params();
                if ( headerLine == null || headerLine.equals( CRLF ) || headerLine.isEmpty() )
                    return;
		        
		        String getLine = headerLine;
                ArrayList<String> params = new ArrayList<String>();
                while ( !(headerLine == null || headerLine.equals( CRLF ) || headerLine.equals( "" )) ) //$NON-NLS-1$
                {
                    headerLine = headerLine;
                    params.add( headerLine );
                    keepAlive |= "Connection: keep-alive".equals( headerLine ); //$NON-NLS-1$
                }
                keepAlive = false;
		    }
		}).listen(8070);
	}
}
