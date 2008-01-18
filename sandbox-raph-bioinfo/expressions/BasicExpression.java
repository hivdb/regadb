package expressions;

/**
 * A class of basic arithmetic expressions.
 *   A basic expression does not involve any operators. Its
 *   value can be obtained without any computations.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public abstract class BasicExpression extends Expression {

	/**
	 * Check whether this basic expression has the given expression as
	 * one of its subexpressions.
	 *
	 * @return  True if the given expression is the same expression as this
	 *          basic expression; false otherwise.
	 *          | result == (expression == this)
	 */
	@Override public final boolean hasAsSubExpression(Expression expression) {
		return expression == this;
	}

	/**
	 * Return the postfix notation of this basic expression.
	 *
	 * @return	The textual representation of this basic expression.
	 *			| result.equals(this.toString())
	 */
	@Override public String toPostfix() {
		return toString();
	}

}