package rational;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class RationalTest {

	// In the current version, a whole bunch of rationale numbers are defined.
	// This stems from the fact that in a previous version, the tests for all
	// the methods applicable to rationale numbers were worked out in separate
	// classes.
	private static Rational rational1_8, rational1_8Clone, rational3_8,
			rational1_9, rational4_17, rational12_51, rational4_6, rational6_9,
			rational6_8, rational4_15, rational_22_100, rational0_10,
			rational_22_10, rationalMAX2_12, rationalMAX16_MAX4,
			rationalMAX_MAX1, rational3_12, rational1_6, rational_11_20,
			rational_9_20, rational_0, rationalMAX_5_3, rational5_MAX_7,
			rational3_MAX_5, rationalMAX_7_5, rationalMAX_7_MAX_5, rational1_1,
			rational1_MAX_5, rational1_MAX_7, rational_85_150, rational12_39,
			rational7_29, rational0_25, rational5_13, rational12_36,
			rational_3_7;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rational1_8 = new Rational(1, 8);
		rational1_8Clone = new Rational(1, 8);
		rational3_8 = new Rational(3, 8);
		rational1_9 = new Rational(1, 9);
		rational4_17 = new Rational(4, 17);
		rational12_51 = new Rational(12, 51);
		rational4_6 = new Rational(4, 6);
		rational6_9 = new Rational(6, 9);
		rational6_8 = new Rational(6, 8);
		rational4_15 = new Rational(4, 15);
		rational_22_100 = new Rational(-22, 100);
		rational0_10 = new Rational(0, 10);
		rational_22_10 = new Rational(-22, 10);
		rationalMAX2_12 = new Rational(Long.MAX_VALUE / 2, 12);
		rationalMAX16_MAX4 = new Rational(Long.MAX_VALUE / 16,
				(Long.MAX_VALUE / 16) * 4);
		rationalMAX_MAX1 = new Rational(Long.MAX_VALUE, (Long.MAX_VALUE - 1));
		rational3_12 = new Rational(3, 12);
		rational1_6 = new Rational(1, 6);
		rational_11_20 = new Rational(-11, 20);
		rational_9_20 = new Rational(-9, 20);
		rational_0 = new Rational(0, 1);
		rationalMAX_5_3 = new Rational(Long.MAX_VALUE - 5, 3);
		rational5_MAX_7 = new Rational(5, Long.MAX_VALUE - 7);
		rational3_MAX_5 = new Rational(3, Long.MAX_VALUE - 5);
		rationalMAX_7_5 = new Rational(Long.MAX_VALUE - 7, 5);
		rationalMAX_7_MAX_5 = new Rational((Long.MAX_VALUE - 7),
				(Long.MAX_VALUE - 5));
		rational1_1 = new Rational(1, 1);
		rational1_MAX_5 = new Rational(1, Long.MAX_VALUE - 5);
		rational1_MAX_7 = new Rational(1, Long.MAX_VALUE - 7);
		rational_85_150 = new Rational(15 * 7, 15 * 10);
		rational12_39 = new Rational(12, 39);
		rational7_29 = new Rational(7, 29);
		rational0_25 = new Rational(0, 25);
		rational5_13 = new Rational(5, 13);
		rational12_36 = new Rational(12, 36);
		rational_3_7 = new Rational(-3, 7);
	}

	// If this test ends with an exception, the JUnit framework will
	// signal it as a failure.
	@Test
	public final void extendedConstructorLegalCase() throws Exception {
		Rational rat = new Rational(7, 38);
		// In using the method assertEquals, care must be taken that
		// both objects have exactly the same type. In this case, we must
		// see to it that both objects are values of type long.
		assertEquals(7L, rat.getNumerator());
		assertEquals(38L, rat.getDenominator());
	}

	// As part of the @Test-annotation, JUnit 4 supports the specification of
	// exceptions
	// that can be thrown in performing a test.
	// If this test ends with an IllegalDenominatorException, the test succeeds.
	// If it
	// ends with another exception, the test fails.
	@Test(expected = IllegalDenominatorException.class)
	public final void extendedConstructorIllegalCase() throws Exception {
		new Rational(100, -333);
		// An explicit failure is needed, if the above constructor invocation
		// does not
		// terminate with an exception.
		fail("Exception expected!");
	}

	@Test
	public final void testtestMiddleConstructorLegalCase() throws Exception {
		Rational rat = new Rational(1000);
		assertEquals(1L, rat.getNumerator());
		assertEquals(1000L, rat.getDenominator());
	}

	@Test(expected = IllegalDenominatorException.class)
	public final void middleConstructorIllegalCase() throws Exception {
		new Rational(-1);
		fail("Exception expected!");
	}

	@Test
	public final void defaultConstructorSingleCase() {
		Rational rat = new Rational();
		assertEquals(0L, rat.getNumerator());
		assertTrue(rat.getDenominator() > 0);
	}

	@Test
	public final void isValidDenominatorTrueCase() {
		assertTrue(Rational.isValidDenominator(1));
	}

	@Test
	public final void isValidDenominatorFalseCase() {
		assertFalse(Rational.isValidDenominator(0));
	}

	@Test
	public final void isIdenticalToTrueCase() throws Exception {
		assertTrue(rational1_8.isIdenticalTo(rational1_8Clone));
	}

	@Test
	public final void isIdenticalToDifferentNumerators() throws Exception {
		assertFalse(rational1_8.isIdenticalTo(rational3_8));
	}

	@Test
	public final void isIdenticalToDifferentDenominators() throws Exception {
		assertFalse(rational1_8.isIdenticalTo(rational1_9));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void isIdenticalToNonEffectiveNumber() throws Exception {
		rational1_8.isIdenticalTo(null);
		fail("Exception expected!");
	}

	@Test
	public final void hasSameValueAsTrueCase() {
		assertTrue(rational4_17.hasSameValueAs(rational12_51));
	}

	@Test
	public final void hasSameValueAsOtherTrueCase() {
		// This test is not really needed in a pure black box test. It was
		// worked out initially, and not removed from the test suite.
		assertTrue(rational4_6.hasSameValueAs(rational6_9));
	}

	@Test
	public final void hasSameValueAsFalseCase() {
		assertFalse(rational4_6.hasSameValueAs(rational6_8));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void hasSameValueAsNonEffectiveNumber() throws Exception {
		rational6_8.hasSameValueAs(null);
		fail("Exception Expected!");
	}

	@Test
	public final void timesProductInRange() throws Exception {
		Rational result = rational4_15.times(5);
		assertTrue(result.hasSameValueAs(new Rational(4, 3)));
	}

	@Test
	public final void timesProductInRangeNegativeRational() throws Exception {
		// This case is not needed in a pure black-box test.
		Rational result = rational_22_100.times(5);
		assertTrue(result.hasSameValueAs(new Rational(-22, 20)));
	}

	@Test
	public final void timesZeroRational() throws Exception {
		Rational result = rational0_10.times(5);
		assertTrue(result.hasSameValueAs(new Rational(0, 1)));
	}

	@Test
	public final void timesZeroFactor() throws Exception {
		// This case is not needed in a pure black-box test.
		Rational result = rational_22_10.times(0);
		assertTrue(result.hasSameValueAs(new Rational(0, 1)));
	}

	@Test
	public final void timesSimpleProductOutOfRange() throws Exception {
		// This case is not needed in a pure black-box test.
		Rational result = rationalMAX2_12.times(6);
		assertTrue(result.hasSameValueAs(new Rational(Long.MAX_VALUE / 2, 2)));
	}

	@Test
	public final void timesMiddleProductOutOfRange() throws Exception {
		// This case is not needed in a pure black-box test.
		Rational result = rationalMAX16_MAX4.times(23);
		assertTrue(result.hasSameValueAs(new Rational(23, 4)));
	}

	@Test(expected = TimesOverflowException.class)
	public final void timesProductOutOfRange() throws Exception {
		rationalMAX_MAX1.times(73);
		fail("Exception Expected!");
	}

	@Test
	public final void addPositiveNumbers() throws Exception {
		Rational result = rational3_12.add(rational1_6);
		assertEquals(30L, result.getNumerator());
		assertEquals(72L, result.getDenominator());
	}

	@Test
	public final void addNegativeNumbers() throws Exception {
		// This case is not needed in a pure black-box test.
		Rational result = rational_11_20.add(rational_9_20);
		assertEquals(-400L, result.getNumerator());
		assertEquals(400L, result.getDenominator());
	}

	@Test
	public final void addZeroRightOperand() throws Exception {
		// This case is not needed in a pure black-box test.
		Rational result = rational_11_20.add(rational_0);
		assertEquals(-11L, result.getNumerator());
		assertEquals(20L, result.getDenominator());
	}

	@Test
	public final void addZeroLeftOperand() throws Exception {
		// This case is not needed in a pure black-box test.
		Rational result = rational_0.add(rational_0);
		assertEquals(0L, result.getNumerator());
		assertEquals(1L, result.getDenominator());
	}

	@Test(expected = OverflowException.class)
	public final void addCrossProductOutOfRange() throws Exception {
		rationalMAX_5_3.add(rational5_MAX_7);
		fail("Exception Expected!");
	}

	@Test(expected = OverflowException.class)
	public final void addOtherCrossProductOutOfRange() throws Exception {
		rational3_MAX_5.add(rationalMAX_7_5);
		fail("Exception Expected!");
	}

	@Test(expected = OverflowException.class)
	public final void addSumCrossProductsOutOfRange() throws Exception {
		rationalMAX_7_MAX_5.add(rational1_1);
		fail("Exception Expected!");
	}

	@Test(expected = OverflowException.class)
	public final void addDenominatorProductOutOfRange() throws Exception {
		rational1_MAX_5.add(rational1_MAX_7);
		fail("Exception Expected!");
	}

	@Test
	public final void getCommonFactorSingleCase() {
		assertEquals(15L, rational_85_150.getCommonFactor());
	}

	@Test
	public final void normalizeNonNormalizedNumber() {
		Rational other = rational12_39.normalize();
		assertNotNull(other);
		assertEquals(4L, other.getNumerator());
		assertEquals(13L, other.getDenominator());
	}

	@Test
	public final void normalizeNormalizedNumber() {
		// This case is not needed in a pure black-box test.
		Rational other = rational7_29.normalize();
		assertNotNull(other);
		assertEquals(7L, other.getNumerator());
		assertEquals(29L, other.getDenominator());
	}

	@Test
	public final void normalizeZeroNumerator() {
		// This case is not needed in a pure black-box test.
		Rational other = rational0_25.normalize();
		assertNotNull(other);
		assertEquals(0L, other.getNumerator());
		assertEquals(1L, other.getDenominator());
	}

	@Test
	public final void isNormalizedTrueCase() {
		assertTrue(rational5_13.isNormalized());
	}

	@Test
	public final void isNormalizedFalseCase() {
		assertFalse(rational12_36.isNormalized());
	}

	@Test
	public final void toStringSingleCase() {
		assertEquals("[-3/7]", rational_3_7.toString());
	}

	@Test
	public final void testGetCopy() {
		assertTrue(rational_3_7.getCopy() == rational_3_7);
	}

}
