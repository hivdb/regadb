package thieves.cards;


/**
 * A class of cards used in the game of Thieves.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public abstract class Card {

    /**
     * Check whether this card matches on the given card.
     *
     * @param   other
     *          The card to match with.
     * @return  False if the given card is not an effective card.
     *          | if (other == null)
     *          |   then result == false
     */
    public abstract boolean matchesOn(Card other);

    /**
     * Check whether this card matches on the given numbered card.
     *
     * @param   other
     *          The numbered card to match with.
     * @return  False if the given card is not an effective numbered
     *          card.
     *          | if (other == null)
     *          |   then result == false
     */
    public abstract boolean matchesOnNumbered(NumberedCard other);

    /**
     * Check whether this card matches on the given jack card.
     *
     * @param   other
     *          The jack card to match with.
     * @return  False if the given card is not an effective jack card.
     *          | if (other == null)
     *          |   then result == false
     */
    public abstract boolean matchesOnJack(JackCard other);

    /**
     * Check whether this card matches on the given joker card.
     *
     * @param	other
     *			The joker card to match with.
     * @return  True if the given card is an effective joker card;
     *          false otherwise.
     *          | result == (other != null)
     */
    public final boolean matchesOnJoker(JokerCard other) {
        return other != null;
    }
    
    /**
     * Check whether this card is equal to the given object.
     * 
     * @return  False if the given object is not effective.
     *          | if (other == null)
     *          |   then result == false
     * @return  False if the given object does not belong to the
     *          same concrete class as this object.
     *          | if (this.getClass() != other.getClass())
     *          |   then result == false
     */
    @Override
    public abstract boolean equals(Object other);

}