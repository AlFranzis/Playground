package al.franzis.vertx.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class ProxyServer
{

    public static void start()
    {
        final String[] args = parseParameters();

        Thread proxy = new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                ProxyServer.main( args );
            }
        } );
        proxy.start();
    }

    private static String[] parseParameters()
    {
        String port = null;
        String targetHost = null;
        String cacheDir = null;
        String useMemCache = null;
        
        // read local file from user directory (agility_server_proxy.cfg)
        File localConfigFile = new File( System.getProperty( "user.home" ) + "/agility_server_proxy.cfg" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( localConfigFile.exists() )
        {
            BufferedReader fileReader = null;
            try
            {
                fileReader = new BufferedReader( new FileReader( localConfigFile ) );
                String line = fileReader.readLine();
                while ( line != null )
                {
                    if ( line.startsWith( "port" ) ) //$NON-NLS-1$
                    {
                        port = line;
                    }
                    else if ( line.startsWith( "targetHost" ) ) //$NON-NLS-1$
                    {
                        targetHost = line;
                    }
                    else if ( line.startsWith( "cacheDir" ) ) //$NON-NLS-1$
                    {
                        cacheDir = line;
                    }
                    else if ( line.startsWith( "useMemCache" ) ) //$NON-NLS-1$
                    {
                        useMemCache = line;
                    }
                    
                    line = fileReader.readLine();
                }
            }
            catch ( FileNotFoundException e )
            {
                
            }
            catch ( IOException e )
            {
                
            }
            finally
            {
                if ( fileReader != null )
                {
                    try
                    {
                        fileReader.close();
                    }
                    catch ( IOException e )
                    {
                        
                    }
                }
            }
        }
        
        // if not yet all parameters set, read config
        if ( port == null )
        {
            port = readFromConfg( "TEST.proxy.port" ); //$NON-NLS-1$
            if ( port != null )
                port = "port=" + port; //$NON-NLS-1$
        }
        if ( targetHost == null )
        {
            targetHost = readFromConfg( "TEST.proxy.targetHost" ); //$NON-NLS-1$
            if ( targetHost != null )
                targetHost = "targetHost=" + targetHost; //$NON-NLS-1$
        }
        if ( cacheDir == null )
        {
            cacheDir = readFromConfg( "TEST.proxy.cacheDir" ); //$NON-NLS-1$
            if ( cacheDir != null )
                cacheDir = "cacheDir=" + cacheDir; //$NON-NLS-1$
        }
        if ( useMemCache == null )
        {
            useMemCache = readFromConfg( "TEST.proxy.useMemCache" ); //$NON-NLS-1$
            if ( useMemCache != null )
                useMemCache = "useMemCache=" + cacheDir; //$NON-NLS-1$
        }

        // if not yet all parameters set, set hard coded values
        if ( port == null )
            port = "port=8070"; //$NON-NLS-1$
        if ( targetHost == null )
            targetHost = "targetHost=10.231.163.233"; //$NON-NLS-1$
        if ( cacheDir == null )
            cacheDir = "cacheDir=c:\\TEMP\\proxycache\\"; //$NON-NLS-1$
        if ( useMemCache == null )
            useMemCache = "useMemCache=false"; //$NON-NLS-1$

        return new String[] {
                port, targetHost, cacheDir, useMemCache
        };
    }
    
    private static String readFromConfg( String configKey )
    {
        try
        {
            return null;
        }
        catch ( IllegalArgumentException ex )
        {
            return null;
        }
    }

    public static String CACHEPATH = "c:\\TEMP\\proxycache\\"; //$NON-NLS-1$
    public static String DELEGATE_HOST = "10.231.163.233"; //$NON-NLS-1$
    public static final int DELEGATE_PORT = 8080;

    private static final int WORKERCOUNT = 6;
    public static Map<String, File> cache = new Hashtable<String, File>();
    public static Map<String, Reference<byte[]>> memCache = new Hashtable<String, Reference<byte[]>>();
    
    public static BlockingQueue<httpRequestHandler> queue = new LinkedBlockingQueue<httpRequestHandler>();
    public static AtomicInteger availableWorkers = new AtomicInteger( 0 );
    private static int workers;
    public static boolean USE_MEMCACHE = false;
    
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
    
    public static void main( String args[] )
    {
        if ( args.length == 0 )
        {
            //            System.out.prinlkn("ProxyServer [port=] targetHost= cacheDir= [useMemCache=true/false]"); //$NON-NLS-1$
            return;
        }
        Map<String, String> params = parseParameters( args );
        int port;
        ServerSocket server_socket;
        String portString = params.get( "port" ); //$NON-NLS-1$
        if ( portString != null )
        {
            try
            {
                port = Integer.parseInt( portString );
            }
            catch ( Exception e )
            {
                port = 8070;
            }
        }
        else
            port = 8070;
        String targetHost = params.get( "targetHost" ); //$NON-NLS-1$
        if ( targetHost != null )
        {
            DELEGATE_HOST = targetHost;
        }
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
        try
        {
            scanCache();

            // print out the port number for user
            server_socket = new ServerSocket( port );
            

            for ( workers = 0; workers < WORKERCOUNT; workers++ )
                new Worker( workers ).start();

            // server infinite loop
            while ( true )
            {
                Socket socket = server_socket.accept();
               

                // Construct handler to process the HTTP request message.
                try
                {
                    httpRequestHandler request = new httpRequestHandler( socket );
                    if ( availableWorkers.get() == 0 )
                        new Worker( workers++ ).start();
                    queue.put( request );
                }
                catch ( Exception e )
                {
                }
            }
        }
        catch ( IOException e )
        {
        }
    }

    private static void scanCache()
    {
        File cacheDir = new File( CACHEPATH );
        for ( File file : cacheDir.listFiles() )
        {
            cache.put( file.getName(), file );
        }
    }
    
    private static class Worker extends Thread
    {
        public Worker( int nr )
        {
            super( "[PROXY] worker " + nr ); //$NON-NLS-1$
            ProxyServer.availableWorkers.incrementAndGet();
            setDaemon( true );
        }
        
        @Override
        public void run()
        {
            while ( true )
            {
                httpRequestHandler request;
                try
                {
                    request = ProxyServer.queue.take();
                    ProxyServer.availableWorkers.decrementAndGet();
                    request.run();
                    ProxyServer.availableWorkers.incrementAndGet();
                }
                catch ( InterruptedException e )
                {
                }
            }
        }
    }
    
    private static class httpRequestHandler implements Runnable
    {
        final static String CRLF = "\r\n"; //$NON-NLS-1$

        Socket socket;
        //        InputStream input;
        OutputStream output;
        BufferedReader br;

        public httpRequestHandler( Socket socket ) throws Exception
        {
            this.socket = socket;
            //            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();
            this.br = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
        }

        // Implement the run() method of the Runnable interface.
        @Override
        public void run()
        {
            try
            {
                processRequest();
            }
            catch ( Exception e )
            {
            }
        }
        
        private void processRequest() throws Exception
        {
            while ( true )
            {
                boolean keepAlive = false;
                boolean served = false;
                int waited = 0;
                while ( !br.ready() )
                {
                    Thread.sleep( 10 );
                    waited++;
                    if ( waited > 100 )
                        break;
                }
                String headerLine = br.readLine();
                if ( headerLine == null || headerLine.equals( CRLF ) || headerLine.isEmpty() )
                    break;

                String getLine = headerLine;
                ArrayList<String> params = new ArrayList<String>();
                while ( !(headerLine == null || headerLine.equals( CRLF ) || headerLine.equals( "" )) ) //$NON-NLS-1$
                {
                    headerLine = br.readLine();
                    params.add( headerLine );
                    keepAlive |= "Connection: keep-alive".equals( headerLine ); //$NON-NLS-1$
                }
                keepAlive = false;
                if ( cacheableData( getLine ) ) // try to get from cache...
                {
                    InputStream response = lookupCache( getLine );
                    if ( response != null ) // cached
                    {
                        sendBytes( response, output );
                        served = true;
                        if ( !keepAlive )
                            break;
                    }
                    // pipe through cache
                    else
                    {
                        Socket s = doRequest( getLine, params );
                        sendAndCacheBytes( s.getInputStream(), output, getLine );
                        s.close();
                        served = true;
                        if ( !keepAlive )
                            break;
                    }
                }
                if ( !served )
                {
                    // not cacheable...
                    Socket s = doRequest( getLine, params );
                    sendBytes( s.getInputStream(), output );
                    s.close();
                }
                if ( !keepAlive )
                    break;
            }
            
            try
            {
                output.close();
                br.close();
                socket.close();
            }
            catch ( Exception e )
            {
            }
        }
        
        private static Socket doRequest( String headerLine, List<String> params ) throws IOException
        {
            StringBuilder req = new StringBuilder();
            req.append( headerLine );
            req.append( CRLF );
            int i = 0;
            while ( !(i >= params.size() || headerLine.equals( CRLF ) || headerLine.equals( "" )) ) //$NON-NLS-1$
            {
                headerLine = params.get( i++ );
                req.append( headerLine );
                if ( !headerLine.equals( CRLF ) )
                    req.append( CRLF );
            }
            String reqString = req.toString();
            reqString = reqString.replaceAll( "keep-alive", "close" ); //$NON-NLS-1$ //$NON-NLS-2$
            Socket s = new Socket( ProxyServer.DELEGATE_HOST, ProxyServer.DELEGATE_PORT );
            OutputStream os = s.getOutputStream();
            os.write( reqString.getBytes() );
            return s;
        }
        
        private static boolean cacheableData( String headerLine )
        {
            return headerLine.contains( "wado" ); //$NON-NLS-1$
        }
        
        private static InputStream lookupCache( String headerLine )
        {
            String key = Integer.toString( headerLine.hashCode() );
            Reference<byte[]> inMem = ProxyServer.USE_MEMCACHE ? ProxyServer.memCache.get( headerLine ) : null;
            byte[] cached = inMem != null ? inMem.get() : null;
            if ( cached != null )
            {
                return new ByteArrayInputStream( cached );
            }
            
            if ( !ProxyServer.cache.containsKey( key ) )
                return null;
            File f = new File( ProxyServer.CACHEPATH + headerLine.hashCode() );
            InputStream cs = null;
            try
            {
                cs = new BufferedInputStream( new FileInputStream( f ) );
                byte[] hdr = new byte[ headerLine.length() + 2 ];
                try
                {
                    DataInputStream bis = new DataInputStream( cs );
                    bis.readFully( hdr );
                    if ( !new String( hdr ).equals( headerLine + CRLF ) )
                    {
                        return null;
                    }
                    if ( !ProxyServer.USE_MEMCACHE )
                        return bis;
                    cached = new byte[ (int)(f.length() - hdr.length) ];
                    bis.readFully( cached );
                    bis.close();
                    cs.close();
                    ProxyServer.memCache.put( headerLine, new SoftReference<byte[]>( cached ) );
                    return new ByteArrayInputStream( cached );
                }
                catch ( IOException e )
                {
                    try
                    {
                        cs.close();
                    }
                    catch ( IOException e1 )
                    {
                    }
                    return null;
                }
            }
            catch ( FileNotFoundException e )
            {
            }
            return cs;
        }
        
        private static void sendBytes( InputStream fis, OutputStream os ) throws Exception
        {
            // Construct a 1K buffer to hold bytes on their way to the socket.
            byte[] buffer = new byte[ 1024 ];
            int bytes = 0;

            // Copy requested file into the socket's output stream.
            while ( (bytes = fis.read( buffer )) != -1 )
            {
                os.write( buffer, 0, bytes );
            }
        }
        
        private static void sendAndCacheBytes( InputStream connection, OutputStream os, String params ) throws Exception
        {
            File f = new File( ProxyServer.CACHEPATH + params.hashCode() );
            FileOutputStream cs = new FileOutputStream( f );
            // Construct a 1K buffer to hold bytes on their way to the socket.
            byte[] buffer = new byte[ 1024 ];
            int bytes = 0;
            try
            {
                cs.write( (params + CRLF).getBytes() );
                // Copy requested file into the socket's output stream.
                while ( (bytes = connection.read( buffer )) != -1 )
                {
                    os.write( buffer, 0, bytes );
                    cs.write( buffer, 0, bytes );
                }
                cs.close();
                ProxyServer.cache.put( Integer.toString( params.hashCode() ), f );
            }
            catch ( Throwable e )
            {
                cs.close();
                f.delete();
            }
            connection.close();
        }
    }
}
