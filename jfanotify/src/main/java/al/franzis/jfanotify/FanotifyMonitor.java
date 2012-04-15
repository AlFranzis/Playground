package al.franzis.jfanotify;

import java.util.List;
import java.util.Vector;

public class FanotifyMonitor {
	private final int fanotifyFd;
	private final FanotifyCom fanotifyCom;
	private final List<IFanotifyListener> listeners = new Vector<IFanotifyListener>(); 
	
	public FanotifyMonitor() {
		this.fanotifyCom = new FanotifyCom();
		this.fanotifyFd = fanotifyCom.init();
	}
	
	public FanotifyEvent read() {
		return fanotifyCom.read(fanotifyFd);
	}
	
	public void write(FanotifyResponse response) {
		
	}
	
	public void close(FanotifyEvent event) {
		
	}
	
	public void monitor() {
		while( true ) {
			FanotifyEvent event = read();
			for(IFanotifyListener listener : listeners) {
				FanotifyResponse response = listener.notify(event);
				if(response != null)
					write(response);
			}
			close(event);
		}
	}
	
	public boolean addListener( IFanotifyListener listener, IFanotifyListenerCriteria criteria) {
		return listeners.add(listener);
	}
	
	public boolean removeListener(IFanotifyListener listener) {
		return listeners.remove(listener);
	}
	
	public boolean addMark(FanotifyMark mark) {
		String filename = mark.getFilename();
		fanotifyCom.add(fanotifyFd, filename);
		return true;
	}
	
	public void shutdown() {
		
	}
	
	
	
	
}
