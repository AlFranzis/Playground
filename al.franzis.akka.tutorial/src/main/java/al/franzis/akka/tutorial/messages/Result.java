package al.franzis.akka.tutorial.messages;

/**
 * Message sent from Worker(s) to Master to transfer (partial)
 * calculation result of Pi.
 * 
 * @author alex
 */
public class Result {
	private final double value;

	public Result(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}
}