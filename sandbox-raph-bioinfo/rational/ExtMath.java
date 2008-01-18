package rational;

/**
 * A collection of mathematical functions not offered by the standard class Math
 * in the Java API.
 * 
 * @version 2.0
 * @author Eric Steegmans
 */
public class ExtMath {

	/**
	 * Check whether the given integer numbers can be added.
	 * 
	 * @param a
	 *            The first integer number to be checked.
	 * @param b
	 *            The other integer number to be checked.
	 * @return If the given integer numbers are both positive, true if a does
	 *         not exceed the largest possible value in the type long diminished
	 *         with b; false otherwise. | if ( (a > 0) && (b > 0) ) | then
	 *         result == (a <= Long.MAX_VALUE - b)
	 * @return If the given integer numbers are both negative, true if a is not
	 *         below the smallest possible value in the type long incremented
	 *         with b; false otherwise. | if ( (a < 0) && (b < 0) ) | then
	 *         result == (a >= Long.MIN_VALUE - b)
	 * @return True if the given integer numbers have different signs. | if ( (
	 *         (a <= 0) && (b >= 0) ) || | ( (a >= 0) && (b <=0) ) ) | then
	 *         result == true
	 */
	public static boolean areAddable(long a, long b) {
		if ((a > 0) && (b > 0))
			return a <= Long.MAX_VALUE - b;
		if ((a < 0) && (b < 0))
			return a >= Long.MIN_VALUE - b;
		return true;
	}

	/**
	 * Return the sum of the given integer numbers.
	 * 
	 * @param a
	 *            The left operand for the addition.
	 * @param b
	 *            The right operand for the addition.
	 * @return The sum of the given numbers. | result == a + b
	 * @throws AddOverflowException
	 *             [must] The given integer numbers cannot be added. | !
	 *             areAddable(a,b)
	 */
	public static long add(long a, long b) throws AddOverflowException {
		if (!areAddable(a, b))
			throw new AddOverflowException(a, b);
		return a + b;
	}

	/**
	 * Check whether the given integer numbers can be multiplied.
	 * 
	 * @param a
	 *            The first integer number to be checked.
	 * @param b
	 *            The other integer number to be checked.
	 * @return If the given integer numbers have the same sign, true if the
	 *         absolute value of a does not exceed the largest possible value in
	 *         the type long divided by the absoulte value of b; false
	 *         otherwise. | if ( ( (a > 0) && (b > 0) ) || | ( (a < 0) && (b <
	 *         0) ) ) | then | result == | (Math.abs(a) <= Long.MAX_VALUE /
	 *         Math.abs(b))
	 * @return If a is positive and b is negative, true if b is not below the
	 *         smallest possible value in the type long divided by a; false
	 *         otherwise. | if ( (a > 0) && (b < 0) ) | then result == (b >=
	 *         Long.MIN_VALUE / a)
	 * @return If b is positive and a is negative, true if a is not below the
	 *         smallest possible value in the type long divided by b; false
	 *         otherwise. | if ( (a < 0) && (b > 0) ) | then result == (a >=
	 *         Long.MIN_VALUE / b)
	 * @return True if a is zero or if b is zero. | if ( (a == 0) || (b == 0) ) |
	 *         then result == true
	 */
	public static boolean areMultipliable(long a, long b) {
		if (((a > 0) && (b > 0)) || ((a < 0) && (b < 0)))
			return (Math.abs(a) <= Long.MAX_VALUE / Math.abs(b));
		if ((a < 0) && (b > 0))
			return (a >= Long.MIN_VALUE / b);
		if ((a > 0) && (b < 0))
			return (b >= Long.MIN_VALUE / a);
		return true; // a or b is 0
	}

	/**
	 * Return the product of the given integer numbers.
	 * 
	 * @param a
	 *            The left operand for the product.
	 * @param b
	 *            The right operand for the product.
	 * @return The product of the given numbers. | result == a * b
	 * @throws TimesOverflowException
	 *             [must] The given integer numbers cannot be multiplied. | !
	 *             areMultipliable(a,b)
	 */
	public static long times(long a, long b) throws TimesOverflowException {
		if (!areMultipliable(a, b))
			throw new TimesOverflowException(a, b);
		return a * b;
	}

	/**
	 * Return the greatest common divisor of the given integer numbers.
	 * 
	 * @param a
	 *            The first integer number for which the greatest common divisor
	 *            must be calculated.
	 * @param b
	 *            The other integer number for which the greatest common divisor
	 *            must be calculated.
	 * @return The resulting value divides both integer numbers a and b. | (a %
	 *         result == 0) && (b % result == 0)
	 * @return No integer number larger than the resulting value divides both
	 *         integer numbers a and b. | for each number in
	 *         result+1..Long.MAX_VALUE: | (a % number != 0) || (b % number !=
	 *         0)
	 * @throws IllegalArgumentException
	 *             [must] At least one of the given integer numbers is negative. |
	 *             (a < 0) || (b < 0)
	 * @throws IllegalArgumentException
	 *             [must] Both integer numbers are 0. | (a == 0) && (b == 0)
	 */
	public static long gcd(long a, long b) throws IllegalArgumentException {
		if ((a < 0) || (b < 0))
			throw new IllegalArgumentException();
		if ((a == 0) && (b == 0))
			throw new IllegalArgumentException();
		while ((a != 0) && (b != 0))
			if (a > b)
				a = a % b;
			else
				b = b % a;
		if (b == 0)
			return a;
		else
			return b;
	}

	/**
	 * Return the sign of the given integer number.
	 * 
	 * @param a
	 *            The given integer number.
	 * @return +1 if the given integer number is positive; -1 if the given
	 *         integer number is negative; 0 if the given integer number is 0. |
	 *         if (a > 0) | then result == 1 | else if (a < 0) | then result ==
	 *         -1 | else | result == 0
	 */
	public static int sign(long a) {
		if (a > 0)
			return 1;
		if (a < 0)
			return -1;
		return 0;
	}

}