package thieves.decks;

import static org.junit.Assert.*;

import org.junit.*;

import thieves.cards.Card;
import thieves.cards.JackCard;
import thieves.cards.JokerCard;
import thieves.cards.NumberedCard;


public class SourceDeckTest {
    
    private SourceDeck sourceDeck;
    private TargetDeck targetDeck;

    @Before
    public void setUp() throws Exception {
        sourceDeck = new SourceDeck(new NumberedCard(6));
        targetDeck = new TargetDeck(10,new NumberedCard(7));
    }

    @Test
    public void constructor_LegalCase() {
        Card cards[] = { new JokerCard(), new NumberedCard(7), new JackCard() };
        SourceDeck theDeck = new SourceDeck(cards[0], cards[1], cards[2]);
        assertEquals(0, theDeck.getMinimumNbCards());
        assertEquals(3, theDeck.getMaximumNbCards());
        assertEquals(3, theDeck.getNbCards());
        assertSame(cards[0], theDeck.getCardAt(1));
        assertSame(cards[1], theDeck.getCardAt(2));
        assertSame(cards[2], theDeck.getCardAt(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_IllegalCard() throws Exception {
        new SourceDeck(new JokerCard(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_DuplicateCard() throws Exception {
        Card theCard = new JackCard();
        new SourceDeck(theCard, new JokerCard(), theCard);
    }
    
    @Test public void topCardMatchesOn_TrueCase() {
        assertTrue(sourceDeck.topCardMatchesOn(targetDeck));
    }
    
    @Test public void topCardMatchesOn_EmptySourceDeck() {
        sourceDeck.clear();
        assertFalse(sourceDeck.topCardMatchesOn(targetDeck));
    }
    
    @Test public void topCardMatchesOn_NonEffectiveTargetDeck() {
        assertFalse(sourceDeck.topCardMatchesOn(null));
    }   

    @Test public void topCardMatchesOn_FullTargetDeck() {
        targetDeck = new TargetDeck(1,new NumberedCard(7));
        assertFalse(sourceDeck.topCardMatchesOn(targetDeck));
    }   
    
    @Test public void topCardMatchesOn_NonMatchingCard() {
        sourceDeck.pushCard(new NumberedCard(9));
        assertFalse(sourceDeck.topCardMatchesOn(targetDeck));
    }   

}
