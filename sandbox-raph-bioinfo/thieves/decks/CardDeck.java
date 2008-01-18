package thieves.decks;

import java.util.Stack;

import thieves.cards.*;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class of decks containing cards.
 *
 * @invar   Each card deck must can have its capacity as its
 *          capacity.
 *          | canHaveAsCapacity(getMinimumNbCards(),getMaximumNbCards())
 * @invar   Each card deck must can have its number of cards as its
 *          number of cards.
 *          | canHaveAsNbCards(getNbCards())
 * @invar   Each card deck must can have each of its cards at their
 *          index as a card at that index.
 *          | for each I in 1..getNbCards():
 *          |   canHaveAsCard(getCardAt(I),I)
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public abstract class CardDeck {

    /**
     * Initialize this new empty card deck with given capacity.
     *
     * @param   minNbCards
     *          The minimum number of cards ever on this new card deck.
     * @param   maxNbCards
     *          The maximum number of cards ever on this new card deck.
     * @post    The maximum number of cards for this new card deck is equal
     *          to the given maximum number of cards.
     *          | new.getMaximumNbCards() == maxNbCards
     * @throws  IllegalArgumentException
     *          This new card deck can not have a capacity ranging
     *          between the given minimum and maximum number of cards.
     *          | ! (canHaveAsCapacity(minNbCards,maxNbCards))
     */
    @Model
    protected CardDeck(int minNbCards, int maxNbCards)
            throws IllegalArgumentException {
        if (!canHaveAsCapacity(minNbCards, maxNbCards))
            throw new IllegalArgumentException("Illegal capacity!");
        setMaximumNbCards(maxNbCards);
        cards.ensureCapacity(maxNbCards);
    }

    /**
     * Return the maximum number of cards that can be put on this card
     * deck.
     */
    @Raw
    public int getMaximumNbCards() {
        return this.maximumNbCards;
    }

    /**
     * Return the minimum number of cards that must be on this card deck.
     */
    @Raw
    public abstract int getMinimumNbCards();

    /**
     * Check whether this card deck can have the given capacity
     * as its capacity.
     * 
     * @param   minNbCards
     *          The minimum number of cards for the capacity to check.
     * @param   maxNbCards
     *          The maximum number of cards for the capacity to check.
     * @return  False if the given minimum number of cards is negative,
     *          or if that minimum exceeds the given maximum number of
     *          cards.
     *          | if (minNbCards < 0) || (minNbCards > maxNbCards)
     *          |   then result == false
     */
    @Raw
    public boolean canHaveAsCapacity(int minNbCards, int maxNbCards) {
        return (minNbCards >= 0) && (minNbCards <= maxNbCards);
    }

    /**
     * Set the maximum number of cards for this card deck to the
     * given maximum number of cards.
     * 
     * @param   maximumNbCards
     *          The new maximum number of cards for this card deck.
     * @pre     This card deck must can have the given maximum number
     *          of cards as part of its capacity.
     *          | canHaveAsCapacity(getMinimumNbCards(),maximumNbCards)
     * @post    The maximum number of cards for this card deck is equal
     *          to the given maximum number of cards.
     *          | new.getMaximumNbCards() == maximumNbCards 
     */
    @Raw
    protected void setMaximumNbCards(int maximumNbCards) {
        this.maximumNbCards = maximumNbCards;
    }

    /**
     * Variable registering the maximum number of cards for this card deck.
     * 
     * @invar   This card deck can have the registered maximum number of
     *          cards as part of its capacity.
     *          | canHaveAsCapacity(getMinNbCards(),maximumNbCards)
     */
    private int maximumNbCards;

    /**
     * Return the number of cards on this card deck.
     */
    @Raw
    public int getNbCards() {
        return cards.size();
    }

    /**
     * Check whether this card deck can have the given number
     * of cards as its number of cards.
     *
     * @param   number
     *          The number to check.
     * @return  True if the given number is not below the minimum number
     *          of cards for this card deck, and does not exceed the
     *          maximum number of cards for this card deck;
     *          false otherwise.
     *          | result ==
     *          |   (number >= getMinimumNbCards()) &&
     *          |   (number <= getMaximumNbCards())
     */
    @Raw
    public boolean canHaveAsNbCards(int number) {
        return (number >= getMinimumNbCards())
            && (number <= getMaximumNbCards());
    }

    /**
     * Check whether this card deck has reached its maximum number
     * of cards.
     *
     * @return  True if the number of cards on this card deck is equal to
     *          its maximum number of cards; false otherwise.
     *          | result == (getNbCards() == getMaximumNbCards())
     */
    @Raw
    public boolean hasMaximumSize() {
        return getNbCards() == getMaximumNbCards();
    }

    /**
     * Check whether this card deck has reached its minimum number of
     * cards.
     *
     * @return  True if the number of cards on this card deck is equal to
     *          its minimum number of cards; false otherwise.
     *          | result == (getNbCards() == getMinimumNbCards())
     */
    @Raw
    public boolean hasMinimumSize() {
        return getNbCards() == getMinimumNbCards();
    }

    /**
     * Trim this card deck to its current size.
     * 
     * @post   The maximum number of cards for this card deck is equal
     * 		   to its number of cards.
     * 		   | new.getMaximumNbCards() == getNbCards()
     */
    public void trimToSize() {
        setMaximumNbCards(getNbCards());
        cards.trimToSize();
    }

    /**
     * Return the card at the given index in this card deck.
     *
     * @param   index
     *          The index of the card to be returned.
     * @throws  IndexOutOfBoundsException [must]
     *          The given index is below 1 or exceeds the number
     *          of cards on this card deck.
     *          | (index < 1) || (index > getNbCards())
     */
    @Raw
    public Card getCardAt(int index) throws IndexOutOfBoundsException {
        return cards.get(index - 1);
    }

    /**
     * Check whether this card deck can have the given card as one
     * of its cards.
     *
     * @param   card
     *          The card to be checked.
     * @param   index
     *          The index to be checked.
     * @return  False if the given card is not effective.
     *          | if (card == null)
     *          |   then result == false
     * @return  Otherwise, false if the given index is below the minimum
     *          number of cards for this card deck, or above the maximum
     *          number of cards for this card deck.
     *          | else if ( (index < getMinimumNbCards()) ||
     *          |           (index > getMaximumNbCards()) )
     *          |   then result == false
     * @return  Otherwise, true if this card deck does not have this
     *          card, or if the given index does not exceed the number of
     *          cards on this card deck and this card deck has the given
     *          card at the given index; false otherwise.
     *          | else result == 
     *          |        (! hasAsCard(card)) ||
     *          |        ( (index <= getNbCards()) && (getCardAt(index) == card) )
     */
    public boolean canHaveAsCardAt(Card card, int index) {
        return (card != null)
            && (index >= getMinimumNbCards())
            && (index <= getMaximumNbCards())
            && ((!hasAsCard(card)) || ((index <= getNbCards()) && (getCardAt(index) == card)));
    }

    /**
     * Check whether this card deck can accept the given card
     * as its new top card.
     * 
     * @param   card
     *          The card to check.
     * @return  True if this card deck has not reached its maximum size and
     *          if this card deck can have the given card as its
     *          card at an index 1 above the index of its current top
     *          card; false otherwise.
     *          | result ==
     *          |   (! hasMaximumSize()) &&
     *          |   canHaveAsCardAt(card,getNbCards()+1)
     */
    @Raw
    public boolean canAcceptAsTopCard(Card card) {
        return (!hasMaximumSize()) && canHaveAsCardAt(card, getNbCards() + 1);
    }

    /**
     * Return the card on top of this card deck.
     *
     * @return  The card on this card deck at the highest index.
     *          | result == getCardAt(getNbCards())
     * @throws  IllegalStateException
     *          This card deck is empty.
     *          | getNbCards() == 0
     */
    public Card getCardAtTop() throws IllegalStateException {
        if (getNbCards() == 0)
            throw new IllegalStateException("No top card for an empty deck!");
        return getCardAt(getNbCards());
    }

    /**
     * Check whether the given card is on this card deck.
     *
     * @param   card
     *          The card to be checked.
     * @return  True if the given card is loaded at some index on
     *          this card deck; false otherwise.
     *          | result ==
     *          |   (for some I in 1..getNbCards():
     *          |      getCardAt(I) == card) 
     */
    @Raw
    public boolean hasAsCard(Card card) {
        for (int pos = 1; pos <= getNbCards(); pos++)
            if (getCardAt(pos) == card)
                return true;
        return false;
        // The method contains from Stack cannot be used, because
        // it compares elements using equals and not using ==.
    }

    /**
     * Return all the cards loaded on this card deck, with the card on top
     * of this card deck at the end of the resulting array.
     *
     * @return  The resulting array is effective.
     *          | result != null
     * @return  The length of the resulting array is equal to
     *          the number of cards in this card deck.
     *          | result.length == getNbCards()
     * @return  Successive elements in the resulting array are the same
     *          as the cards at corresponding positions in this card deck.
     *          | for each I in 1..getNbCards():
     *          |   result[I-1] == getCardAt(I)
     */
    public Card[] getAllCards() {
        Card[] result = new Card[getNbCards()];
        cards.toArray(result);
        return result;
    }

    /**
     * Push the given card on top of this card deck.
     *
     * @param   card
     *          The card to be pushed.
     * @pre     This card deck must accept the given card as its
     *          top card.
     *          | canAcceptAsTopCard(card)
     * @post    The number of cards on this card deck is incremented
     *          by 1.
     *          | new.getNbCards() == getNbCards() + 1
     * @post    The card on top of this card deck is the same as the
     *          given card.
     *          | new.getCardAtTop() == card
     */
    @Raw
    protected void pushCard(Card card) {
        cards.push(card);
    }

    /**
     * Remove the card on top of this card deck.
     *
     * @pre     This card deck has not reached its minimal size.
     *          | ! hasMinimumSize()
     * @post    The number of cards on this card deck is decremented
     *          by 1.
     *          | new.getNbCards() == getNbCards() � 1
     */
    @Raw
    protected void popCard() {
        cards.pop();
    }

    /**
     * Remove all cards from this card deck.
     * 
     * @pre     The minimum number of cards for this card deck must
     *          be 0.
     *          | getMinimumNbCards() == 0
     * @post    No cards are loaded any more on this card deck.
     *          | new.getNbCards() == 0
     */
    protected void clear() {
        cards.clear();
    }

    /**
     * Variable referencing a stack containing all the cards in this
     * card deck.
     * 
     * @invar   The stack of cards is effective.
     *          | cards != null
     * @invar   This card deck can have each of the cards in the
     *          stack at its corresponding position.
     *          | for each I in 0..cards.size()-1:
     *          |   canHaveAsCardAt(cards.get(I),I+1)
     * @note    Because the variable is declared final, we assume
     *          in all methods - even with @Raw qualifications- that
     *          the variable references an effective stack.
     */
    private final Stack<Card> cards = new Stack<Card>();

    /**
     * Return a textual representation of this card deck.
     * 
     * @return  An effective string starting with the simple name of the class
     *          to which this card deck effectively belongs, followed by
     *          the textual representation of each card on this card
     *          deck, separated by comma's and enclosed in square brackets.
     *          | (result != null) &&
     *          | (result.matches(getClass().getSimpleName() + ": [.*]") &&
     *          | (for each I in 1..getNbCards():
     *          |    result.matches(".*[.*"+getCardAt(I)+".*]")
     * @note    The formal specification does not express that the cards
     *          must be displayed in order.
     */
    @Override
    public String toString() {
        String result = this.getClass().getSimpleName() + ": [";
        if (getNbCards() > 0)
            result += getCardAt(1);
        for (int i = 2; i <= getNbCards(); i++)
            result += "," + getCardAt(i);
        return result + "]";
    }

}