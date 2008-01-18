package rational;

import be.kuleuven.cs.som.annotate.Raw;

/**
 * A class of rational numbers with integer numerator and denominator.
 * 
 * @invar The denominator of each rational number must be a legal denominator
 *        for a rational number. | isValidDenominator(getDenominator())
 * 
 * @version 2.0
 * @author Eric Steegmans
 */
public class Rational {

	/**
	 * Initialize this new rational number with given numerator and denominator.
	 * 
	 * @param numerator
	 *            The numerator for this new rational number.
	 * @param denominator
	 *            The denominator for this new rational number.
	 * @post The numerator of this new rational number is set to the given
	 *       numerator. | new.getNumerator() == numerator
	 * @post The denominator of this new rational number is set to the given
	 *       denominator. | new.getDenominator() == denominator
	 * @throws IllegalDenominatorException
	 *             [must] The given denominator is not a legal denominator for a
	 *             rational number. | ! isValidDenominator(denominator)
	 */
	public Rational(long numerator, long denominator)
			throws IllegalDenominatorException {
		if (!isValidDenominator(denominator))
			throw new IllegalDenominatorException(denominator);
		this.numerator = numerator;
		this.denominator = denominator;
	}

	/**
	 * Initialize this new rational number with numerator 1 and given
	 * denominator.
	 * 
	 * @param denominator
	 *            The denominator for this new rational number.
	 * @effect This new rational number is initialized with numerator 1 and with
	 *         the given denominator. | this(1,denominator)
	 */
	public Rational(long denominator) throws IllegalDenominatorException {
		this(1, denominator);
	}

	/**
	 * Initialize this new rational number with numerator 0 and arbitrary
	 * denominator.
	 * 
	 * @post The numerator of this new rational number is set to 0. |
	 *       new.getNumerator() == 0
	 * @post The denominator of this new rational number is set to an arbitrary
	 *       valid value. | isValidDenominator(new.getDenominator())
	 */
	public Rational() {
		// Another constructor cannot be invoked at this point,
		// because Java offers no abilities to catch exceptions
		// they might throw.
		this.numerator = 0;
		this.denominator = 1;
	}

	/**
	 * Return the numerator of this rational number.
	 */
	@Raw
	public long getNumerator() {
		return numerator;
	}

	/**
	 * Return the denominator of this rational number.
	 */
	@Raw
	public long getDenominator() {
		return denominator;
	}

	/**
	 * Check whether the given denominator is a valid denominator for any
	 * rational number.
	 * 
	 * @param denominator
	 *            The denominator to check.
	 * @return True if the given denominator is positive; false otherwise. |
	 *         result == denominator > 0
	 */
	public static boolean isValidDenominator(long denominator) {
		return denominator > 0;
	}

	/**
	 * Return a boolean reflecting whether this rational number is identical to
	 * the given rational number.
	 * 
	 * @param other
	 *            The rational number to compare with.
	 * @return True if the numerator and denominator of this rational number are
	 *         equal to the numerator, respectively the denominator of the other
	 *         rational number; false otherwise. | result == | (getNumerator() ==
	 *         other.getNumerator()) && | (getDenominator() ==
	 *         other.getDenominator())
	 * @throws IllegalArgumentException
	 *             [must] The other rational number is not effective. | other ==
	 *             null
	 * @note This method or hasSameValueAs is better worked out as a
	 *       redefinition of the method equals inherited from the class Object.
	 *       At this point in the course, not all ingredients to work out such a
	 *       redefinition have been covered.
	 */
	public boolean isIdenticalTo(Rational other)
			throws IllegalArgumentException {
		try {
			return (getNumerator() == other.getNumerator())
					&& (getDenominator() == other.getDenominator());
		} catch (NullPointerException exc) {
			// It is good practice to add assert statements to catchers,
			// clarifying
			// the conditions under which the exception can be thrown.
			assert (other == null);
			throw new IllegalArgumentException("Non-effective rational number!");
		}
	}

