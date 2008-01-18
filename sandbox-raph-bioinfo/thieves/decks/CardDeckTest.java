package thieves.decks;

import static org.junit.Assert.*;

import org.junit.*;

import thieves.cards.*;


public class CardDeckTest {

    private CardDeck theDeck;

    private static CardDeck emptyDeck, minimumDeck, fullDeck, mediumDeck;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        emptyDeck = new HelpDeck();
        minimumDeck = new TargetDeck(1, new JokerCard());
        fullDeck = new TargetDeck(1, new JokerCard());
        mediumDeck = new TargetDeck(20, new JokerCard());
        mediumDeck.pushCard(new JackCard());
    }

    @Before
    public void setUp() throws Exception {
        theDeck = new TargetDeck(20, new JokerCard());
        theDeck.pushCard(new JackCard());
    }

    @Test
    public void canHaveAsCapacity_NegativeMinimum() {
        assertFalse(theDeck.canHaveAsCapacity(-1, 5));
    }

    @Test
    public void canHaveAsCapacity_MinimumExceedsMaximum() {
        assertFalse(theDeck.canHaveAsCapacity(4, 3));
    }

    @Test
    public void setMaximumNbCards_SingleCase() {
        theDeck.setMaximumNbCards(20);
        assertEquals(20, theDeck.getMaximumNbCards());
    }

    @Test
    public void canHaveAsNbCards_TrueCase() {
        assertTrue(theDeck.canHaveAsNbCards(theDeck.getMinimumNbCards()));
    }

    @Test
    public void canHaveAsNbCards_NumberBelowMinimum() {
        assertFalse(theDeck.canHaveAsNbCards(theDeck.getMinimumNbCards() - 1));
    }

    @Test
    public void canHaveAsNbCards_NumberAboveMaximum() {
        if (theDeck.getMaximumNbCards() < Integer.MAX_VALUE)
            assertFalse(theDeck
                .canHaveAsNbCards(theDeck.getMaximumNbCards() + 1));
    }

    @Test
    public void hasMaximumSize_TrueCase() {
        assertTrue(fullDeck.hasMaximumSize());
    }

    @Test
    public void hasMaximumSize_FalseCase() {
        assertFalse(mediumDeck.hasMaximumSize());
    }

    @Test
    public void hasMinimumSize_TrueCase() {
        assertTrue(minimumDeck.hasMinimumSize());
    }

    @Test
    public void hasMinimumSize_FalseCase() {
        assertFalse(mediumDeck.hasMinimumSize());
    }

    @Test
    public void trimToSize_SingleCase() {
        theDeck.trimToSize();
        assertEquals(theDeck.getNbCards(), theDeck.getMaximumNbCards());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getCardAt_IndexTooLow() throws Exception {
        mediumDeck.getCardAt(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getCardAt_IndexTooHigh() throws Exception {
        mediumDeck.getCardAt(mediumDeck.getNbCards() + 1);
    }

    @Test
    public void canHaveAsCardAt_CardAtIndex() {
        assertTrue(mediumDeck.canHaveAsCardAt(mediumDeck.getCardAt(1), 1));
    }

    @Test
    public void canHaveAsCardAt_OtherCard() {
        assertTrue(mediumDeck.canHaveAsCardAt(new NumberedCard(2), 1));
    }

    @Test
    public void canHaveAsCardAt_CardAtOtherIndexInRange() {
        assertFalse(mediumDeck.canHaveAsCardAt(mediumDeck.getCardAt(1), 2));
    }

    @Test
    public void canHaveAsCardAt_CardAtOtherIndexOutOfRange() {
        assertFalse(mediumDeck.canHaveAsCardAt(mediumDeck.getCardAt(1), 3));
    }

    @Test
    public void canHaveAsCardAt_NonEffectiveCard() {
        assertFalse(mediumDeck.canHaveAsCardAt(null, 2));
    }

    @Test
    public void canHaveAsCardAt_IndexTooLow() {
        assertFalse(mediumDeck.canHaveAsCardAt(new NumberedCard(2), 0));
    }

    @Test
    public void canHaveAsCardAt_IndexTooHigh() {
        assertFalse(mediumDeck.canHaveAsCardAt(new NumberedCard(2), mediumDeck
            .getMaximumNbCards() + 1));
    }

    @Test
    public void canAcceptAsTopCard_TrueCase() {
        assertTrue(mediumDeck.canAcceptAsTopCard(new JokerCard()));
    }

    @Test
    public void canAcceptAsTopCard_IllegalCard() {
        assertFalse(mediumDeck.canAcceptAsTopCard(mediumDeck.getCardAtTop()));
    }

    @Test
    public void canAcceptAsTopCard_FullDeck() {
        assertFalse(fullDeck.canAcceptAsTopCard(new JackCard()));
    }

    @Test
    public void getCardAtTop_LegalCase() {
        Card topCard = mediumDeck.getCardAt(2);
        assertSame(topCard, mediumDeck.getCardAtTop());
    }

    @Test(expected = IllegalStateException.class)
    public void getCardAtTop_IndexTooHigh() throws Exception {
        emptyDeck.getCardAtTop();
    }

    @Test
    public void hasAsCard_TrueCase() {
        Card theCard = mediumDeck.getCardAt(2);
        assertTrue(mediumDeck.hasAsCard(theCard));
    }

    @Test
    public void hasAsCard_FalseCase() {
        assertFalse(mediumDeck.hasAsCard(new JackCard()));
    }

    @Test
    public void hasAsCard_NonEffectiveCard() {
        assertFalse(mediumDeck.hasAsCard(null));
    }

    @Test public void getAllCards_SingleCase() {
        Card[] theCards = mediumDeck.getAllCards();
        assertNotNull(theCards);
        assertEquals(mediumDeck.getNbCards(),theCards.length);
        for (int i=1; i<=mediumDeck.getNbCards(); i++)
            assertSame(mediumDeck.getCardAt(i),theCards[i-1]);
    }

    @Test public void pushCard_SingleCase() {
        Card newCard = new NumberedCard(7);
        int oldNbCards = theDeck.getNbCards();
        assertTrue(theDeck.getNbCards()<theDeck.getMaximumNbCards());
        theDeck.pushCard(newCard);
        assertEquals(oldNbCards+1,theDeck.getNbCards());
        assertSame(theDeck.getCardAtTop(),newCard);
    }

    @Test public void popCard_SingleCase() {
        int oldNbCards = theDeck.getNbCards();
        assertTrue(theDeck.getNbCards()>theDeck.getMinimumNbCards());
        theDeck.popCard();
        assertEquals(oldNbCards-1,theDeck.getNbCards());
    }

    @Test public void clear_SingleCase() {
        CardDeck theDeck = new SourceDeck(new JokerCard(),new JackCard());
        theDeck.clear();
        assertEquals(0,theDeck.getNbCards());
    }

    @Test public void toString_SingleCase() {
        assertEquals("TargetDeck: [Joker,Jack]",theDeck.toString());
    }

}
