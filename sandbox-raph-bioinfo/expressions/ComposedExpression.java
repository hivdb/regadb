package expressions;

import be.kuleuven.cs.som.annotate.Raw;

/**
 * A class of composed arithmetic expressions.
 *   A composed expression involves a series of operators applied
 *   to operands.
 * 
 * @invar    Each composed expression must can have its number of
 *           operands as number of operands.
 *           | canHaveAsNbOperands(getNbOperands())
 * @invar    Each composed expression must can have each of its
 *           operands as an operand.
 *           | for each I in 1..getNbOperands():
 *           |   canHaveAsOperand(getOperandAt(I))
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public abstract class ComposedExpression extends Expression {

	/**
	 * Check whether this composed expression is equal to 
	 * the given object.
	 *
	 * @return  True if the given object is effective, if the given
	 *          object belongs to the same (concrete) class as this
	 *          composed expression, if both composed expressions have
	 *          the same number of operands, and if all corresponding
	 *          operands of both composed expressions are equal;
	 *          false otherwise.
	 *          | result ==
	 *          |   (other != null) &&
	 *          |   (getClass() == other.getClass()) &&
	 *          |   (getNbOperands() == ((ComposedExpression)other).getNbOperands()) &&
	 *          |   ( for each I in 1..getNbOperands():
	 *          |       getOperandAt(I).equals(((ComposedExpression)other.getOperandAt(I)) )
	 */
	@Override public boolean equals(Object other) {
		if ((other == null) || (getClass() != other.getClass()))
			return false;
		ComposedExpression otherExpr = (ComposedExpression) other;
		if (getNbOperands() != otherExpr.getNbOperands())
			return false;
		for (int pos = 1; pos <= getNbOperands(); pos++)
			if (!getOperandAt(pos).equals(otherExpr.getOperandAt(pos)))
				return false;
		return true;
	}

	/**
	 * Check whether the state of this composed expression can be
	 * changed.
	 * 
	 * @return  True if the state of at least one of the operands of
	 *          this composed expression can be changed; false
	 *          otherwise.
	 *          | result ==
	 *          |   (for some I in 1..getNbOperands():
	 *          |      getOperandAt(I).isMutable() )
	 */
	public boolean isMutable() {
		for (int i = 1; i <= getNbOperands(); i++)
			if (getOperandAt(i).isMutable())
				return true;
		return false;
	}

	/**
	 * Return a clone of this composed expression.
	 * 
	 * @return  Each operand of the resulting composed expression is the
	 *          same as the corresponding operand of this expression, if
	 *          and only if that operand is immutable.
	 *          | for each I in 1..getNbOperands():
	 *          |   ( (result.getOperandAt(I) == this.getOperandAt(I)) ==
	 *          |       (! this.getOperandAt(I).isMutable()) )
	 */
	@Override public ComposedExpression clone() {
		ComposedExpression result = (ComposedExpression) super.clone();
		if (isMutable())
			for (int i = 1; i <= getNbOperands(); i++)
				if (getOperandAt(i).isMutable())
					setOperandAt(i, getOperandAt(i).clone());
		return result;
	}

	/**
	 * Return the number of operands involved in this composed
	 * expression.
	 */
	@Raw public abstract int getNbOperands();

	/**
	 * Check whether this composed expression can have the given
	 * number of operands as its number of operands.
	 *
	 * @param   nbOperands
	 *          The number of operands to check.
	 * @return  False if the given number is not positive; undefined
	 *          otherwise.
	 *          | if (number <= 0)
	 *          |   then result == false
	 * @note    It is important not to define this method as a static method.
	 *          In that case, we would not be able to strengthen its definition
	 *          in e.g. BinaryExpression.
	 */
	@Raw public boolean canHaveAsNbOperands(int nbOperands) {
		return nbOperands > 0;
	}

	/**
	 * Return the operand of this composed expression at the given index.
	 *
	 * @param   index
	 *          The index of the requested operand.
	 * @throws  IndexOutOfBoundsException [must]
	 *          The given index is not positive or exceeds the
	 *          number of operands for this composed expression.
	 *          | (index < 1) || (index > getNbOperands())
	 */
	@Raw public abstract Expression getOperandAt(int index)
			throws IndexOutOfBoundsException;

	/**
	 * Check whether this composed expression can have the given
	 * expression as one of its operands.
	 *
	 * @param   expression
	 *          The expression to check.
	 * @return  True if the given expression is effective, and if
	 *          that expression does not have this composed expression
	 *          as a subexpression; false otherwise.
	 *          | result ==
	 *          |   ( (expression != null) &&
	 *          |     (! expression.hasAsSubExpression(this)) )
	 */
	public boolean canHaveAsOperand(Expression expression) {
		try {
			return !expression.hasAsSubExpression(this);
		}
		catch (NullPointerException exc) {
			assert expression == null;
			return false;
		}
	}

	/**
	 * Set the operand for this composed expression at the given
	 * index to the given operand.
	 * 
	 * @param   index
	 *          The index at which the operand must be registered.
	 * @param   operand
	 *          The operand to be registered.
	 * @pre     The given index is positive and does not exceed the
	 *          number of operands for this composed expression.
	 *          | (index > 0) && (index <= getNbOperands())
	 * @pre     This expression must can have the given operand as
	 *          one of its operands.
	 *          | canHaveAsOperand(operand)
	 * @post    The operand at the given index of this composed
	 *          expression is the same as the given operand.
	 *          | new.getOperandAt(index) == operand
	 */
	protected abstract void setOperandAt(int index, Expression operand);

	/**
	 * Check whether this composed expression has the given expression
	 * as one of its subexpressions.
	 *
	 * @return  True if the given expression is the same expression as this
	 *          composed expression, or if the given expression is a
	 *          subexpression of one of the operands of this composed
	 *          expression; false otherwise.
	 *          | result ==
	 *          |   (expression == this) ||
	 *          |   ( for some I in 1..getNbOperands():
	 *          |       getOperandAt(I).hasAsSubExpression(expression) )
	 */
	@Override public boolean hasAsSubExpression(Expression expression) {
		if (expression == this)
			return true;
		for (int pos = 1; pos <= getNbOperands(); pos++)
			if (getOperandAt(pos).hasAsSubExpression(expression))
				return true;
		return false;
	}

	/**
	 * Return the symbol representing the operator of this composed
	 * expression.
	 * 
	 * @return  An effective, non-empty string.
	 *          | (result != null) && (result.length() > 0)
	 */
	public abstract String getOperatorSymbol();

	/**
	 * Return the postfix notation of this composed expression.
	 *
	 * @return  The postfix notation of the operands of this composed
	 *          expression, followed by the symbol representing the
	 *          operator of this composed expression. Successive operands
	 *          are separated by spaces.
	 * @note    The formal specification language is not expressive enough
	 *          to work out a formal specification for this method.
	 */
	@Override public String toPostfix() {
		String result = "";
		for (int i = 1; i <= getNbOperands(); i++)
			result += getOperandAt(i).toPostfix() + " ";
		return result + getOperatorSymbol();
	}

}