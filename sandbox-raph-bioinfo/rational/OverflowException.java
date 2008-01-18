package rational;

/**
 * A class for signalling overflows in arithmetic operations.
 * 
 * @version 2.0
 * @author Eric Steegmans
 */
public abstract class OverflowException extends Exception {

	/**
	 * Initialize this new overflow exception with given operands.
	 * 
	 * @param left
	 *            The left operand involved in this new overflow exception.
	 * @param right
	 *            The right operand involved in this new overflow exception.
	 * @post The left operand of this new overflow exception is equal to the
	 *       given left operand. | new.getLeftOperand() == left
	 * @post The right operand of this new overflow exception is equal to the
	 *       given right operand. | new.getRightOperand() == right
	 */
	public OverflowException(long left, long right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * Return the left operand involved in this overflow exception.
	 */
	public long getLeftOperand() {
		return this.left;
	}

	/**
	 * Return the right operand involved in this overflow exception.
	 */
	public long getRightOperand() {
		return this.right;
	}

	/**
	 * Variables registering the left operand, respectively the right operand
	 * for this overflow exception.
	 */
	private final long left;

	private final long right;

	/**
	 * The Java API strongly recommends to explicitly define a version number
	 * for classes that implement the interface Serializable. At this stage,
	 * that aspect is of no concern to us.
	 */
	private static final long serialVersionUID = 2003001L;

}