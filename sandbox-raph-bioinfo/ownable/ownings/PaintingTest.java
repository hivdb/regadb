package ownable.ownings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ownable.exceptions.IllegalOwnerException;
import ownable.exceptions.IllegalValueException;
import ownable.persons.Person;

public class PaintingTest {

	private static Person someOwner, somePainter;

	private static BigInteger someValue;
	Painting somePainting;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		someOwner = new Person();
		somePainter = new Person();
		someValue = BigInteger.valueOf(1000);
	}

	@Before public void setUp() throws Exception {
		somePainting = new Painting("Portrait");
	}

	@Test public void extendedConstructor_LegalCase() throws Exception {
		Painting thePainting = new Painting(someOwner, someValue, "Landscape",
			somePainter);
		assertEquals(someOwner, thePainting.getOwner());
		assertTrue(someOwner.hasAsOwning(thePainting));
		assertEquals(someValue, thePainting.getValue());
		assertEquals("Landscape", thePainting.getTitle());
		assertEquals(somePainter, thePainting.getPainter());
	}

	@Test(expected = IllegalOwnerException.class) public void extendedConstructor_IllegalOwner()
			throws Exception {
		Person terminatedPerson = new Person();
		terminatedPerson.terminate();
		new Painting(terminatedPerson, someValue, "Landscape", somePainter);
	}

	@Test(expected = IllegalValueException.class) public void extendedConstructor_IllegalValue()
			throws Exception {
		new Painting(someOwner, null, "Landscape", somePainter);
	}
	
	@Test public void testLegalCase() throws Exception {
		Painting thePainting = new Painting("Landscape");
		assertNull(thePainting.getOwner());
		assertEquals(BigInteger.ZERO, thePainting.getValue());
		assertEquals("Landscape", thePainting.getTitle());
		assertNull(thePainting.getPainter());
	}
	
	@Test public void isValidTitle_LegalTitle() {
		assertTrue(Painting.isValidTitle("Waterfront"));
	}

	@Test public void isValidTitle_NonEffectiveTitle() {
		assertFalse(Painting.isValidTitle(null));
	}

	@Test public void isValidTitle_EmptyTitle() {
		assertFalse(Painting.isValidTitle(""));
	}
	
	@Test public void setTitle_SingleCase() {
		somePainting.setTitle("Landscape");
		assertEquals("Landscape",somePainting.getTitle());
	}
	
	@Test public void setPainter_SingleCase() {
		Person newPainter = new Person();
		somePainting.setPainter(newPainter);
		assertEquals(newPainter,somePainting.getPainter());
	}

}
