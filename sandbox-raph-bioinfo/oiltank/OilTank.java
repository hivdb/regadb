package oiltank;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class of tanks for storing oil, involving a capacity and a content.
 * 
 * @invar The capacity of each oil tank must be a valid capacity for an oil
 *        tank. | isValidCapacity(getCapacity())
 * @invar The contents of each oil tank must be a valid contents for an oil tank
 *        in view of its capacity. |
 *        isValidContents(getContents(),getCapacity())
 * 
 * @version 2.0
 * @author Eric Steegmans
 */
public class OilTank {

	/**
	 * Initialize this new oil tank with given capacity and given contents.
	 * 
	 * @param capacity
	 *            The capacity for this new oil tank.
	 * @param contents
	 *            The contents for this new oil tank.
	 * @pre The given capacity must be a valid capcity for an oil tank. |
	 *      isValidCapacity(capacity)
	 * @pre The given contents must be a valid contents for an oil tank in view
	 *      of the given capacity. | isValidContents(contents,capacity)
	 * @post The capacity of this new oil tank is equal to the given capacity. |
	 *       new.getCapacity() == capacity
	 * @post The contents of this new oil tank is equal to the given contents. |
	 *       new.getContents() == contents
	 * @note Upon exit from this constructor, this new oil tank will satisfy all
	 *       its class invariants.
	 * @note In the specification of this constructor, we cannot use the model
	 *       method setContents. The precondition for that method would expand
	 *       to isValidContents(contents,getCapacity()) and the capacity of this
	 *       new oil tank upon entry to the constructor is unknown, because
	 *       final instance variables are not initialized upon entry to a
	 *       constructor (unless they are explicitly initialized in their
	 *       declaration).
	 */
	public OilTank(int capacity, int contents) {
		this.capacity = capacity;
		setContents(contents);
	}

	/**
	 * Initialize this new oil tank with given capacity and no contents.
	 * 
	 * @param capacity
	 *            The capacity for this new oil tank.
	 * @effect This new oil tank is initialized with the given capacity and with
	 *         no contents. | this(capacity,0)
	 */
	public OilTank(int capacity) {
		this(capacity, 0);
	}

	/**
	 * Initialize this new oil tank with a capacity of 5000 and no contents.
	 * 
	 * @effect This new oil tank is initialized with a capacity of 5000 and with
	 *         no contents. | this(5000)
	 */
	public OilTank() {
		this(5000);
	}

	/**
	 * Return the capacity of this oil tank. The capacity expresses the maximum
	 * amount of oil that can be stored in this oil tank.
	 * 
	 * @note This method must be qualified raw because it is used in the
	 *       specification of a class invariant.
	 */
	@Raw
	public int getCapacity() {
		return this.capacity;
	}

	/**
	 * Check whether the given capacity is a valid capacity for any oil tank.
	 * 
	 * @param capacity
	 *            The capacity to check.
	 * @return True if the given capacity is positive; false otherwise. | result ==
	 *         (capacity > 0)
	 * @note Raw annotations do not apply to static methods.
	 */
	public static boolean isValidCapacity(int capacity) {
		return capacity > 0;
	}

	/**
	 * Variable registering the capacity of this oil tank.
	 * 
	 * @note Final instance variables are not initialized to the default value
	 *       of their type upon entry to a constructor.
	 */
	private final int capacity;

	/**
	 * Return the contents of this oil tank. The contents expresses the actual
	 * amount of oil stored in this oil tank.
	 * 
	 * @note This method must be qualified raw because it is used in the
	 *       specification of a class invariant.
	 */
	@Raw
	public int getContents() {
		return this.contents;
	}

	/**
	 * Check whether the given contents is a valid contents for any oil tank
	 * under the given capacity.
	 * 
	 * @param contents
	 *            The contents to check.
	 * @param capacity
	 *            The capacity to check the contents against.
	 * @return True if the given contents is not negative and does not exceed
	 *         the given capacity; false otherwise. | result == | (contents >=
	 *         0) && (contents <= capacity)
	 * @note The capacity is also explicitly supplied as an argument, such that
	 *       the method can also be used at times oil tanks have not been
	 *       initialized properly, such as upon entry to a constructor.
	 */
	public static boolean isValidContents(int contents, int capacity) {
		return (contents >= 0) && (contents <= capacity);
	}

