package bundleb;

import bundlea.BundleAService;

public class BundleBServiceConsumer
{
    
    public String getMessage( String name )
    {
        BundleAService bundleAService = BundleAService.getInstance();
        return bundleAService.getMessage( name );
    }
    
}
