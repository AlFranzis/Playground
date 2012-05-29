package al.franzis.jfanotify;

public class FanotifyEvent {
	private String filename;
	private int mask;
	
	public FanotifyEvent() {}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getMask() {
		return mask;
	}

	public void setMask(int mask) {
		this.mask = mask;
	}
	
}