	/**
	 * Set the contents of this oil tank to the given amount.
	 * 
	 * @param contents
	 *            The new contents for this oil tank.
	 * @pre The given contents must be a valid contents for this oil tank, in
	 *      view of its current capacity. |
	 *      isValidContents(contents,getCapacity())
	 * @post The contents of this oil tank is equal to the given contents. |
	 *       new.getContents() == contents
	 * @note This method is qualified
	 * @Raw, such that it can be used in initializing new oil tanks, even at
	 *       times at which they do not yet satisfy all their class invariants.
	 * @note This method is qualified
	 * @Model, such that it can be used in specifications of public methods.
	 *         However, instead of using this method in specifications of some
	 *         of the mutators, we could just as well have worked out
	 *         postconditions for those methods. The annotation is introduced
	 *         here just for purposes of illustration.
	 */
	@Raw
	@Model
	private void setContents(int contents) {
		this.contents = contents;
	}

	/**
	 * Variable registering the amount of oil currently stored in this oil tank.
	 */
	private int contents;

	/**
	 * Return the free space of this oil tank. The free space is the maximum
	 * amount of oil that can still be added to this oil tank.
	 * 
	 * @return The difference between the capacity of this oil tank and its
	 *         contents. | result == getCapacity() - getContents()
	 * @note This method could have been qualified 'raw'. However, in the
	 *       current definition, there is no need for that.
	 */
	public int getFree() {
		return getCapacity() - getContents();
	}

	/**
	 * Check whether this oil tank is completely filled with oil.
	 * 
	 * @return True if the free space in this oil tank is 0; false otherwise. |
	 *         result == (getFree() == 0)
	 * @note This method can only be qualified 'raw', if the method getFree() is
	 *       also qualified 'raw'.
	 */
	public boolean isFull() {
		return getFree() == 0;
	}

	/**
	 * Check whether this oil tank is empty.
	 * 
	 * @return True if the contents of this oil tank is 0; false otherwise. |
	 *         result == (getContents() == 0)
	 */
	public boolean isEmpty() {
		return getContents() == 0;
	}

	/**
	 * Fill this oil tank with the given amount of oil.
	 * 
	 * @param amount
	 *            The amount to be added to this oil tank.
	 * @pre The given amount must be positive. | amount > 0
	 * @effect The contents of this oil tank is set to its current contents
	 *         incremented with the given amout of oil. |
	 *         setContents(getContents() + amount)
	 * @note Because we use the method setContents in the specification, this
	 *       method has another precondition, namely
	 *       isValidContents(getContents()+amount,getCapacity()
	 * @note Because this is not a 'raw' method, this oil tank must satisfy its
	 *       class invariants upon entry to this method. Upon exit, it is easy
	 *       to see that this oil tank then still satisfies its class
	 *       invariants.
	 * @note This method could be annotated
	 * @Raw, because (1) there is no oiltank that does not satisfy its class
	 *       invariants but still satisfies the preconditions for this method,
	 *       for which this method will not achieve its postcondition, and (2)
	 *       because all the methods used in the specification and in the
	 *       implementation are raw methods.
	 */
	public void fill(int amount) {
		setContents(getContents() + amount);
	}

	/**
	 * Fill this oil tank to its full capacity.
	 * 
	 * @post This oil tank is completely filled with oil. | new.isFull()
	 * @note We prefer not to specify this method in terms of other mutators. We
	 *       feel that such a specification would impose too much restrictions
	 *       on a possible implementation.
	 * @note This method cannot be qualified 'raw', even if the method
	 *       'fill(int)' would be qualified 'raw'. Indeed, an oiltank with
	 *       capacity 2000 and contents 3000 (which obviously does not satisfy
	 *       its invariants), would invoke the method 'fill(int)' with a
	 *       negative value, which would violate one of the preconditions for
	 *       that method.
	 */
	public void fill() {
		// We must check whether this oil tank is not already full,
		// because otherwise the method 'fill(int) would be invoked
		// with an amount of 0, which would violate the first precondition
		// of that method.
		if (!isFull())
			fill(getFree());
	}

	/**
	 * Fill this oil tank with the given amounts of oil.
	 * 
	 * @param amounts
	 *            The amounts to be added to this oil tank.
	 * @pre Each of the supplied amounts must be positive.
	 * @post The contents of this oil tank is set to its current contents,
	 *       incremented with the total of all the supplied amounts.
	 * @note Formal specifications for this method have been left out, because
	 *       we need quantifiers from first-order logic, which we only discuss
	 *       in part II.
	 */
	public void fill(int... amounts) {
		for (int amount : amounts)
			fill(amount);
	}

