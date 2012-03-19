package al.franzis.akka.tutorial.messages;

/**
 * Message sent from Master to Worker(s) to request a partial
 * calculation of Pi.
 * 
 * @author alex
 */
public class Work {
	private final int start;
	private final int nrOfElements;

	public Work(int start, int nrOfElements) {
		this.start = start;
		this.nrOfElements = nrOfElements;
	}

	public int getStart() {
		return start;
	}

	public int getNrOfElements() {
		return nrOfElements;
	}
}