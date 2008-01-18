package rational;

/**
 * A class for signalling exceptions in multiplications.
 * 
 * @version 2.0
 * @author Eric Steegmans
 */
public class TimesOverflowException extends OverflowException {

	/**
	 * Initialize this new times overflow exception with given operands.
	 * 
	 * @param left
	 *            The left operand involved in this new times overflow
	 *            exception.
	 * @param right
	 *            The right operand involved in this new times overflow
	 *            exception.
	 * @effect This new times overflow exception is initialized in the same way
	 *         an overflow exception is initialized with the given left operand
	 *         and given right operand. | super(left,right)
	 */
	public TimesOverflowException(long left, long right) {
		super(left, right);
	}

	/**
	 * The Java API strongly recommends to explicitly define a version number
	 * for classes that implement the interface Serializable. At this stage,
	 * that aspect is of no concern to us.
	 */
	private static final long serialVersionUID = 2003001L;

}