	/**
	 * Extract the given amount of oil from this oil tank.
	 * 
	 * @param amount
	 *            The amount to be extracted from this oil tank.
	 * @pre The given amount must be positive. | amount > 0
	 * @effect The contents of this oil tank is set to its current contents
	 *         decremented with the given amout of oil. |
	 *         setContents(getContents() - amount)
	 */
	public void extract(int amount) {
		setContents(getContents() - amount);
	}

	/**
	 * Extract the entire contents from this oil tank.
	 * 
	 * @post This oil tank is empty. | new.isEmpty()
	 * @note It is easy to see that this method could have been qualified 'raw'.
	 */
	public void extract() {
		setContents(0);
	}

	/**
	 * Extract the given amounts of oil from this oil tank with.
	 * 
	 * @param amounts
	 *            The amounts to be extracted from this oil tank.
	 * @pre Each of the supplied amounts must be positive.
	 * @post The contents of this oil tank is set to its current contents,
	 *       decremented with the total of all the supplied amounts.
	 */
	public void extract(int... amounts) {
		for (int amount : amounts)
			extract(amount);
	}

	/**
	 * Transfer the entire contents from the other oil tank into this oil tank.
	 * 
	 * @param other
	 *            The oil tank to transfer oil from.
	 * @pre The other oil tank must be effective. | other != null
	 * @pre The other oil tank must not be the same as this oil tank. | other !=
	 *      this
	 * @effect This oil tank is filled with the contents of the other oil tank. |
	 *         fill(other.getContents())
	 * @effect All oil is extracted from the other oil tank. | other.extract()
	 */
	public void transferFrom(OilTank other) {
		this.fill(other.getContents());
		other.extract();
	}

	/**
	 * Return the percentage of the capacity of this oil tank is currently
	 * filled with oil.
	 * 
	 * @return The contents of this oil tank divided by its capacity, returned
	 *         as a percentage. | result == (100.0F * getContents()) /
	 *         getCapacity()
	 */
	public float getPercentageFilled() {
		// The multiplication of the (integer) contents with the floating
		// point literal 100.0F yields a floating point value. The
		// subsequent division is therefore a floating point division
		// and not an integer division.
		return (100.0F * getContents()) / getCapacity();
	}

	/**
	 * Check whether this oil tank is relatively more filled than the other oil
	 * tank.
	 * 
	 * @param other
	 *            The oil tank to compare with.
	 * @pre The other oil tank must be effective. | other != null
	 * @return True if the filled percentage of this oil tank exceeds the filled
	 *         percentage of the other oil tank; false otherwise. | result == |
	 *         this.getPercentageFilled() > other.getPercentageFilled()
	 */
	public boolean isRelativelyFullerThan(OilTank other) {
		return this.getPercentageFilled() > other.getPercentageFilled();
	}

	/**
	 * Return a textual representation of this oil tank.
	 * 
	 * @return An effective string of the following form "Oil tank with capacity
	 *         xxx and contents yyy". | result.equals | ("Oil tank with capacity " +
	 *         getCapacity() + | " and contents " + getContents())
	 * @note A default version of this method is inherited from the root class
	 *       Object. The method is invoked at points where a string is expected
	 *       and an oil tank is passed (Example: System.out.println(myTank))
	 */
	@Override
	public String toString() {
		return "Oil tank with capacity " + getCapacity() + " and contents "
				+ getContents();
	}

	/**
	 * Check whether this oil tank is identical to the other oil tank.
	 * 
	 * @param other
	 *            The other oil tank to compare with.
	 * @pre The other oil tank must be effective. | other != null
	 * @return True if the capacity and the contents of this oil tank are equal
	 *         to the capacity, respectively the contents of the other oil tank. |
	 *         result == | (getCapacity() == other.getCapacity()) && |
	 *         (getContents() == other.getContents())
	 * @note This method stands next to the method equals(Object) inherited from
	 *       the root class Object, which performs a referential comparison.
	 */
	public boolean isSameAs(OilTank other) {
		return (this.getCapacity() == other.getCapacity())
				&& (this.getContents() == other.getContents());
	}

	/**
	 * Return a new oil tank as an exact copy of this oil tank.
	 * 
	 * @return A new effective oil tank, whose capacity and contents are
	 *         identical to the capacity and the contents of this oil tank. |
	 *         (result != null) && (result != this) && | result.isSameAs(this)
	 * @note It would be better to use the clone() method, inherited from the
	 *       root class Object. At this point in the course, we have not covered
	 *       all concepts to work out such a redefinition.
	 */
	public OilTank getCopy() {
		return new OilTank(getCapacity(), getContents());
	}

}