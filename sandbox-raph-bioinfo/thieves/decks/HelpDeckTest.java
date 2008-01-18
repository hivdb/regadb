package thieves.decks;

import static org.junit.Assert.*;

import org.junit.*;

import thieves.cards.*;


public class HelpDeckTest {
    private HelpDeck helpDeck;
    private TargetDeck targetDeck;

    @Before
    public void setUpBeforeClass() throws Exception {
        helpDeck = new HelpDeck(new NumberedCard(6));
        targetDeck = new TargetDeck(10,new NumberedCard(7));
    }

    @Test
    public void constructor_LegalCase() {
        Card cards[] = { new JokerCard(), new NumberedCard(7), new JackCard() };
        HelpDeck theDeck = new HelpDeck(cards[0], cards[1], cards[2]);
        assertEquals(0, theDeck.getMinimumNbCards());
        assertEquals(3, theDeck.getMaximumNbCards());
        assertEquals(3, theDeck.getNbCards());
        assertSame(cards[0], theDeck.getCardAt(1));
        assertSame(cards[1], theDeck.getCardAt(2));
        assertSame(cards[2], theDeck.getCardAt(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_IllegalCard() throws Exception {
        new HelpDeck(new JokerCard(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_DuplicateCard() throws Exception {
        Card theCard = new JackCard();
        new HelpDeck(theCard, new JokerCard(), theCard);
    }
    
    @Test public void topCardMatchesOn_TrueCase() {
        assertTrue(helpDeck.topCardMatchesOn(targetDeck));
    }
    
    @Test public void topCardMatchesOn_EmptySourceDeck() {
        helpDeck.clear();
        assertFalse(helpDeck.topCardMatchesOn(targetDeck));
    }
    
    @Test public void topCardMatchesOn_NonEffectiveTargetDeck() {
        assertFalse(helpDeck.topCardMatchesOn(null));
    }   

    @Test public void topCardMatchesOn_FullTargetDeck() {
        targetDeck = new TargetDeck(1,new NumberedCard(7));
        assertFalse(helpDeck.topCardMatchesOn(targetDeck));
    }   

}
