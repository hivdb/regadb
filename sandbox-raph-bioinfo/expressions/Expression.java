package expressions;

/**
 * A class of arithmetic expressions for computing integer
 * values.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public abstract class Expression implements Cloneable {

	/**
	 * Check whether this expression has the given expression as one
	 * of its subexpressions.
	 *
	 * @param   expression
	 *          The expression to be checked.
	 * @return  True if the given expression is the same expression as this
	 *          expression.
	 *          | if (expression == this)
	 *          |   then result == true
	 * @return  False if the given expression is not effective.
	 *          | if (expression == null)
	 *          |   then result == false
	 * @note    This method illustrates partial specifications of methods. At this
	 *          level, the effect of this method is only defined in 2 cases. All
	 *          other cases must be worked out at the lower levels of the hierarchy.
	 */
	public abstract boolean hasAsSubExpression(Expression expression);

	/**
	 * Return the value of this expression.
	 */
	public abstract long getValue();

	/**
	 * Check whether the state of this expression can be changed.
	 * 
	 * @note    This inspector is best avoided in languages fully supporting
	 *          multiple inheritance. Then, mutable expression will inherit
	 *          form an abstract class of mutable expressions introducing a.o.
	 *          a method set Value, and a preliminary definition of cloning.
	 *          An abstract class of immutable expressions might also be defined
	 *          then, introducing a final version of cloning.
	 */
	public abstract boolean isMutable();

	/**
	 * Check whether this expression is equal to the given object.
	 * 
	 * @return  True if the given object is the same as this expression.
	 *          | if (this == other)
	 *          |   then result == true
	 * @return  False if this expression and the given object do not
	 *          belong to the same (concrete) class.
	 *          | if (getClass() != object.getClass())
	 *          |   then result == false
	 * @return  False if the given object is an expression and its value
	 *          differs from the value of this expression differs.
	 *          | if ( (other instanceof Expression) &&
	 *          |      (getValue() != ((Expression)other).getValue()) )
	 *          |   then result == false
	 */
	@Override public abstract boolean equals(Object other);

	/**
	 * Return a clone of this expression.
	 * 
	 * @return  The resulting expression is effective and equals to this expression.
	 *          | (result != null) && (result.equals(this))
	 * @return  The resulting expression is the same as this expression
	 *          if and only if this expression is immutable.
	 *          | (result == this) == (! this.isMutable())
	 */
	@Override public Expression clone() {
		try {
			if (isMutable())
				return (Expression) super.clone();
			else
				return this;
		}
		catch (CloneNotSupportedException exc) {
			assert false;
			return null;
		}
	}

	/**
	 * Return the hash code of this expression.
	 */
	@Override public int hashCode() {
		return (int) getValue();
	}

	/**
	 * Return a textual representation of this expression.
	 *
	 * @return  The resulting string is effective and non-empty.
	 *          | (result != null) && (result.length() > 0)
	 */
	@Override public abstract String toString();

	/**
	 * Return the postfix notation of this expression.
	 *
	 * @return  The resulting string is effective and non-empty.
	 *          | (result != null) && (result.length() > 0)
	 */
	public abstract String toPostfix();

}