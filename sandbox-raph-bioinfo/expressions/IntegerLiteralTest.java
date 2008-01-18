package expressions;

import static org.junit.Assert.*;
import org.junit.*;

public class IntegerLiteralTest {

	private static IntegerLiteral literal_1, otherLiteral_1, literal_123;
	private static Expression nonLiteral;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		literal_1 = new IntegerLiteral(1);
		otherLiteral_1 = new IntegerLiteral(1);
		literal_123 = new IntegerLiteral(123);
		nonLiteral = new MemoryCell(1000);
	}
	
	@Test public void extendedConstructor_SingleCase() {
		IntegerLiteral theLiteral = new IntegerLiteral(10);
		assertEquals(10L,theLiteral.getValue());
	}

	@Test public void defaultConstructor_SingleCase() {
		IntegerLiteral theLiteral = new IntegerLiteral();
		assertEquals(0L,theLiteral.getValue());
	}

	@Test public void ZERO_SingleCase() {
		IntegerLiteral zeroLiteral = IntegerLiteral.ZERO;
		assertNotNull(zeroLiteral);
		assertEquals(0L,zeroLiteral.getValue());
	}
	
	@Test public void equals_IdenticalLiterals() {
		assertTrue(literal_1.equals(otherLiteral_1));
	}
	
	@Test public void equals_testNonIdenticalLiterals() {
		assertFalse(literal_1.equals(literal_123));
	}
	
	@Test public void equals_NonEffectiveLiteral() {
		assertFalse(literal_1.equals(null));
	}
	
	@Test public void equals_NonLiteral() {
		assertFalse(literal_1.equals(nonLiteral));
	}
	
	@Test public void isMutable_SingleCase() {
		assertFalse(literal_1.isMutable());
	}
	
	@Test public void toString_SingleCase() {
		assertEquals(Long.valueOf(123).toString(),literal_123.toString());
		// Assertion added to check correctness of specification.
		assertEquals("123",literal_123.toString());
	}

}
