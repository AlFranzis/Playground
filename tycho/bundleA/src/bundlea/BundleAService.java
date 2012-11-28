package bundlea;

public class BundleAService
{
    private static final BundleAService INSTANCE = new BundleAService();
    
    public static BundleAService getInstance()
    {
        return INSTANCE;
    }
    
    public String getMessage( String name )
    {
        return "Hello " + name + " !";
    }
}
