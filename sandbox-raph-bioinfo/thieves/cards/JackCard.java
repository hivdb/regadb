package thieves.cards;

/**
 * A class of jack cards used in a game of Thieves.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public class JackCard extends Card {

	/**
	 * Initialize this new jack card.
	 */
	public JackCard() {
	}

	/**
	 * Check whether this jack card matches on the given card.
	 *
	 * @return  True if the given card is an effective card that matches
	 *          on this jack card; false otherwise.
	 *          | result ==
	 *          |   (other != null) && other.matchesOnJack(this)
	 */
	@Override
	public boolean matchesOn(Card other) {
		return (other != null) && other.matchesOnJack(this);
	}

	/**
	 * Check whether this jack card matches on the given numbered card.
	 *
	 * @return  True if the given card is an effective numbered card
	 *          with value 10; false otherwise.
	 *          | result ==
	 *          |   (other != null) && (other.getValue() == 10)
	 * @see		superclass
	 */
	@Override
	public boolean matchesOnNumbered(NumberedCard other) {
		return (other != null) && other.getValue() == 10;
	}

	/**
	 * Check whether this jack card matches on the given jack card.
	 *
	 * @return  Always false.
	 *          | result == false
	 */
	@Override
	public boolean matchesOnJack(JackCard other) {
		return false;
	}

	/**
	 * Return a textual representation of this jack card.
	 * 
	 * @return  The string "Jack".
	 *          | result.equals("Jack")
	 */
	@Override
	public String toString() {
		return "Jack";
	}

	/**
	 * Check whether this jack card is equal to the given object.
	 * 
	 * @return  True if the given object is an effective jack card;
	 *          false otherwise.
	 *          | result == (other instanceof JackCard)
	 */
	@Override
	public boolean equals(Object other) {
		return (other instanceof JackCard);
	}

	/**
	 * Return the hash code of this jack card.
	 * 
	 * @return  Always 11.
	 *          | result == 11
	 */
	@Override
	public int hashCode() {
		return 11;
	}

}