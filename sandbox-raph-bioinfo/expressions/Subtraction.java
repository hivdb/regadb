package expressions;

import expressions.exceptions.*;

/**
 * A class of binary expressions, representing the subtraction of the
 * operand at the right-hand side form the operand at the left-hand
 * side.
 */

public class Subtraction extends BinaryExpression {

	/**
	 * Initialize this new subtraction with given operands.
	 *
	 * @param	left
	 *			The left operand for this new subtraction.
	 * @param	right
	 *			The right operand for this new subtraction.
	 * @post	The given left operand is registered as the left
	 * 			operand for this new subtraction.
	 *			| new.getLeftOperand() == left
	 * @post	The given right operand is registered as the right
	 * 			operand for this new subtraction.
	 *			| new.getRightOperand() == right
	 * @throws	IllegalOperandException
	 *          The given left operand or the given right operand is not a
	 *			proper operand for this new subtraction.
	 *			| (! canHaveAsOperand(left)) ||
	 *			| (! canHaveAsOperand(right))
	 */
	public Subtraction(Expression left, Expression right)
			throws IllegalOperandException {
		super(left, right);
	}

	/**
	 * Return the value of this subtraction.
	 *
	 * @return	The difference between the value of the left operand
	 *			and the right operand of this subtraction.
	 *			| result ==
	 *			|	getLeftOperand().getValue() - 
	 *			|	getRightOperand().getValue()
	 */
	@Override public long getValue() {
		return getLeftOperand().getValue() - getRightOperand().getValue();
	}

	/**
	 * Return the symbol representing the operator of this subtraction.
	 * 
	 * @return  The string "-"
	 *          | result.equals("-")
	 */
	@Override public String getOperatorSymbol() {
		return "-";
	}

}