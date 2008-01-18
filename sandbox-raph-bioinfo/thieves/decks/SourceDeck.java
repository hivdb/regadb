package thieves.decks;

import thieves.cards.*;

/**
 * A class of source stacks containing cards.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public class SourceDeck extends FromDeck {

	/**
	 * Initialize this new source deck with given cards.
	 *
	 * @param   cards
	 *          The cards for this new source deck.
	 * @effect  This new source deck is initialized as a new from
	 *          deck with the given cards.
	 *          | super(cards)
	 */
	public SourceDeck(Card... cards) throws IllegalArgumentException {
		super(cards);
	}

	/**
	 * Check whether the card on top of this source deck can be added
	 * on top of the given target deck.
	 *
	 * @return  True if this source deck has not reached its minimal size,
	 *          if the the given target deck is effective, if the given target
	 *          deck can accept the card on top of this source deck as its own top,
	 *          and if the card on top of this source deck matches on the card
	 *          on top of the given target deck; false otherwise.
	 *          | result ==
	 *          |   (! hasMinimumSize()) &&
	 *          |   (target != null) &&
	 *          |   (target.canAcceptAsTopCard(getCardAtTop())) &&
	 *          |   (getCardAtTop().matchesOn(target.getCardAtTop()))
	 */
	@Override public boolean topCardMatchesOn(TargetDeck target) {
		return (!hasMinimumSize()) && (target != null)
			&& (target.canAcceptAsTopCard(getCardAtTop()))
			&& (getCardAtTop().matchesOn(target.getCardAtTop()));
	}

}