	/**
	 * Check whether this rational number has the same value as the given
	 * rational number.
	 * 
	 * @param other
	 *            The rational number to compare with.
	 * @return True if this rational number in normalized form is identical to
	 *         the other rational number in normalized form; false otherwise. |
	 *         result == | this.normalize().isIdenticalTo(other.normalize())
	 * @throws IllegalArgumentException
	 *             [must] The other rational number is not effective. | other ==
	 *             null
	 */
	public boolean hasSameValueAs(Rational other)
			throws IllegalArgumentException {
		try {
			Rational first = this.normalize();
			Rational second = other.normalize();
			return first.isIdenticalTo(second);
		} catch (NullPointerException exc) {
			assert (other == null);
			throw new IllegalArgumentException("Non-effective rational number!");
		}
	}

	/**
	 * Return a rational number obtained by multiplying this rational number
	 * with the given integer number.
	 * 
	 * @param factor
	 *            The integer number to multiply with.
	 * @return The resulting rational number is effective. | result != null
	 * @return A rational number that has the same value as a rational number
	 *         whose denominator is equal to the denominator of this rational
	 *         number in normalized form, and whose numerator is equal to the
	 *         numerator of this rational number in normalized form multiplied
	 *         with the given factor divided by the greatest common divisor of
	 *         the absolute value of that factor and the denominator of this
	 *         rational number in normalized form. | let | reducedFactor =
	 *         factor / ExtMath.gcd |
	 *         (Math.abs(factor),this.normalize().getDenominator()) | in |
	 *         result.hasSameValueAs( | new Rational( |
	 *         this.normalize().getNumerator()*reducedFactor, |
	 *         this.normalize().getDenominator()))
	 * @throws TimesOverflowException
	 *             [must] The product of the numerator of this rational number
	 *             in normalized form with the given factor divided by the
	 *             greatest common divisor of the absolute value of that factor
	 *             and the denominator of this rational number in normalized
	 *             form is outside the range of the type long. | let |
	 *             reducedFactor = factor / ExtMath.gcd |
	 *             (Math.abs(factor),this.normalize().getDenominator()) | in | !
	 *             ExtMath.areMulipliable |
	 *             (this.normalize().getNumerator(),reducedFactor)
	 */
	public Rational times(long factor) throws TimesOverflowException {
		try {
			try {
				long newNumerator = ExtMath.times(getNumerator(), factor);
				return new Rational(newNumerator, getDenominator());
			} catch (TimesOverflowException exc) {
				assert !ExtMath.areMultipliable(getNumerator(), factor);
				if (!this.isNormalized())
					return normalize().times(factor);
				long commonFactor = ExtMath.gcd(Math.abs(factor),
						getDenominator());
				if (commonFactor > 1) {
					Rational thisReduced = new Rational(this.getNumerator(),
							this.getDenominator() / commonFactor);
					return thisReduced.times(factor / commonFactor);
				}
				throw exc;
			}
		} catch (IllegalDenominatorException exc) {
			assert false;
			return null;
		}
	}

