package marriage;

import be.kuleuven.cs.som.annotate.Raw;

/**
 * A class of persons involving a gender and a marital partner.
 * 
 * @invar The gender of each person must be a valid gender for a person. |
 *        isValidGender(getGender())
 * @invar Each person must can have its spouse as its spouse. |
 *        canHaveAsSpouse(getSpouse())
 * 
 * @version 2.0
 * @author Eric Steegmans
 */
public class Person {

	/**
	 * Initialize this new person with given gender and given partner.
	 * 
	 * @param gender
	 *            The gender for this new person.
	 * @param person
	 *            The partner for this new person.
	 * @post The gender of this new person is set to the given gender. |
	 *       new.getGender() == gender
	 * @post This new person references the given partner as its spouse. |
	 *       new.getSpouse() == partner
	 * @post If the given partner references a true person, that partner
	 *       references this new person as its spouse. | if (partner != null) |
	 *       then (new partner).getSpouse() == this
	 * @post This new person is not terminated() | ! new.isTerminated()
	 * @throws IllegalArgumentException
	 *             [must] The given gender is not a valid gender for a person. | !
	 *             isValidGender(gender)
	 * @throws IllegalPartnerException
	 *             [must] The given partner is effective, and either its gender
	 *             is the same as the given gender, or this new person cannot
	 *             accept the given partner as its spouse. | (partner != null) && | (
	 *             (gender == partner.getGender()) || | (!
	 *             canHaveAsSpouse(partner)) )
	 * @note In the specification of this constructor, we cannot use the mutator
	 *       'marry'. Indeed, upon entry to the constructor, the gender of this
	 *       new person is still null, meaning that that new person does not
	 *       satisfy its class invariants. Because the method 'marry' is not a
	 *       raw method, we cannot invoke it against the new person. This also
	 *       explains why the last throws-clause has to specify that the
	 *       exception is also thrown if the given partner has the same gender
	 *       as the given gender. Indeed, the conditions for throwing an
	 *       exception apply to the state of all objects upon entry to the
	 *       constructor. The method canAcceptAsSpouse is thus invoked against
	 *       the new person at the time its gender is not set yet.
	 */
	public Person(Gender gender, Person partner)
			throws IllegalArgumentException, IllegalPartnerException {
		this(gender);
		if (partner != null)
			marry(partner);
		// Resetting the gender back to its default value in case
		// an exception is thrown in marrying, is impossible. Indeed,
		// the gender must be set before the method marry can be invoked
		// against this new person (Because marry is a public method,
		// all objects involved in it must satisfy their class
		// invariants). Once a final instance variable is given its
		// value, it cannot be given another value.
	}

	/**
	 * Initialize this new person as an unmarried person with given gender.
	 * 
	 * @param gender
	 *            The gender for this new person.
	 * @effect This new person is initialized with the given gender and with a
	 *         non-effective partner. | this(gender,null)
	 */
	public Person(Gender gender) throws IllegalArgumentException {
		// Impossible to invoke the more general constructor here,
		// because it throws a checked exception. We therefore
		// invoke this constructor in the more extended constructor.
		if (!isValidGender(gender))
			throw new IllegalArgumentException("Improper gender!");
		this.gender = gender;
	}

	/**
	 * Initialize this new person as an unmarried person of female gender.
	 * 
	 * @effect This new person is initialized with the female gender. |
	 *         this(Gender.FEMALE)
	 */
	public Person() {
		this(Gender.FEMALE);
	}

	/**
	 * Terminate this person, breaking the marriage in which that person might
	 * be involved.
	 * 
	 * @post This person is terminated. | new.isTerminated()
	 * @effect This person is divorced from its spouse, if any. | this.divorce()
	 */
	public void terminate() {
		divorce();
		// We feel no need to introduce a setter for "isTerminated". It is
		// only used at this point. Moreover, it should not be possible to
		// bring an object back to live.
		this.isTerminated = true;
	}

	/**
	 * Return a boolean indicating whether or not this person is terminated.
	 */
	@Raw
	public boolean isTerminated() {
		return this.isTerminated;
	}

	/**
	 * Variable registering whether this person is terminated.
	 */
	private boolean isTerminated = false;

	/**
	 * Return the gender of this person.
	 */
	@Raw
	public Gender getGender() {
		return this.gender;
	}

	/**
	 * Check whether a person can have the given gender as its gender.
	 * 
	 * @param gender
	 *            The gender to check.
	 * @return True if the given gender is the male gender or the female gender;
	 *         false otherwise. | result == | (gender == Gender.MALE) || |
	 *         (gender == Gender.FEMALE)
	 */
	public static boolean isValidGender(Gender gender) {
		return (gender == Gender.MALE) || (gender == Gender.FEMALE);
	}

	/**
	 * Return a boolean reflecting whether this person is a female.
	 * 
	 * @return True if the gender of this person is the female gender; false
	 *         otherwise. | result == (getGender() == Gender.FEMALE)
	 */
	@Raw
	public boolean isFemale() {
		return getGender() == Gender.FEMALE;
	}

	/**
	 * Return a boolean reflecting whether this person is a male.
	 * 
	 * @return True if the gender of this person is the male gender; false
	 *         otherwise. | result == (getGender() == Gender.MALE)
	 */
	@Raw
	public boolean isMale() {
		return getGender() == Gender.MALE;
	}

	/**
	 * Variable referencing the gender of this person.
	 */
	private final Gender gender;

	/**
	 * Return the spouse of this person. A null reference is returned if this
	 * person is not married.
	 */
	@Raw
	public Person getSpouse() {
		return this.spouse;
	}

