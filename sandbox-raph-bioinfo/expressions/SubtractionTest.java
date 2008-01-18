package expressions;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.BeforeClass;
import org.junit.Test;

import expressions.exceptions.IllegalOperandException;


public class SubtractionTest {


	private static Subtraction subtraction_5_2;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		subtraction_5_2 = new Subtraction(new IntegerLiteral(5),
			new IntegerLiteral(2));
	}

	@Test public void constructor_LegalCase() throws Exception {
		Expression theLeftOperand = new IntegerLiteral(1);
		Expression theRightOperand = new IntegerLiteral(2);
		Subtraction theSubtraction = new Subtraction(theLeftOperand, theRightOperand);
		assertSame(theLeftOperand, theSubtraction.getLeftOperand());
		assertSame(theRightOperand, theSubtraction.getRightOperand());
	}

	@Test(expected = IllegalOperandException.class) public void constructor_IllegalLeftOperand()
			throws Exception {
		new Subtraction(null, new IntegerLiteral(1));
	}

	@Test(expected = IllegalOperandException.class) public void constructor_IllegalRightOperand()
			throws Exception {
		new Subtraction(new IntegerLiteral(1), null);
	}

	@Test public void getValue_SingleCase() {
		assertEquals(3L, subtraction_5_2.getValue());
	}

	@Test public void testSingleCase() {
		assertEquals("-", subtraction_5_2.getOperatorSymbol());
	}


}
