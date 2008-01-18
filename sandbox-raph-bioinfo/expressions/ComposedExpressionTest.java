package expressions;

import static org.junit.Assert.*;

import org.junit.*;

public class ComposedExpressionTest {

	private static ComposedExpression someComposedExpression,
			identicalExpression, nonIdenticalExpression,
			expressionWithDifferentNbOperands, mutableComposedExpression,
			immutableComposedExpression;

	private static Expression otherTypeExpression;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		someComposedExpression = new Multiplication(new IntegerLiteral(1),
			new IntegerLiteral(2));
		identicalExpression = new Multiplication(new IntegerLiteral(1),
			new IntegerLiteral(2));
		nonIdenticalExpression = new Multiplication(new IntegerLiteral(2),
			new IntegerLiteral(2));
		expressionWithDifferentNbOperands = new Multiplication(new Addition(
			new IntegerLiteral(1), new IntegerLiteral(2)),
			new IntegerLiteral(2));
		mutableComposedExpression = new Addition(new IntegerLiteral(1),
			new MemoryCell(200));
		immutableComposedExpression = new Addition(new IntegerLiteral(1),
			new IntegerLiteral(2));
		otherTypeExpression = new Addition(new IntegerLiteral(1),
			new IntegerLiteral(2));
	}

	@Test public void equals_IdenticalComposedExpressions() {
		assertTrue(someComposedExpression.equals(identicalExpression));
	}

	@Test public void equals_ExpressionsWithDifferentOperands() {
		assertFalse(someComposedExpression.equals(nonIdenticalExpression));
	}

	@Test public void equals_ExpressionsWithDifferentNbOperands() {
		assertFalse(someComposedExpression
			.equals(expressionWithDifferentNbOperands));
	}

	@Test public void equals_NonEffectiveExpression() {
		assertFalse(someComposedExpression.equals(null));
	}

	@Test public void equalsDifferentKindExpression() {
		assertFalse(someComposedExpression.equals(otherTypeExpression));
	}

	@Test public void isMutable_TrueCase() {
		assertTrue(mutableComposedExpression.isMutable());
	}

	@Test public void isMutable_FalseCase() {
		assertFalse(immutableComposedExpression.isMutable());
	}
	
	@Test public void clone_MutableExpression() {
		ComposedExpression clone = mutableComposedExpression.clone();
		assertNotNull(clone);
		assertEquals(mutableComposedExpression,clone);
		assertNotSame(mutableComposedExpression,clone);
		for (int i=1; i<=mutableComposedExpression.getNbOperands(); i++)
			if (mutableComposedExpression.getOperandAt(i).isMutable())
				assertNotSame(mutableComposedExpression.getOperandAt(i),clone.getOperandAt(i));
			else
				assertSame(mutableComposedExpression.getOperandAt(i),clone.getOperandAt(i));
	}
	
	@Test public void clone_ImmutableExpression() {
		ComposedExpression clone = immutableComposedExpression.clone();
		assertNotNull(clone);
		assertEquals(immutableComposedExpression,clone);
		assertSame(immutableComposedExpression,clone);
		for (int i=1; i<=immutableComposedExpression.getNbOperands(); i++)
				assertSame(immutableComposedExpression.getOperandAt(i),clone.getOperandAt(i));
	}
	
	@Test public void canHaveAsNbOperands_FalseCase() {
		assertFalse(someComposedExpression.canHaveAsNbOperands(0));
	}
	
	@Test(expected=IndexOutOfBoundsException.class) public void getOperandAt_NonPositiveIndex() throws Exception {
		someComposedExpression.getOperandAt(0);
	}
	
	@Test(expected=IndexOutOfBoundsException.class) public void getOperandAt_IndexTooHigh() throws Exception {
		someComposedExpression.getOperandAt(someComposedExpression.getNbOperands()+1);
	}
	
	@Test public void canHaveAsOperand_TrueCase() {
		assertTrue(someComposedExpression.canHaveAsOperand(new IntegerLiteral(10)));
	}
	
	@Test public void canHaveAsOperand_NonEffectiveOperand() {
		assertFalse(someComposedExpression.canHaveAsOperand(null));
	}
	
	@Test public void canHaveAsOperand_CyclicOperand() throws Exception {
		ComposedExpression moreComposedExpression = new Addition(someComposedExpression,new IntegerLiteral(50));
		assertFalse(someComposedExpression.canHaveAsOperand(moreComposedExpression));
	}
	
	@Test public void hasAsSubExpression_SameExpression() {
		assertTrue(someComposedExpression.hasAsSubExpression(someComposedExpression));
	}
	
	@Test public void hasAsSubExpression_SubExpressionOfOperand() {
		Expression operand = someComposedExpression.getOperandAt(1);
		assertTrue(someComposedExpression.hasAsSubExpression(operand));
	}
	
	@Test public void hasAsSubExpression_NonOperand() throws Exception {
		assertFalse(someComposedExpression.hasAsSubExpression(new IntegerLiteral(20)));
	}
	
	@Test public void getOperatorSymbol_SingleCase() throws Exception {
		assertNotNull(someComposedExpression.getOperatorSymbol());
		assertTrue(someComposedExpression.getOperatorSymbol().length() > 0);
	}

	@Test public void testUnaryExpression() throws Exception {
		ComposedExpression unaryExpression = new Negation(new IntegerLiteral(1));
		assertEquals("1 -", unaryExpression.toPostfix());
	}

	@Test public void testBinaryExpression() throws Exception {
		ComposedExpression unaryExpression = new Negation(new IntegerLiteral(1));
		ComposedExpression binaryExpression = new Addition(new Multiplication(
			new IntegerLiteral(1), new IntegerLiteral(2)), unaryExpression);
		assertEquals("1 2 * 1 - +", binaryExpression.toPostfix());
	}

}
