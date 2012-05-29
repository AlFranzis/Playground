package al.franzis.jfanotify;

public class FanotifyCom {
	static {
		System.loadLibrary("jfanotify");
	}
	
	public native int init();
	
	public native void add(int fanotifyFd, String filename);
	
	public native FanotifyEvent read(int fanotifyFd);
	
	public native void write(int fanotifyFd, FanotifyResponse response);
}
