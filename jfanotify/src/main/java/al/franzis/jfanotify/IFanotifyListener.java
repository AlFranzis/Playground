package al.franzis.jfanotify;

public interface IFanotifyListener {
	public FanotifyResponse notify(FanotifyEvent event);
}
