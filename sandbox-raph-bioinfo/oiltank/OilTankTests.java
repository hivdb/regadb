package oiltank;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class OilTankTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private OilTank tankCap1000Cont200, tankCont500, fullTank, emptyTank;

	@Before
	public void setUp() throws Exception {
		tankCap1000Cont200 = new OilTank(1000, 200);
		tankCont500 = new OilTank(3000, 500);
		fullTank = new OilTank(2000, 2000);
		emptyTank = new OilTank(2000, 0);
	}

	@Test
	public final void testExtendedConstructor() {
		OilTank newTank = new OilTank(1000, 300);
		assertEquals(1000, newTank.getCapacity());
		assertEquals(300, newTank.getContents());
	}

	@Test
	public final void testMiddleConstructor() {
		OilTank newTank = new OilTank(1000);
		assertEquals(1000, newTank.getCapacity());
		assertEquals(0, newTank.getContents());
	}

	@Test
	public final void testDefaultConstructor() {
		OilTank newTank = new OilTank();
		assertEquals(5000, newTank.getCapacity());
		assertEquals(0, newTank.getContents());
	}

	@Test
	public final void testIsValidCapacity() {
		// Valid capacity
		assertTrue(OilTank.isValidCapacity(1));
		// Invalid capacity
		assertFalse(OilTank.isValidCapacity(0));
	}

	@Test
	public final void testIsValidContents() {
		// Valid contents
		assertTrue(OilTank.isValidContents(400, 800));
		// Negative contents
		assertFalse(OilTank.isValidContents(-1, 1000));
		// Contents exceeding capacity
		assertFalse(OilTank.isValidContents(5, -10));
	}

	@Test
	public final void testGetFree() {
		assertEquals(800, tankCap1000Cont200.getFree());
	}

	@Test
	public final void testIsFull() {
		// Full tank.
		assertTrue(fullTank.isFull());
		// Non full tank
		assertFalse(tankCap1000Cont200.isFull());
	}

	@Test
	public final void testIsEmpty() {
		// Empty tank
		assertTrue(emptyTank.isEmpty());
		// Non empty tank
		assertFalse(tankCap1000Cont200.isEmpty());
	}

	@Test
	public final void testFillSingleAmount() {
		tankCap1000Cont200.fill(500);
		assertEquals(700, tankCap1000Cont200.getContents());
	}

	@Test
	public final void testFillCompletely() {
		// Non full tank
		tankCap1000Cont200.fill();
		assertTrue(tankCap1000Cont200.isFull());
		// Full tank:
		// This test is not really needed in a pure black-box test.
		fullTank.fill();
		assertTrue(fullTank.isFull());
	}

	@Test
	public final void testFillSeveralAmounts() {
		// Several amounts
		tankCap1000Cont200.fill(150, 40, 60);
		assertEquals(450, tankCap1000Cont200.getContents());
	}

	@Test
	public final void testExtractSingleAmount() {
		tankCap1000Cont200.extract(150);
		assertEquals(50, tankCap1000Cont200.getContents());
	}

	@Test
	public final void testExtractCompletely() {
		tankCap1000Cont200.extract();
		assertTrue(tankCap1000Cont200.isEmpty());
	}

	@Test
	public final void testExtractSeveralAmounts() {
		// Several amounts
		tankCap1000Cont200.extract(50, 70, 40);
		assertEquals(40, tankCap1000Cont200.getContents());
	}

	@Test
	public final void testTransferFrom() {
		tankCap1000Cont200.transferFrom(tankCont500);
		assertEquals(700, tankCap1000Cont200.getContents());
		assertTrue(tankCont500.isEmpty());
	}

	@Test
	public final void testGetPercentageFilled() {
		assertEquals(20.0F, tankCap1000Cont200.getPercentageFilled());
	}

	@Test
	public final void testIsRelativelyFullerThan() {
		// True case
		OilTank otherTank = new OilTank(500, 70);
		assertTrue(tankCap1000Cont200.isRelativelyFullerThan(otherTank));
		// False case
		otherTank = new OilTank(500, 127);
		assertFalse(tankCap1000Cont200.isRelativelyFullerThan(otherTank));
		// Equal case:
		// This test is not really needed in a pure black-box test.
		otherTank = new OilTank(500, 100);
		assertFalse(tankCap1000Cont200.isRelativelyFullerThan(otherTank));
	}

	@Test
	public final void testToString() {
		assertEquals("Oil tank with capacity 1000 and contents 200",
				tankCap1000Cont200.toString());
	}

	@Test
	public final void testIsSameAs() {
		// True case
		OilTank otherTank = new OilTank(1000, 200);
		assertTrue(tankCap1000Cont200.isSameAs(otherTank));
		// Different capacity
		otherTank = new OilTank(500, 200);
		assertFalse(tankCap1000Cont200.isSameAs(otherTank));
		// Different contents
		otherTank = new OilTank(1000, 250);
		assertFalse(tankCap1000Cont200.isSameAs(otherTank));
	}

	@Test
	public final void testGetCopy() {
		OilTank copy = tankCap1000Cont200.getCopy();
		assertNotNull(copy);
		assertNotSame(tankCap1000Cont200, copy);
		assertTrue(tankCap1000Cont200.isSameAs(copy));
	}

}
