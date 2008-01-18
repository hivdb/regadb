package ownable.ownings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ownable.exceptions.IllegalFoodAmountException;
import ownable.exceptions.IllegalNameException;
import ownable.exceptions.IllegalOwnerException;
import ownable.exceptions.IllegalValueException;
import ownable.persons.Person;

public class DogTest {

	private static Person somePerson;

	private static BigInteger someValue;

	private Dog someDog, terminatedDog;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		somePerson = new Person();
		someValue = BigInteger.valueOf(1000);
	}

	@Before public void setUp() throws Exception {
		someDog = new Dog("Bobby");
		terminatedDog = new Dog("Bessy");
		terminatedDog.terminate();
	}

	@Test public void extendedConstructor_LegalCase() throws Exception {
		Dog theDog = new Dog(somePerson, someValue, "Bobby", 100);
		assertEquals(somePerson, theDog.getOwner());
		assertTrue(somePerson.hasAsOwning(theDog));
		assertEquals(someValue, theDog.getValue());
		assertEquals("Bobby", theDog.getName());
		assertEquals(100, theDog.getDailyFoodAmount());
	}

	@Test(expected = IllegalOwnerException.class) public void extendedConstructor_IllegalOwner()
			throws Exception {
		Person terminatedPerson = new Person();
		terminatedPerson.terminate();
		new Dog(terminatedPerson, someValue, "Bobby", 100);
	}

	@Test(expected = IllegalValueException.class) public void extendedConstructor_IllegalValue()
			throws Exception {
		new Dog(somePerson, null, "Bobby", 100);
	}

	@Test(expected = IllegalNameException.class) public void extendedConstructor_IllegalName()
			throws Exception {
		new Dog(somePerson, someValue, null, 100);
	}

	@Test(expected = IllegalFoodAmountException.class) public void extendedConstructor_IllegalDailyFoodAmount()
			throws Exception {
		new Dog(somePerson, someValue, "Bobby", -100);
	}

	@Test public void simpleConstructor_LegalCase() throws Exception {
		Dog theDog = new Dog("Bobby");
		assertNull(theDog.getOwner());
		assertEquals(BigInteger.ZERO, theDog.getValue());
		assertEquals("Bobby", theDog.getName());
		assertEquals(500, theDog.getDailyFoodAmount());
	}

	@Test(expected = IllegalNameException.class) public void simpleConstructor_IllegalName()
			throws Exception {
		new Dog(null);
	}

	@Test public void testLegalName() {
		assertTrue(Dog.isValidName("Bobby"));
	}

	@Test public void testNonEffectiveName() {
		assertFalse(Dog.isValidName(null));
	}

	@Test public void testEmptyName() {
		assertFalse(Dog.isValidName(""));
	}

	@Test public void setName_LegalCase() throws Exception {
		someDog.setName("Lassy");
		assertEquals("Lassy", someDog.getName());
	}

	@Test(expected = IllegalNameException.class) public void setName_InvalidName()
			throws Exception {
		someDog.setName("");
	}

	@Test(expected = IllegalStateException.class) public void setName_TerminatedDog()
			throws Exception {
		terminatedDog.setName("Lassy");
	}

	@Test public void isValidDailyFoodAmount_TrueCase() {
		assertTrue(Dog.isValidDailyFoodAmount(1));
	}

	@Test public void isValidDailyFoodAmount_FalseCase() {
		assertFalse(Dog.isValidDailyFoodAmount(0));
	}

	@Test public void setDailyFoodAmount_LegalCase() throws Exception {
		someDog.setDailyFoodAmount(100);
		assertEquals(100, someDog.getDailyFoodAmount());
	}

	@Test(expected = IllegalFoodAmountException.class) public void setDailyFoodAmount_IllegalCase()
			throws Exception {
		someDog.setDailyFoodAmount(0);
	}

}
