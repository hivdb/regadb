package expressions;

import static org.junit.Assert.*;
import org.junit.*;
import expressions.exceptions.IllegalOperandException;

public class AdditionTest {

	private static Addition addition_3_2;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		addition_3_2 = new Addition(new IntegerLiteral(3),
			new IntegerLiteral(2));
	}

	@Test public void constructor_LegalCase() throws Exception {
		Expression theLeftOperand = new IntegerLiteral(1);
		Expression theRightOperand = new IntegerLiteral(2);
		Addition theAddition = new Addition(theLeftOperand, theRightOperand);
		assertSame(theLeftOperand, theAddition.getLeftOperand());
		assertSame(theRightOperand, theAddition.getRightOperand());
	}

	@Test(expected = IllegalOperandException.class) public void constructor_IllegalLeftOperand()
			throws Exception {
		new Addition(null, new IntegerLiteral(1));
	}

	@Test(expected = IllegalOperandException.class) public void constructor_IllegalRightOperand()
			throws Exception {
		new Addition(new IntegerLiteral(1), null);
	}

	@Test public void getValue_SingleCase() {
		assertEquals(5L, addition_3_2.getValue());
	}

	@Test public void testSingleCase() {
		assertEquals("+", addition_3_2.getOperatorSymbol());
	}

}
