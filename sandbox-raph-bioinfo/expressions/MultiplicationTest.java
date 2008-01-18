package expressions;

import static org.junit.Assert.*;
import org.junit.*;
import expressions.exceptions.IllegalOperandException;

public class MultiplicationTest {
	
	private static Multiplication multiplication_3_4;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		multiplication_3_4 = new Multiplication(new IntegerLiteral(3),new IntegerLiteral(4));
	}

	@Test public void constructor_LegalCase() throws Exception {
		Expression theLeftOperand = new IntegerLiteral(1);
		Expression theRightOperand = new IntegerLiteral(2);
		Multiplication theMultiplication = new Multiplication(theLeftOperand,
			theRightOperand);
		assertSame(theLeftOperand, theMultiplication.getLeftOperand());
		assertSame(theRightOperand, theMultiplication.getRightOperand());
	}

	@Test(expected = IllegalOperandException.class) public void constructor_IllegalLeftOperand()
			throws Exception {
		new Multiplication(null, new IntegerLiteral(1));
	}

	@Test(expected = IllegalOperandException.class) public void constructor_IllegalRightOperand()
			throws Exception {
		new Multiplication(new IntegerLiteral(1), null);
	}
	
	@Test public void getValue_SingleCase() {
		assertEquals(12L,multiplication_3_4.getValue());
	}
	
	@Test public void testSingleCase() {
		assertEquals("*",multiplication_3_4.getOperatorSymbol());
	}

}
