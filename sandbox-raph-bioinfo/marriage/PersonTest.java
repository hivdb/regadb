package marriage;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PersonTest {

	private Person unmarriedMale, otherUnmarriedMale, marriedMale,
			otherMarriedMale, unmarriedFemale, marriedFemale, terminatedFemale;

	@Before
	public void setUp() throws Exception {
		unmarriedMale = new Person(Gender.MALE);
		otherUnmarriedMale = new Person(Gender.MALE);
		marriedMale = new Person(Gender.MALE, new Person());
		otherMarriedMale = new Person(Gender.MALE, new Person());
		unmarriedFemale = new Person();
		marriedFemale = new Person(Gender.FEMALE, new Person(Gender.MALE));
		terminatedFemale = new Person();
		terminatedFemale.terminate();
	}

	@Test
	public void isValidGenderMaleCase() {
		assertTrue(Person.isValidGender(Gender.MALE));
	}

	@Test
	public void isValidGenderFemaleCase() {
		assertTrue(Person.isValidGender(Gender.FEMALE));
	}

	@Test
	public void isValidGenderNonEffectiveGender() {
		assertFalse(Person.isValidGender(null));
	}

	@Test
	public void isFemaleTrueCase() {
		assertTrue(unmarriedFemale.isFemale());
	}

	@Test
	public void isFemaleFalseCase() {
		assertFalse(unmarriedMale.isFemale());
	}

	@Test
	public void isMaleTrueCase() {
		assertTrue(unmarriedMale.isMale());
	}

	@Test
	public void isMaleFalseCase() {
		assertFalse(unmarriedFemale.isMale());
	}

	@Test
	public void extendedConstructorNonEffectivePartner() throws Exception {
		Person thePerson = new Person(Gender.MALE, null);
		assertEquals(Gender.MALE, thePerson.getGender());
		assertNull(thePerson.getSpouse());
		assertFalse(thePerson.isTerminated());
	}

	@Test
	public void extendedConstructortLegalEffectivePartner() throws Exception {
		Person thePartner = new Person(Gender.MALE);
		Person thePerson = new Person(Gender.FEMALE, thePartner);
		assertEquals(Gender.FEMALE, thePerson.getGender());
		assertEquals(thePartner, thePerson.getSpouse());
		assertEquals(thePerson, thePartner.getSpouse());
		assertFalse(thePerson.isTerminated());
	}

	@Test(expected = IllegalArgumentException.class)
	public void extendedConstructorIllegalGender() throws Exception {
		new Person(null, null);
		fail("Exception expected!");
	}

	@Test(expected = IllegalPartnerException.class)
	public void extendedConstructorUnacceptablePartner() throws Exception {
		new Person(Gender.MALE, terminatedFemale);
		fail("Exception expected!");
	}

	@Test(expected = IllegalPartnerException.class)
	public void extendedConstructorSameGender() throws Exception {
		new Person(Gender.MALE, unmarriedMale);
		fail("Exception expected!");
	}

	@Test
	public void middleConstructorLegalGender() throws Exception {
		Person thePerson = new Person(Gender.MALE);
		assertEquals(Gender.MALE, thePerson.getGender());
		assertNull(thePerson.getSpouse());
		assertFalse(thePerson.isTerminated());
	}

	@Test(expected = IllegalArgumentException.class)
	public void middleConstructorIllegalGender() throws Exception {
		new Person(null);
		fail("Exception expected!");
	}

	@Test
	public void defaultConstructorSingleCase() {
		Person thePerson = new Person();
		assertEquals(Gender.FEMALE, thePerson.getGender());
		assertNull(thePerson.getSpouse());
		assertFalse(thePerson.isTerminated());
	}

	@Test
	public void isMarriedFalseCase() {
		assertFalse(unmarriedMale.isMarried());
	}

	@Test
	public void isMarriedTrueCase() {
		assertTrue(marriedMale.isMarried());
	}

	@Test
	public void canHaveAsSpouseNonEffectiveSpouse() {
		assertTrue(unmarriedMale.canHaveAsSpouse(null));
	}

	@Test
	public void canHaveAsSpouseTerminatedPersonEffectiveSpouse() {
		assertFalse(terminatedFemale.canHaveAsSpouse(unmarriedMale));
	}

	@Test
	public void canHaveAsSpouseTerminatedPersonNonEffectiveSpouse() {
		// This test is not really needed in a pure black-box test.
		assertTrue(terminatedFemale.canHaveAsSpouse(null));
	}

	@Test
	public void canHaveAsSpouseTerminatedSpouse() {
		assertFalse(unmarriedMale.canHaveAsSpouse(terminatedFemale));
	}

	@Test
	public void canHaveAsSpouseSameGendersNonTerminatedPersons() {
		assertFalse(unmarriedMale.canHaveAsSpouse(otherUnmarriedMale));
	}

	@Test
	public void canHaveAsSpouseDifferentGendersUnmarriedPersons() {
		assertTrue(unmarriedMale.canHaveAsSpouse(unmarriedFemale));
	}

	@Test
	public void canHaveAsSpouseDifferentGendersMarriedPerson() {
		assertFalse(marriedMale.canHaveAsSpouse(unmarriedFemale));
	}

	@Test
	public void canHaveAsSpouseDifferentGendersMarriedOther() {
		assertFalse(unmarriedMale.canHaveAsSpouse(marriedFemale));
	}

	@Test
	public void canHaveAsSpouseOwnSpouse() {
		assertTrue(marriedMale.canHaveAsSpouse(marriedMale.getSpouse()));
	}

	@Test
	public void marryLegalCase() throws Exception {
		unmarriedMale.marry(unmarriedFemale);
		assertEquals(unmarriedFemale, unmarriedMale.getSpouse());
		assertEquals(unmarriedMale, unmarriedFemale.getSpouse());
	}

	@Test(expected = IllegalPartnerException.class)
	public void marryNonEffectiveSpouse() throws Exception {
		unmarriedMale.marry(null);
		fail("Exception Expected!");
	}

	@Test(expected = IllegalPartnerException.class)
	public void marryUnacceptableSpouse() throws Exception {
		unmarriedMale.marry(otherUnmarriedMale);
		fail("Exception Expected!");
	}

	@Test
	public void divorceMarriedPerson() {
		Person spouseOfMarriedMale = marriedMale.getSpouse();
		marriedMale.divorce();
		assertFalse(marriedMale.isMarried());
		assertFalse(spouseOfMarriedMale.isMarried());
	}

	@Test
	public void divorceUnmarriedPerson() {
		unmarriedMale.divorce();
		assertFalse(unmarriedMale.isMarried());
	}

	@Test
	public void terminateMarriedPerson() {
		Person spouseOfMarriedMale = marriedMale.getSpouse();
		marriedMale.terminate();
		assertTrue(marriedMale.isTerminated());
		assertFalse(marriedMale.isMarried());
		assertFalse(spouseOfMarriedMale.isMarried());
	}

	@Test
	public void terminatedUnmarriedPerson() {
		unmarriedMale.terminate();
		assertTrue(unmarriedMale.isTerminated());
		assertFalse(unmarriedMale.isMarried());
	}

	@Test
	public void switchPartnerWithUnmarriedPersonsSameGender() throws Exception {
		unmarriedMale.switchPartnerWith(otherUnmarriedMale);
		// Literal postcondition 1 (other postconditions must be worked out
		// still)
		assertTrue((unmarriedMale.getSpouse() == otherUnmarriedMale)
				|| (unmarriedMale.getSpouse() == otherUnmarriedMale.getSpouse()));
		// Expected effects for this invocation in a more compact way
		assertTrue(!unmarriedMale.isMarried());
		assertTrue(!otherUnmarriedMale.isMarried());
	}

	@Test
	public void switchPartnerWithUnmarriedPersonsDifferentGender()
			throws Exception {
		unmarriedFemale.switchPartnerWith(unmarriedMale);
		// Literal postcondition 1 (other postconditions must be worked out
		// still)
		assertTrue((unmarriedFemale.getSpouse() == unmarriedMale)
				|| (unmarriedFemale.getSpouse() == unmarriedMale.getSpouse()));
		// Expected effects for this invocation in a more compact way
		assertTrue((unmarriedFemale.getSpouse() != unmarriedMale)
				|| (!unmarriedFemale.isMarried()));
		assertTrue((unmarriedMale.getSpouse() != unmarriedFemale)
				|| (!unmarriedMale.isMarried()));
	}

	@Test
	public void swicthPartnerWithMarriedPersonsSameGender() throws Exception {
		Person spouseOfMalePerson = marriedMale.getSpouse();
		Person spouseOfOtherMalePerson = otherMarriedMale.getSpouse();
		marriedMale.switchPartnerWith(otherMarriedMale);
		// Literal postcondition 1 (other postconditions must be worked out
		// still)
		assertTrue((marriedMale.getSpouse() == otherMarriedMale)
				|| (marriedMale.getSpouse() == spouseOfOtherMalePerson));
		// Expected effects for this invocation in a more compact way
		assertTrue(marriedMale.getSpouse() == spouseOfOtherMalePerson);
		assertTrue(spouseOfMalePerson.getSpouse() == otherMarriedMale);
		assertTrue(otherMarriedMale.getSpouse() == spouseOfMalePerson);
		assertTrue(spouseOfOtherMalePerson.getSpouse() == marriedMale);
	}

	@Test
	public void switchPartnerWithMarriedPersonsDifferentGender()
			throws Exception {
		Person spouseOfFemalePerson = marriedFemale.getSpouse();
		Person spouseOfMalePerson = marriedMale.getSpouse();
		marriedFemale.switchPartnerWith(marriedMale);
		// Literal postcondition 1 (other postconditions must be worked out
		// still)
		assertTrue((marriedFemale.getSpouse() == marriedMale)
				|| (marriedFemale.getSpouse() == spouseOfMalePerson));
		// Expected effects for this invocation in a more compact way
		assertTrue(marriedFemale.getSpouse() == marriedMale);
		assertTrue(spouseOfFemalePerson.getSpouse() == spouseOfMalePerson);
		assertTrue(marriedMale.getSpouse() == marriedFemale);
		assertTrue(spouseOfMalePerson.getSpouse() == spouseOfFemalePerson);
	}

	@Test
	public void swtichPartnerWithOnePersonUnmarried() throws Exception {
		Person spouseOfMarriedPerson = marriedFemale.getSpouse();
		unmarriedFemale.switchPartnerWith(marriedFemale);
		// Literal postcondition 1 (other postconditions must be worked out
		// still)
		assertTrue((unmarriedFemale.getSpouse() == marriedFemale)
				|| (unmarriedFemale.getSpouse() == spouseOfMarriedPerson));
		// Expected effects for this invocation in a more compact way
		assertTrue(unmarriedFemale.getSpouse() == spouseOfMarriedPerson);
		assertTrue(!marriedFemale.isMarried());
		assertTrue(spouseOfMarriedPerson.getSpouse() == unmarriedFemale);
	}

	@Test
	public void switchPartnerWithOtherPersonUnmarried() throws Exception {
		Person spouseOfMarriedPerson = marriedFemale.getSpouse();
		marriedFemale.switchPartnerWith(unmarriedMale);
		// Literal postcondition 1 (other postconditions must be worked out
		// still)
		assertTrue((marriedFemale.getSpouse() == unmarriedMale)
				|| (marriedFemale.getSpouse() == unmarriedMale.getSpouse()));
		// Expected effects for this invocation in a more compact way
		assertTrue(marriedFemale.getSpouse() == unmarriedMale);
		assertTrue(!spouseOfMarriedPerson.isMarried());
		assertTrue(unmarriedMale.getSpouse() == marriedFemale);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNonEffectivePerson() throws Exception {
		unmarriedFemale.switchPartnerWith(null);
		fail("Exception Expected!");
	}

}
