package al.franzis.jfanotify;

import java.io.File;


public class Fanotify {
	private FanotifyMonitor monitor;
	
	public static Fanotify getInstance() {
		return new Fanotify();
	}
	
	private Fanotify() {
		monitor = new FanotifyMonitor();
	}
	
	public boolean addMark(FanotifyMark mark) {
		return monitor.addMark(mark);
	}
	
	public boolean removeMark(File filename) {
		return false;
	}
	
	public boolean addListener( IFanotifyListener listener, IFanotifyListenerCriteria criteria) {
		return monitor.addListener(listener, criteria);
	}
	
	public boolean removeListener(IFanotifyListener listener) {
		return monitor.removeListener(listener);
	}
	
	public void start() {
		monitor.monitor();
	}
	
	public void shutdown() {
		monitor.shutdown();
	}
}
