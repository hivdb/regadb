package expressions;

import static org.junit.Assert.*;

import org.junit.*;

public class BinaryExpressionTest {

	private static BinaryExpression someBinaryExpression, addition_1_2,
			addition_3_addition12, addition_addition12_3,
			addition_addition12_addition12;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		someBinaryExpression = new Addition(new IntegerLiteral(1),
			new IntegerLiteral(2));
		addition_1_2 = new Addition(new IntegerLiteral(1),
			new IntegerLiteral(2));
		addition_3_addition12 = new Addition(new IntegerLiteral(3),
			addition_1_2);
		addition_addition12_3 = new Addition(addition_1_2,
			new IntegerLiteral(3));
		addition_addition12_addition12 = new Addition(addition_1_2,
			addition_1_2);
	}

	@Test public void getNbOperands_SingleCase() {
		assertEquals(2, someBinaryExpression.getNbOperands());
	}

	@Test public void canHaveAsNbOperands_TrueCase() {
		assertTrue(someBinaryExpression.canHaveAsNbOperands(2));
	}

	@Test public void canHaveAsNbOperands_FalseCase() {
		assertFalse(someBinaryExpression.canHaveAsNbOperands(1));
	}

	@Test public void getOperandAt_FirstOperand() throws Exception {
		Expression leftOperand = new IntegerLiteral(1);
		BinaryExpression theExpression = new Addition(leftOperand,
			new IntegerLiteral(2));
		assertSame(leftOperand, theExpression.getOperandAt(1));
	}

	@Test public void getOperandAt_SecondOperand() throws Exception {
		Expression rightOperand = new IntegerLiteral(2);
		BinaryExpression theExpression = new Addition(new IntegerLiteral(1),
			rightOperand);
		assertSame(rightOperand, theExpression.getOperandAt(2));
	}

	@Test(expected = IndexOutOfBoundsException.class) public void getOperandAt_IndexTooLow()
			throws Exception {
		someBinaryExpression.getOperandAt(0);
	}

	@Test(expected = IndexOutOfBoundsException.class) public void getOperandAt_IndexTooHigh()
			throws Exception {
		someBinaryExpression.getOperandAt(3);
	}

	@Test public void setOperandAt_FirstOperand() throws Exception {
		Expression leftOperand = new IntegerLiteral(1);
		someBinaryExpression.setOperandAt(1, leftOperand);
		assertSame(leftOperand, someBinaryExpression.getOperandAt(1));
	}

	@Test public void setOperandAt_SecondOperand() throws Exception {
		Expression rightOperand = new IntegerLiteral(2);
		someBinaryExpression.setOperandAt(2, rightOperand);
		assertSame(rightOperand, someBinaryExpression.getOperandAt(2));
	}

	@Test public void toString_SimpleOperands() {
		assertEquals("1+2", addition_1_2.toString());
	}

	@Test public void toString_SimpleLeftOperand() {
		assertEquals("3+(1+2)", addition_3_addition12.toString());
	}

	@Test public void toString_SimpleRightOperand() {
		assertEquals("(1+2)+3", addition_addition12_3.toString());
	}

	@Test public void toString_ComplexOperands() {
		assertEquals("(1+2)+(1+2)", addition_addition12_addition12.toString());
	}

}
