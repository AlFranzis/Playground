package al.franzis.vertx.proxy;

import java.util.ArrayList;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.deploy.Verticle;


public class Server extends Verticle {
	private final Logger logger = container.getLogger();
	private final static String CRLF = "\r\n"; //$NON-NLS-1$
	
	public void start() {
		vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
			public void handle(HttpServerRequest request) {
		        logger.info("A request has arrived on the server!");
		        boolean keepAlive = false;
		        
		        
		        String headerLine = request.uri;
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
