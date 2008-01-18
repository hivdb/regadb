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

public class CarTest {

	private static Person someOwner;

	private static BigInteger someValue;

	Car someCar, terminatedCar;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		someOwner = new Person();
		someValue = BigInteger.valueOf(1000);
	}

	@Before public void setUp() throws Exception {
		someCar = new Car(new Person(), BigInteger.valueOf(22222), 2000);
		terminatedCar = new Car(new Person(), BigInteger.valueOf(30303), 1000);
		terminatedCar.terminate();
	}

	@Test public void extendedConstructor_LegalCase() throws Exception {
		Car theCar = new Car(someOwner, someValue, 1500);
		assertEquals(someOwner, theCar.getOwner());
		assertTrue(someOwner.hasAsOwning(theCar));
		assertEquals(someValue, theCar.getValue());
		assertEquals(1500, theCar.getMotorVolume());
	}

	@Test public void extendedConstructor_IllegalMotorVolume() throws Exception {
		Car theCar = new Car(someOwner, someValue, -100);
		assertEquals(someOwner, theCar.getOwner());
		assertTrue(someOwner.hasAsOwning(theCar));
		assertEquals(someValue, theCar.getValue());
		assertEquals(1000, theCar.getMotorVolume());
	}

	@Test(expected = IllegalOwnerException.class) public void extendedConstructor_IllegalOwner()
			throws Exception {
		Person terminatedPerson = new Person();
		terminatedPerson.terminate();
		new Car(terminatedPerson, someValue, 1500);
	}

	@Test(expected = IllegalValueException.class) public void extendedConstructor_IllegalValue()
			throws Exception {
		new Car(someOwner, null, 1500);
	}

	@Test public void defaultConstructor_LegalCase() {
		Car theCar = new Car();
		assertNull(theCar.getOwner());
		assertEquals(BigInteger.ZERO, theCar.getValue());
		assertEquals(1000, theCar.getMotorVolume());
	}

	@Test public void isValidMotorVolume_IllegalVolume() {
		assertFalse(Car.isValidMotorVolume(-1));
	}

	@Test public void setMotorVolume_LegalVolume() {
		someCar.setMotorVolume(1000);
		assertEquals(1000, someCar.getMotorVolume());
	}

	@Test public void setMotorVolume_IllegalVolume() {
		if (!Car.isValidMotorVolume(-1000)) {
			someCar.setMotorVolume(-100);
			assertEquals(2000, someCar.getMotorVolume());
		}
	}

	@Test public void setMotorVolume_TerminatedCar() {
		int oldVolume = terminatedCar.getMotorVolume();
		terminatedCar.setMotorVolume(1000);
		assertEquals(oldVolume, terminatedCar.getMotorVolume());
	}

}
