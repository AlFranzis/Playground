package bundlec;

import org.apache.log4j.Logger;

public class Log4JClient
{
    public void useLogger()
    {
        Logger logger = Logger.getLogger( Log4JClient.class );
        logger.info( "Hello from log4j" );
    }
}
