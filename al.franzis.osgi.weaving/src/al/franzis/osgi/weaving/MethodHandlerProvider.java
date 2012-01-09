package al.franzis.osgi.weaving;


public abstract class MethodHandlerProvider {
	private final static MethodHandlerProvider INSTANCE = new EclipseMethodHandlerProvider();
	
	public static MethodHandlerProvider getInstance() {
		return INSTANCE;
	}
	
	public abstract IMethodInvocationHandler getHandler(int index);
}
