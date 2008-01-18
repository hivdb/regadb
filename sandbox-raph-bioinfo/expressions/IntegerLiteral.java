package expressions;

import be.kuleuven.cs.som.annotate.Raw;

/**
 * A class of integer literals.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public class IntegerLiteral extends BasicExpression {

	/**
	 * Initialize this new integer literal with given value.
	 *
	 * @param   value
	 *          The value for this new integer literal.
	 * @post    The value of this new integer literal is equal to
	 *          the given value.
	 *          | new.getValue() == value
	 */
	public IntegerLiteral(long value) {
		this.value = value;
	}

	/**
	 * Initialize this new integer literal with value 0.
	 *
	 * @effect  This new integer literal is initialized with 0
	 *          as its value.
	 *          | this(0)
	 */
	public IntegerLiteral() {
		// We must explicitly initialize the final instance variable value in
		// this constructor, either in a direct way or in an indirect way.
		this(0);
	}

	/**
	 * Constant referencing a predefined integer literal with value 0.
	 * 
	 * @invar   The constant references an effective integer literal,
	 *          whose value is 0.
	 *          | (ZERO !=  null) && (ZERO.getValue() == 0)
	 */
	public final static IntegerLiteral ZERO = new IntegerLiteral();

	/**
	 * Return the value of this integer literal.
	 */
	@Override @Raw public long getValue() {
		return value;
	}

	/**
	 * Variable registering the value of this integer literal.
	 */
	private final long value;

	/**
	 * Check whether this integer literal is equal to the given
	 * object.
	 *
	 * @return	True if the other object is an effective integer literal,
	 *			whose value is equal to the value of this integer literal;
	 *			false otherwise.
	 *			| result ==
	 *			|	(other instanceof IntegerLiteral) &&
	 *			|	(this.getValue() == ((IntegerLiteral)other).getValue())
	 */
	@Override public boolean equals(Object other) {
		return (other instanceof IntegerLiteral)
			&& (getValue() == ((IntegerLiteral) other).getValue());
	}

	/**
	 * Check whether the state of this integer literal can be changed.
	 * 
	 * @return  Always false.
	 *          | result == false
	 */
	@Override public boolean isMutable() {
		return false;
	}

	/**
	 * Return a textual representation of this integer literal.
	 *
	 * @return  The textual representation of the value of this integer
	 *          literal as defined by the predefined class Long.
	 *          | result.equals(Long.toString(getValue())
	 */
	@Override public String toString() {
		return Long.toString(getValue());
	}

}