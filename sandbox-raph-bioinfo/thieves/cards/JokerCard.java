package thieves.cards;


/**
 * A class of joker cards used in a game of Thieves.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public class JokerCard extends Card {

    /**
     * Initialize this new joker card.
     */
    public JokerCard() {
    }

    /**
     * Check whether this joker card matches on the given card.
     *
     * @return  True if the given card is an effective card;
     *          false otherwise.
     *          | result == (other != null)
     */
    @Override
    public boolean matchesOn(Card other) {
        return other != null;
    }

    /**
     * Check whether this joker card matches on the given numbered card.
     *
     * @return  True if the given card is an effective numbered card;
     *          false otherwise.
     *          | result == (other != null)
     */
    @Override
    public boolean matchesOnNumbered(NumberedCard other) {
        return other != null;
    }

    /**
     * Check whether this joker matches on the given jack card.
     *
     * @return  True if the given card is an effective jack card;
     *          false otherwise.
     *          | result == (other != null)
     */
    @Override
    public boolean matchesOnJack(JackCard other) {
        return other != null;
    }
    
    /**
     * Return a textual representation of this joker card.
     * 
     * @return  The string "Joker".
     *          | result.equals("Joker")
     */
    @Override
    public String toString() {
        return "Joker";
    }
    
    /**
     * Check whether this joker card is equal to the given object.
     * 
     * @return  True if the given object is an effective joker card;
     *          false otherwise.
     *          | result == (other instanceof JokerCard)
     */
    @Override
    public boolean equals(Object other) {
        return (other instanceof JokerCard);
    }
    
    /**
     * Return the hash code of this joker card.
     * 
     * @return  Always 0.
     *          | result == 0
     */
    @Override
    public int hashCode() {
        return 0;
    }

}