	/**
	 * Check whether this person can have the other person as its spouse.
	 * 
	 * @param other
	 *            The other person to check.
	 * @return True if the other person is not effective. | if (other == null) |
	 *         then result == true
	 * @return Otherwise, false if this person or the other person is
	 *         terminated. | else if ( this.isTerminated() ||
	 *         other.isTerminated() ) | then result == false
	 * @return Otherwise, false if this person has the same gender as the other
	 *         person. | else if ( this.getGender() == other.getGender() ) |
	 *         then result == false
	 * @return Otherwise, if this person does not have the other person as its
	 *         spouse, true if both this person and the other person are
	 *         unmarried, false otherwise. | else if (this.getSpouse() != other ) |
	 *         then result == | (! this.isMarried()) && (! other.isMarried())
	 * @return Otherwise, true if the other person references this person as its
	 *         spouse, false otherwise. | else | result == (other.getSpouse() ==
	 *         this)
	 */
	@Raw
	public boolean canHaveAsSpouse(@Raw
	Person other) {
		if (other == null)
			return true;
		else if ((other.isTerminated()) || (this.isTerminated()))
			return false;
		else if (this.getGender() == other.getGender())
			return false;
		else if (this.getSpouse() != other)
			return (!this.isMarried()) && (!other.isMarried());
		else
			return (other.getSpouse() == this);
	}

	/**
	 * Check whether this person is married.
	 * 
	 * @return True if this person references an effective spouse; false
	 *         otherwise. | result == (getSpouse() != null)
	 */
	@Raw
	public boolean isMarried() {
		return getSpouse() != null;
	}

	/**
	 * Register a marriage between this person and the given partner.
	 * 
	 * @param partner
	 *            The new partner for this person.
	 * @post The given partner is registered as the spouse of this person. |
	 *       new.getSpouse() == partner
	 * @post This person is registered as the spouse of the given partner. |
	 *       (new partner).getSpouse() == this
	 * @throws IllegalPartnerException
	 *             [must] The given partner is not effective, or this person
	 *             cannot have the given partner as its spouse. | (partner ==
	 *             null) || (! this.canHaveAsSpouse(partner))
	 */
	public void marry(Person partner) throws IllegalPartnerException {
		if ((partner == null) || (!canHaveAsSpouse(partner)))
			throw new IllegalPartnerException(this, partner);
		setSpouse(partner);
		partner.setSpouse(this);
	}

	/**
	 * Register a divorce between this person and its partner, if any.
	 * 
	 * @post This person is no longer married. | ! new.isMarried()
	 * @post The former spouse of this person, if any, is no longer married. |
	 *       if (this.isMarried()) | then ! (new
	 *       (this.getSpouse())).isMarried())
	 */
	public void divorce() {
		try {
			getSpouse().setSpouse(null);
			this.setSpouse(null);
		} catch (NullPointerException exc) {
			assert (!this.isMarried());
		}
	}

	/**
	 * Register a switch of partners between this person and the other person.
	 * 
	 * @param other
	 *            The person to switch partners with.
	 * @post This person is married with the other person or with the spouse of
	 *       the other person. | (new.getSpouse() == other) || |
	 *       (new.getSpouse() == other.getSpouse())
	 * @post This person's spouse, if any, is married with the other person or
	 *       with the spouse of the other person. | if (isMarried()) then | (
	 *       ((new getSpouse()).getSpouse() == other) || | ((new
	 *       getSpouse())getSpouse() == other.getSpouse()) )
	 * @post The other person is married with this person or with this person's
	 *       spouse. | ((new other).getSpouse() == this) || | ((new
	 *       other).getSpouse() = this.getSpouse())
	 * @post The other person's spouse, if any, is married with this person or
	 *       with this person's spouse. | if (other.isMarried()) then | ( ((new
	 *       other.getSpouse()).getSpouse() == this) || | ((new
	 *       other.getSpouse()).getSpouse() = this.getSpouse()) )
	 * @note In case this person nor the other person are married, and both
	 *       persons are of different gender, the specification leaves 2
	 *       options: (1) leave things as they are, or (2) have this person
	 *       marry the other person.
	 * @throws IllegalArgumentException
	 *             [must] The other person is not effective. | other == null
	 */
	public void switchPartnerWith(Person other) throws IllegalArgumentException {
		try {
			if (this.getGender() != other.getGender()) {
				if (this.isMarried())
					this.getSpouse().switchPartnerWith(other);
				else if (other.isMarried())
					switchPartnerWith(other.getSpouse());
			} else {
				assert (other != null)
						&& ((this.getGender() == other.getGender()) || ((!this
								.isMarried()) && (!other.isMarried())));
				Person thisSpouse = this.getSpouse();
				Person otherSpouse = other.getSpouse();
				this.divorce();
				other.divorce();
				if (otherSpouse != null)
					this.marry(otherSpouse);
				if (thisSpouse != null)
					other.marry(thisSpouse);
			}
		} catch (NullPointerException exc) {
			assert (other == null);
			throw new IllegalArgumentException("Non-effective person!");
		} catch (IllegalPartnerException exc2) {
			assert false;
		}
	}

	/**
	 * Register the given person as the spouse of this person.
	 * 
	 * @param person
	 *            The person to be registered as the spouse of this person.
	 * @pre This person must be able to have the given person as its spouse. |
	 *      canHaveAsSpouse(person)
	 * @post The spouse of this person is set to the given person. |
	 *       new.getSpouse() == person
	 */
	@Raw
	private void setSpouse(@Raw
	Person person) {
		this.spouse = person;
	}

	/**
	 * Variable referencing the spouse of this person.
	 */
	private Person spouse = null;

}