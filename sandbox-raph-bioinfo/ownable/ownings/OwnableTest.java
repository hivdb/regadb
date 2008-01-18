package ownable.ownings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ownable.exceptions.IllegalOwnerException;
import ownable.exceptions.IllegalValueException;
import ownable.persons.Person;

public class OwnableTest {

	private Ownable someOwnable, ownableWithOwner, ownableWithoutOwner,
			terminatedOwnable;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
	}

	@Before public void setUp() throws Exception {
		someOwnable = new Dog("Bobby");
		ownableWithOwner = new Painting(new ownable.persons.Person(), BigInteger.valueOf(100),
			"Title", new Person());
		ownableWithoutOwner = new Painting("Waterfront");
		terminatedOwnable = new Dog("Bessy");
		terminatedOwnable.terminate();
	}

	@Test public void canHaveAsValue_NonTerminatedOwnableLegalValue() {
		assertTrue(someOwnable.canHaveAsValue(BigInteger.ZERO));
	}

	@Test public void canHaveAsValue_NonTerminatedOwnableIllegalValue() {
		assertFalse(someOwnable.canHaveAsValue(BigInteger.valueOf(-1)));
	}

	@Test public void canHaveAsValue_NonTerminatedOwnableNonEffectiveValue() {
		assertFalse(someOwnable.canHaveAsValue(null));
	}

	@Test public void canHaveAsValue_TerminatedOwnableNonEffectiveValue() {
		assertTrue(terminatedOwnable.canHaveAsValue(null));
	}

	@Test public void canHaveAsValue_TerminatedOwnableEffectiveValue() {
		// Not really needed in a black-box test.
		assertTrue(terminatedOwnable.canHaveAsValue(BigInteger.ZERO));
	}

	@Test public void setValue_LegalEffectiveValue() throws Exception {
		BigInteger theValue = BigInteger.valueOf(100);
		someOwnable.setValue(theValue);
		assertSame(theValue, someOwnable.getValue());
	}

	@Test public void setValue_LegalNonEffectiveValue() throws Exception {
		someOwnable.setValue(null);
		assertNull(someOwnable.getValue());
	}

	@Test(expected = IllegalValueException.class) public void setValue_IllegalValue()
			throws Exception {
		someOwnable.setValue(BigInteger.valueOf(-100));
	}

	@Test(expected = IllegalStateException.class) public void set_ValueTerminatedOwnable()
			throws Exception {
		terminatedOwnable.setValue(BigInteger.valueOf(-100));
	}

	@Test public void canHaveAsOwner_NonEffectiveOwner() {
		assertTrue(someOwnable.canHaveAsOwner(null));
	}

	@Test public void canHaveAsOwner_TerminatedOwner() {
		Person terminatedPerson = new Person();
		terminatedPerson.terminate();
		assertFalse(someOwnable.canHaveAsOwner(terminatedPerson));
	}

	@Test public void canHaveAsOwner_TerminatedOwnable() {
		assertFalse(terminatedOwnable.canHaveAsOwner(new Person()));
	}

	@Test public void canHaveAsOwner_NotOwner() {
		Person otherPerson = new Person();
		assertTrue(ownableWithOwner.canHaveAsOwner(otherPerson));
	}

	@Test public void canHaveAsOwner_Owner() {
		assertTrue(ownableWithOwner.canHaveAsOwner(ownableWithOwner.getOwner()));
	}

	@Test public void hasOwner_TrueCase() {
		assertTrue(ownableWithOwner.hasOwner());
	}

	@Test public void hasOwner_FalseCase() {
		assertFalse(ownableWithoutOwner.hasOwner());
	}

	@Test public void changeOwner_LegalOwnerForOwnableWithoutOwner()
			throws Exception {
		Person newOwner = new Person();
		ownableWithoutOwner.changeOwnerTo(newOwner);
		assertSame(newOwner, ownableWithoutOwner.getOwner());
		assertTrue(newOwner.hasAsOwning(ownableWithoutOwner));
	}

	@Test public void changeOwner_LegalOwnerForOwnableWithOwner()
			throws Exception {
		Person formerOwner = ownableWithOwner.getOwner();
		Person newOwner = new Person();
		ownableWithOwner.changeOwnerTo(newOwner);
		assertSame(newOwner, ownableWithOwner.getOwner());
		assertTrue(newOwner.hasAsOwning(ownableWithOwner));
		assertFalse(formerOwner.hasAsOwning(ownableWithOwner));
	}

	@Test public void changeOwner_LegalOwnerForOwnableWithSameOwner()
			throws Exception {
		Person formerOwner = ownableWithOwner.getOwner();
		ownableWithOwner.changeOwnerTo(formerOwner);
		assertSame(formerOwner, ownableWithOwner.getOwner());
		assertTrue(formerOwner.hasAsOwning(ownableWithOwner));
	}

	@Test(expected = IllegalOwnerException.class) public void changeOwner_IllegalOwner()
			throws Exception {
		Person terminatedPerson = new Person();
		terminatedPerson.terminate();
		ownableWithoutOwner.changeOwnerTo(terminatedPerson);
	}

	@Test(expected = IllegalArgumentException.class) public void changeOwner_NonEffectiveOwner()
			throws Exception {
		ownableWithoutOwner.changeOwnerTo(null);
	}

	@Test public void removeOwner_OwnableWithoutOwner() {
		ownableWithoutOwner.removeOwner();
		assertFalse(ownableWithoutOwner.hasOwner());
	}

	@Test public void removeOwner_OwnableWithOwner() {
		Person formerOwner = ownableWithOwner.getOwner();
		ownableWithOwner.removeOwner();
		assertFalse(ownableWithOwner.hasOwner());
		assertFalse(formerOwner.hasAsOwning(ownableWithOwner));
	}
	
	@Test public void setOwner_SingleCase() {
		Person newOwner = new Person();
		someOwnable.setOwner(newOwner);
		assertEquals(newOwner,someOwnable.getOwner());
	}

	@Test public void terminate_OwnableNotOwned() {
		ownableWithoutOwner.terminate();
		assertTrue(ownableWithoutOwner.isTerminated());
	}

	@Test public void terminate_OwnableOwned() {
		Person formerOwner = ownableWithOwner.getOwner();
		ownableWithOwner.terminate();
		assertTrue(ownableWithOwner.isTerminated());
		assertFalse(formerOwner.hasAsOwning(ownableWithOwner));
	}

	@Test public void terminate_AlreadyTermiantedOwnable() {
		terminatedOwnable.terminate();
		assertTrue(terminatedOwnable.isTerminated());
	}

	@Test public void setIsTerminated_SingleCase() {
		someOwnable.setIsTerminated();
		assertTrue(someOwnable.isTerminated());
		assertTrue(someOwnable.canHaveAsValue(someOwnable.getValue()));
	}

}
