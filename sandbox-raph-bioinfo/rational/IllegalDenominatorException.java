package rational;

/**
 * A class for signalling illegal denominators for rational numbers.
 * 
 * @version 2.0
 * @author Eric Steegmans
 */
public class IllegalDenominatorException extends Exception {

	/**
	 * Initialize this new illegal denominator exception with given value.
	 * 
	 * @param value
	 *            The value for the new illegal denominator exception.
	 * @post The value of the new illegal denominator exception is set to the
	 *       given value. | new.getValue() == value
	 */
	public IllegalDenominatorException(long value) {
		this.value = value;
	}

	/**
	 * Return the value registered for this illegal denominator exception.
	 */
	public long getValue() {
		return this.value;
	}

	/**
	 * Variable registering the value involved in this illegal denominator
	 * exception.
	 */
	private final long value;

	/**
	 * The Java API strongly recommends to explicitly define a version number
	 * for classes that implement the interface Serializable. At this stage,
	 * that aspect is of no concern to us.
	 */
	private static final long serialVersionUID = 2003001L;

}