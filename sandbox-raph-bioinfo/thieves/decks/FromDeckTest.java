package thieves.decks;

import static org.junit.Assert.*;

import org.junit.*;

import thieves.cards.*;


public class FromDeckTest {

    private FromDeck theFromDeck;

    private TargetDeck theTargetDeck;

    @Before
    public void setUp() throws Exception {
        theFromDeck = new HelpDeck(new JackCard(), new JokerCard());
        theTargetDeck = new TargetDeck(10, new NumberedCard(7));
    }

    @Test
    public void getMinimumNbCards_SingleCase() {
        assertEquals(0, theFromDeck.getMinimumNbCards());
    }

    @Test
    public void canHaveAsCapacity_TrueCase() {
        assertTrue(theFromDeck.canHaveAsCapacity(0, 0));
    }

    @Test
    public void canHaveAsCapacity_IllegalMinimum() {
        assertFalse(theFromDeck.canHaveAsCapacity(2, 12));
    }

    @Test
    public void canHaveAsCapacity_IllegalMaximum() {
        assertFalse(theFromDeck.canHaveAsCapacity(0, -1));
    }

    @Test
    public void topCardMatchesOn_EmptyFromDeck() {
        theFromDeck.clear();
        assertFalse(theFromDeck.topCardMatchesOn(theTargetDeck));
    }

    @Test
    public void topCardMatchesOn_NonEffectiveTargetDeck() {
        assertFalse(theFromDeck.topCardMatchesOn(null));
    }

    @Test
    public void topCardMatchesOn_TargetDeck() {
        theTargetDeck = new TargetDeck(1, new NumberedCard(7));
        assertFalse(theFromDeck.topCardMatchesOn(theTargetDeck));
    }

    @Test
    public void moveTop_LegalCase() {
        Card topCard = theFromDeck.getCardAtTop();
        int nbFromCards = theFromDeck.getNbCards();
        int nbTargetCards = theTargetDeck.getNbCards();
        theFromDeck.moveTop(theTargetDeck);
        assertEquals(nbFromCards - 1, theFromDeck.getNbCards());
        assertEquals(nbTargetCards + 1, theTargetDeck.getNbCards());
        assertSame(topCard, theTargetDeck.getCardAtTop());
    }

    @Test(expected = IllegalStateException.class)
    public void moveTop_IllegalCase() throws Exception {
        theTargetDeck.trimToSize();
        theFromDeck.moveTop(theTargetDeck);
    }

}
