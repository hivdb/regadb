package expressions;

import static org.junit.Assert.*;

import org.junit.*;

import expressions.exceptions.IllegalAddressException;

public class MemoryCellTest {

	private MemoryCell cell_100, otherCell_100, someCell;

	private static Expression nonCell;
	
	@BeforeClass public static void setUpBeforeClass() throws Exception {
		nonCell = new IntegerLiteral(1000);
	}

	@Before public void setUp() throws Exception {
		cell_100 = new MemoryCell(100);
		otherCell_100 = new MemoryCell(100);
		someCell = new MemoryCell(100);
	}

	@Test public void constructor_LegalCase() throws Exception {
		MemoryCell theCell = new MemoryCell(1000);
		assertEquals(1000, theCell.getAddress());
		assertEquals(0L, theCell.getValue());
	}

	@Test(expected = IllegalAddressException.class) public void testIllegalCase()
			throws Exception {
		new MemoryCell(-1);
	}
	
	@Test public void equals_SameCell() {
		assertTrue(cell_100.equals(cell_100));
	}
	
	@Test public void equals_IdenticalCells() {
		assertTrue(cell_100.equals(otherCell_100));
	}
	
	@Test public void equals_DifferentAddresses() throws Exception {
		assertFalse(cell_100.equals(new MemoryCell(200)));
	}
	
	@Test public void equals_DifferentValues() throws Exception {
		cell_100.setValue(200);
		assertFalse(cell_100.equals(otherCell_100));
	}
	
	@Test public void equals_NonEffectiveObject() {
		// Not really needed in a pure black-box test.
		assertFalse(cell_100.equals(null));
	}
	
	@Test public void equals_NonMemoryCell() {
		// Not really needed in a pure black-box test.
		assertFalse(cell_100.equals(nonCell));
	}
	
	@Test public void clone_SingelCase() {
		// Test not realy needed in pure black-box test.
		cell_100.setValue(222);
		MemoryCell clone = (MemoryCell) cell_100.clone();
		assertNotNull(clone);
		assertEquals(cell_100.getValue(),clone.getValue());
		assertNotSame(clone,cell_100);
		assertEquals(cell_100.getAddress(),clone.getAddress());
	}
	@Test public void isMutable_SingleCase() {
		assertTrue(someCell.isMutable());
	}
	
	@Test public void isValidAddress_LegalAddress() {
		assertTrue(MemoryCell.isValidAddress(0));
	}
	
	@Test public void isValidAddress_IllegalAddress() {
		assertFalse(MemoryCell.isValidAddress(-1));
	}

	@Test public void setValue_SingleCase() {
		someCell.setValue(222);
		assertEquals(222L,someCell.getValue());
	}
	
	@Test public void toString_SingleCase() {
			assertEquals("M"+cell_100.getAddress(),cell_100.toString());
			// Checking correctness of the specification.
			assertEquals("M100",cell_100.toString());
	}

}
