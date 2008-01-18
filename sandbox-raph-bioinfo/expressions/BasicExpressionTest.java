package expressions;


import static org.junit.Assert.*;

import org.junit.*;


public class BasicExpressionTest {

	private static BasicExpression literal_1, otherLiteral_1;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		literal_1 = new IntegerLiteral(1);
		otherLiteral_1 = new IntegerLiteral(1);
	}
	
	@Test public void hasAsSubExpression_SameExpression() {
		assertTrue(literal_1.hasAsSubExpression(literal_1));
	}
	
	@Test public void hasAsSubExpression_OtherExpression() {
		assertFalse(literal_1.hasAsSubExpression(otherLiteral_1));
	}
	
	@Test public void toPostfix_SingleCase() {
		assertEquals(literal_1.toString(),literal_1.toPostfix());
	}

}