	/**
	 * Return a new rational number obtained by adding the given rational number
	 * to this rational number. Only a simple version is worked out here.
	 * 
	 * @param other
	 *            The righthand side of the addition.
	 * @return The resulting rational number is effective. | result != null
	 * @return The numerator of the resulting rational number is the sum of the
	 *         cross-products of numerators and denominators of this rational
	 *         number and the other rational number. | result.getNumerator() == |
	 *         getNumerator()*other.getDenominator() + |
	 *         getDenominator()*other.getNumerator()
	 * @return The denominator of the resulting rational number is the product
	 *         of both denominators. | result.getDenominator() == |
	 *         getDenominator()*other.getDenominator()
	 * @throws IllegalArgumentException
	 *             [must] The other rational number is not effective. | other ==
	 *             null
	 * @throws OverflowException
	 *             [must] The numerator of the one rational number cannot be
	 *             multiplied with the denominator of the other rational number. | (!
	 *             ExtMath.areMultipliable |
	 *             (getNumerator(),other.getDenominator()) ) || | (!
	 *             ExtMath.areMultipliable |
	 *             (getDenominator(),other.getNumerator()) )
	 * @throws OverflowException
	 *             [must] The cross-products of the numberator of the one
	 *             rational rational number with the denominator of the other
	 *             rational number cannot be added. | ! ExtMath.areAddable |
	 *             (getNumerator() * other.getDenominator(), | getDenominator() *
	 *             other.getNumerator())
	 * @throws OverflowException
	 *             [must] The denominators of both rational numbers cannot be
	 *             multiplied. | ! ExtMath.areMultipliable |
	 *             (getDenominator(),other.getDenominator())
	 */
	public Rational add(Rational other) throws OverflowException,
			IllegalArgumentException {
		try {
			long prod1 = ExtMath.times(getNumerator(), other.getDenominator());
			long prod2 = ExtMath.times(getDenominator(), other.getNumerator());
			long numer = ExtMath.add(prod1, prod2);
			long denom = ExtMath
					.times(getDenominator(), other.getDenominator());
			return new Rational(numer, denom);
		} catch (IllegalDenominatorException exc) {
			assert false;
			return null;
		} catch (NullPointerException exc) {
			assert other == null;
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Return the common factor of the numerator and the denominator of this
	 * rational number.
	 * 
	 * @return The greatest common divisor of the absolute value of the
	 *         numerator and the denominator of this rational number. | result == |
	 *         ExtMath.gcd(Math.abs(getNumerator()),getDenominator())
	 */
	public long getCommonFactor() {
		return ExtMath.gcd(Math.abs(getNumerator()), getDenominator());
	}

	/**
	 * Return a new rational number in normalized form with the same value as
	 * this rational number.
	 * 
	 * @return The resulting rational number is effective. | result != null
	 * @return Numerator and denominator of the resulting rational number are
	 *         equal to the numerator, respectively the denominator of this
	 *         rational number divided by the common factor of this rational
	 *         number. | (result.getNumerator() ==
	 *         (getNumerator()/getCommonFactor())) && | (result.getDenominator() ==
	 *         getDenominator()/getCommonFactor())
	 */
	public Rational normalize() {
		try {
			long commonFactor = getCommonFactor();
			if (commonFactor > 1)
				return new Rational(getNumerator() / commonFactor,
						getDenominator() / commonFactor);
			else
				return this;
		} catch (IllegalDenominatorException exc) {
			assert (false);
			return null;
		}
	}

	/**
	 * Check whether this rational number is normalized.
	 * 
	 * @return True if the common factor of this rational number is 1; false
	 *         otherwise. | result == (getCommonFactor() == 1)
	 */
	public boolean isNormalized() {
		return (getCommonFactor() == 1);
	}

	/**
	 * Return a textual representation of this rational number.
	 * 
	 * @return A textual representation of this rational number in the form
	 *         [numerator/denominator]. |
	 *         result.equals("["+getNumerator()+"/"+getDenominator()+"]"
	 */
	@Override
	public String toString() {
		return "[" + getNumerator() + "/" + getDenominator() + "]";
	}

	/**
	 * Return a copy of this rational number.
	 * 
	 * @return This rational number. | result == this
	 * @note Because rational numbers are immutable, there is no need to create
	 *       a new rational number identical to this rational number.
	 * @note This method is best defined as a redefinition of the method clone
	 *       inherited from Object. At this point in the course, not all the
	 *       elements to work out such a redefinition have been covered.
	 */
	public Rational getCopy() {
		return this;
	}

	/**
	 * Variable registering the numerator (Dutch: 'teller') of this rational
	 * number.
	 */
	private final long numerator;

	/**
	 * Variable registering the denominator (Dutch: 'noemer') of this rational
	 * number.
	 */
	private final long denominator;

}