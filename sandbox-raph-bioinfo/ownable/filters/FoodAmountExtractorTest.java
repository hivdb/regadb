package ownable.filters;


import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.BeforeClass;
import org.junit.Test;

import ownable.ownings.Car;
import ownable.ownings.Dog;
import ownable.ownings.Ownable;
import ownable.persons.Person;


public class FoodAmountExtractorTest {

	private static FoodAmountExtractor theExtractor;

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		theExtractor = new FoodAmountExtractor();
	}

	@Test public void getValueFor_Dog() throws Exception {
		Dog theDog = new Dog(new Person(), BigInteger.ONE, "Bobby", 300);
		assertEquals(BigInteger.valueOf(300), theExtractor.getValueFor(theDog));
	}

	@Test public void getValueFor_NonDog() throws Exception {
		Ownable nonDog = new Car();
		assertEquals(BigInteger.ZERO, theExtractor.getValueFor(nonDog));
	}

	@Test public void getValueFor_NonEffectiveObject() {
		assertEquals(BigInteger.ZERO, theExtractor.getValueFor(null));
	}

}
