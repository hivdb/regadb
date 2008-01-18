package expressions;

import static org.junit.Assert.*;
import org.junit.*;
import expressions.exceptions.IllegalOperandException;

public class NegationTest {

	private static Negation negation_10;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		negation_10 = new Negation(new IntegerLiteral(10));
	}

	@Test public void constructor_LegalCase() throws Exception {
		Expression theOperand = new IntegerLiteral(1);
		Negation theNegation = new Negation(theOperand);
		assertSame(theOperand, theNegation.getOperand());
	}

	@Test(expected = IllegalOperandException.class) public void constructor_IllegalCase()
			throws Exception {
		new Negation(null);
	}

	@Test public void getValue_SingleCase() {
		assertEquals(-10L, negation_10.getValue());
	}

	@Test public void getOperatorSymbol_SingleCase() {
		assertEquals("-", negation_10.getOperatorSymbol());
	}

}
