package thieves.decks;

import thieves.cards.*;

/**
 * A class of help decks containing cards.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public class HelpDeck extends FromDeck {

	/**
	 * Initialize this new help deck with given cards.
	 *
	 * @param   cards
	 *          The cards for this new help deck.
	 * @effect  This new help deck is initialized as a new from
	 *          deck with the given cards.
	 *          | super(cards)
	 */
	public HelpDeck(Card... cards) throws IllegalArgumentException {
		super(cards);
	}

	/**
	 * Check whether the card on top of this help deck can be added on top
	 * of the given target deck.
	 *
	 * @return  True if this help deck has not reached its minimal size,
	 *          if the given target deck is effective, and if the given target
	 *          can accept the card on top of this help deck as its own top;
	 *          false otherwise.
	 *			| result ==
	 *			|	(! hasMinimumSize()) &&
	 *			|	(target != null) &&
	 *			|	(target.canAcceptAsTopCard(getCardAtTop()))
	 */
	@Override public boolean topCardMatchesOn(TargetDeck target) {
		return (!hasMinimumSize()) && (target != null)
			&& (target.canAcceptAsTopCard(getCardAtTop()));
	}

}