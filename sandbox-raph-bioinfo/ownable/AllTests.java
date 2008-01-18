package ownable;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ownable.filters.FoodAmountExtractorTest;
import ownable.ownings.CarTest;
import ownable.ownings.DogTest;
import ownable.ownings.OwnableTest;
import ownable.ownings.PaintingTest;
import ownable.persons.PersonTest;






@RunWith(Suite.class)
@Suite.SuiteClasses( { OwnableTest.class, DogTest.class,
	PaintingTest.class, CarTest.class, PersonTest.class,
	FoodAmountExtractorTest.class })
public class AllTests {
}