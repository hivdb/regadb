package ownable.persons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ownable.exceptions.IllegalOwnerException;
import ownable.filters.BigIntegerExtractor;
import ownable.filters.FoodAmountExtractor;
import ownable.ownings.Car;
import ownable.ownings.Dog;
import ownable.ownings.Ownable;
import ownable.ownings.Painting;

public class PersonTest {

	private Person somePerson, personWithoutOwnings, personWithOwnings,
			terminatedPerson;

	private Ownable someOwnings[];

	private Ownable terminatedOwnable;
	private static BigIntegerExtractor theExtractor;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		theExtractor = new FoodAmountExtractor();
	}

	@Before public void setUp() throws Exception {
		somePerson = new Person();
		personWithoutOwnings = new Person();
		personWithOwnings = new Person();
		terminatedPerson = new Person();
		terminatedPerson.terminate();
		someOwnings = new Ownable[3];
		someOwnings[0] = new Dog(personWithOwnings, BigInteger.ZERO, "Bobby",
			1000);
		someOwnings[1] = new Painting(personWithOwnings, BigInteger.ZERO,
			"Waterfront", new Person());
		someOwnings[2] = new Car(personWithOwnings, BigInteger.ONE, 1500);
		terminatedOwnable = new Dog("Bobby");
		terminatedOwnable.terminate();
	}

	@Test public void testSingleCase() {
		Person thePerson = new Person();
		assertEquals(0, thePerson.getNbOwnings());
		assertFalse(thePerson.isTerminated());
	}

	@Test public void terminate_PersonWithoutOwnings() {
		personWithoutOwnings.terminate();
		assertTrue(personWithoutOwnings.isTerminated());
	}

	@Test public void terminate_PersonWithOwnings() {
		personWithOwnings.terminate();
		assertTrue(personWithOwnings.isTerminated());
		for (Ownable owning : someOwnings)
			assertFalse(owning.hasOwner());
	}

	@Test public void terminate_PersonAlreadyTerminated() {
		terminatedPerson.terminate();
		assertTrue(terminatedPerson.isTerminated());
	}

	@Test public void sedtIsTerminated_SingleCase() {
		somePerson.setIsTerminated();
		assertTrue(somePerson.isTerminated());
	}

	@Test public void canHaveAsOwning_AcceptableOwnableOfOwner()
			throws Exception {
		Ownable owningOfSomePerson = new Dog(somePerson, BigInteger.TEN,
			"Bobby", 100);
		assertTrue(somePerson.canHaveAsOwning(owningOfSomePerson));
	}

	@Test public void canHaveAsOwning_AcceptableOwnableOfOtherPerson() {
		assertTrue(somePerson.canHaveAsOwning(someOwnings[0]));
	}

	@Test public void canHaveAsOwning_NonEffectiveOwnable() {
		assertFalse(somePerson.canHaveAsOwning(null));
	}

	@Test public void canHaveAsOwning_NonAcceptableOwnable() {
		someOwnings[0].terminate();
		assertFalse(somePerson.canHaveAsOwning(someOwnings[0]));
	}

	// Case in which bindings are not consistent cannot be tested.

	@Test public void getNbOwnings_SingleCase() {
		assertEquals(someOwnings.length, personWithOwnings.getNbOwnings());
	}

	@Test public void addToOwnings_LegalCase() throws Exception {
		Person formerOwner = someOwnings[0].getOwner();
		somePerson.addToOwnings(someOwnings[0]);
		assertEquals(somePerson, someOwnings[0].getOwner());
		assertTrue(somePerson.hasAsOwning(someOwnings[0]));
		assertFalse(formerOwner.hasAsOwning(someOwnings[0]));
	}

	@Test(expected = IllegalOwnerException.class) public void addToOwnings_NonAcceptableOwnable()
			throws Exception {
		somePerson.addToOwnings(terminatedOwnable);
	}

	@Test(expected = IllegalArgumentException.class) public void addToOwnings_NonEffectiveOwnable()
			throws Exception {
		somePerson.addToOwnings(null);
	}

	@Test public void removeFromOwnings_LegalCase() throws Exception {
		personWithOwnings.removeFromOwnings(someOwnings[0]);
		assertFalse(someOwnings[0].hasOwner());
		assertFalse(personWithOwnings.hasAsOwning(someOwnings[0]));
	}

	@Test(expected = IllegalArgumentException.class) public void removeFromOwnings_NonEffectiveOwnable()
			throws Exception {
		somePerson.removeFromOwnings(null);
	}

	@Test(expected = IllegalOwnerException.class) public void removeFromOwnings_NotOwner()
			throws Exception {
		somePerson.removeFromOwnings(someOwnings[0]);
	}

	@Test public void getTotalValue_LegalCase() throws Exception {
		Ownable ownings[] = new Ownable[3];
		ownings[0] = new Dog(somePerson, BigInteger.valueOf(500), "Bobby", 1000);
		ownings[1] = new Painting(somePerson, BigInteger.valueOf(6000),
			"Waterfront", new Person());
		ownings[2] = new Car(somePerson, BigInteger.valueOf(7500), 1500);
		assertEquals(BigInteger.valueOf(14000), somePerson.getTotalValue());
	}

	@Test(expected = IllegalStateException.class) public void getTotalValue_IllegalCase()
			throws Exception {
		terminatedPerson.getTotalValue();
	}

	@Test public void getTotalFoodAmount_LegalCase() throws Exception {
		Ownable[] ownings = new Ownable[5];
		ownings[0] = new Dog(somePerson, BigInteger.valueOf(500), "Bobby", 1000);
		ownings[1] = new Painting(somePerson, BigInteger.valueOf(6000),
			"Waterfront", new Person());
		ownings[2] = new Dog(somePerson, BigInteger.valueOf(500), "Bessy", 400);
		ownings[3] = new Car(somePerson, BigInteger.valueOf(7500), 1500);
		ownings[4] = new Dog(somePerson, BigInteger.valueOf(500), "Lassy", 100);
		assertEquals(BigInteger.valueOf(4500), somePerson.getTotalFoodAmount(3));
	}

	@Test(expected = IllegalArgumentException.class) public void getTotalFoodAmount_NegativeNumberOfDays()
			throws Exception {
		somePerson.getTotalFoodAmount(-1);
	}

	@Test(expected = IllegalStateException.class) public void getTotalFoodAmount_PersonAlreadyTerminated()
			throws Exception {
		terminatedPerson.getTotalFoodAmount(3);
	}

	@Test public void getMostPowerfulCar_LegalCase() throws Exception {
		Ownable[] ownings = new Ownable[6];
		ownings[0] = new Car(somePerson, BigInteger.valueOf(3500), 1000);
		ownings[1] = new Dog(somePerson, BigInteger.valueOf(500), "Bobby", 1000);
		ownings[2] = new Car(somePerson, BigInteger.valueOf(5500), 1200);
		ownings[3] = new Car(somePerson, BigInteger.valueOf(7500), 800);
		ownings[4] = new Painting(somePerson, BigInteger.valueOf(6000),
			"Waterfront", new Person());
		ownings[5] = new Car(somePerson, BigInteger.valueOf(7500), 1500);
		assertSame(ownings[5], somePerson.getMostPowerfulCar());
	}

	@Test(expected = NoSuchElementException.class) public void getMostPowerfulCar_IllegalCase()
			throws Exception {
		personWithoutOwnings.getMostPowerfulCar();
	}

	@Test(expected = IllegalStateException.class) public void getMostPowerfulCar_PersonAlreadyTerminated()
			throws Exception {
		terminatedPerson.getMostPowerfulCar();
	}

	public void getPaintingBy_LegalCase() throws Exception {
		Person thePainter = new Person();
		Ownable[] ownings = new Ownable[4];
		ownings[0] = new Painting(somePerson, BigInteger.valueOf(6000),
			"Waterfront", new Person());
		ownings[1] = new Dog(somePerson, BigInteger.valueOf(500), "Bobby", 1000);
		ownings[2] = new Painting(somePerson, BigInteger.valueOf(6000),
			"Landscape", new Person());
		ownings[3] = new Painting(somePerson, BigInteger.valueOf(6000),
			"Portrait", thePainter);
		assertSame(ownings[3], somePerson.getPaintingBy(thePainter));
	}

	@Test(expected = NoSuchElementException.class) public void getPaintingBy_PersonWithoutPaintingsByThePainter()
			throws Exception {
		personWithoutOwnings.getPaintingBy(somePerson);
	}

	@Test(expected = IllegalStateException.class) public void getPaintingBy_TerminatedPerson()
			throws Exception {
		terminatedPerson.getPaintingBy(somePerson);
	}
	
	@Test public void getTotalFor_LegalCase() throws Exception {
			Person thePerson = new Person();
			Ownable ownings[] = new Ownable[5];
			ownings[0] = new Dog(thePerson, BigInteger.valueOf(500), "Bobby", 1000);
			ownings[1] = new Painting(thePerson, BigInteger.valueOf(6000),
				"Waterfront", new Person());
			ownings[2] = new Dog(thePerson, BigInteger.valueOf(500), "Bessy", 400);
			ownings[3] = new Car(thePerson, BigInteger.valueOf(7500), 1500);
			ownings[4] = new Dog(thePerson, BigInteger.valueOf(500), "Lassy", 100);
			assertEquals(BigInteger.valueOf(1500), thePerson
				.getTotalFor(theExtractor));
	}

	@Test(expected=IllegalArgumentException.class) public void getTotalFor_NonEffectiveExtractor() throws Exception {
			somePerson.getTotalFor(null);
	}

	@Test(expected=IllegalStateException.class) public void getTotalFor_PersonAlreadyTerminated() throws Exception {
			terminatedPerson.getTotalFor(theExtractor);
	}

}
