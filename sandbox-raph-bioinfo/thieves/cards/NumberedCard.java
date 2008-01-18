package thieves.cards;

import be.kuleuven.cs.som.annotate.Raw;

/**
 * A class of numbered cards used in the game of Thieves.
 *
 * @invar   The value of each numbered card must be a valid
 *          value for a numbered card.
 *          | isValidValue(getValue())
 *
 * @version  2.0
 * @author   Eric Steegmans
 */
public class NumberedCard extends Card {

	/**
	 * Initialize this new numbered card with given value.
	 *
	 * @param   value
	 *          The value for this new numbered card.
	 * @post    The value of this new numbered card is equal to the
	 *          given value.
	 *          | new.getValue() == value
	 * @throws  IllegalArgumentException [must]
	 *          The given value is not a valid value for a numbered card.
	 *          | ! isValidValue(value)
	 */
	public NumberedCard(int value) throws IllegalArgumentException {
		if (!isValidValue(value))
			throw new IllegalArgumentException(
				"Invalid value for numbered card!");
		this.value = value;
	}

	/**
	 * Return the value of this numbered card.
	 */
	@Raw public int getValue() {
		return this.value;
	}

	/**
	 * Check whether the given value is a valid value
	 * for a numbered card.
	 * 
	 * @return  True if the given value is positive and does not
	 *          exceed 10; false otherwise.
	 *          | result == (value >= 1) && (value <= 10)
	 */
	public static boolean isValidValue(int value) {
		return (value >= 1) && (value <= 10);
	}

	/**
	 * Variable registering the value of this numbered card.
	 * 
	 * @invar   The registered value is a legal value for
	 *          a numbered card.
	 *          | canHaveAsValue(value)
	 */
	private final int value;

	/**
	 * Check whether this numbered card matches on the given card.
	 *
	 * @return  True if the given card is an effective card that matches
	 *          on this numbered card; false otherwise.
	 *          | result ==
	 *          |   (other != null) && other.matchesOnNumbered(this)
	 */
	@Override public boolean matchesOn(Card other) {
		return (other != null) && other.matchesOnNumbered(this);
	}

	/**
	 * Check whether this numbered card matches on the given numbered card.
	 *
	 * @return  True if the given card is an effective numbered card with
	 *          value one higher or one lower than the value of this
	 *          numbered card; false otherwise.
	 *          | result ==
	 *          |   (other != null) &&
	 *          |   (Math.abs(getValue()-other.getValue()) == 1)
	 */
	@Override public boolean matchesOnNumbered(NumberedCard other) {
		return (other != null)
			&& (Math.abs(getValue() - other.getValue()) == 1);
	}

	/**
	 * Check whether this numbered card matches on the given jack card.
	 *
	 * @return  True if the given card is an effective jack card and
	 *          if the value of this numbered card is 10; false otherwise.
	 *          | result ==
	 *          |   (other != null) && (getValue() == 10)
	 */
	@Override public boolean matchesOnJack(JackCard other) {
		return (other != null) && (getValue() == 10);
	}

	/**
	 * Return a textual representation of this numbered card.
	 * 
	 * @return  The string "Card: " followed by the value of this
	 *          numbered card.
	 *          | result.equals("Card:" + getValue())
	 */
	@Override public String toString() {
		return "Card: " + getValue();
	}

	/**
	 * Check whether this numbered card is equal to the given object.
	 * 
	 * @return  True if the given object is an effective numbered card
	 *          with the same value as this numbered card; false otherwise.
	 *          | result ==
	 *          |   (other instanceof NumberedCard) &&
	 *          |   (getValue() == ((NumberedCard)other).getValue())
	 */
	@Override
	public boolean equals(Object other) {
		return (other instanceof NumberedCard)
			&& (getValue() == ((NumberedCard) other).getValue());
	}

	/**
	 * Return the hash code of this numbered card.
	 * 
	 * @return  The value of this numbered card.
	 *          | result == getValue()
	 */
	@Override
	public int hashCode() {
		return getValue();
	}

}