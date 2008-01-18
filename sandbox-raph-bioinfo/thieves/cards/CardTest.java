package thieves.cards;


import static org.junit.Assert.*;

import org.junit.*;


public class CardTest {
    
    private static Card theCard;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        theCard = new NumberedCard(6);
    }

    @Before
    public void setUp() throws Exception {
    }
    
    @Test public void matchesOn_NonEffectiveCard() {
        Card otherCard = null;
        assertFalse(theCard.matchesOn(otherCard));
    }
    
    @Test public void matchesOnNumbered_NonEffectiveCard() {
        NumberedCard otherCard = null;
        assertFalse(theCard.matchesOn(otherCard));
    }
    
    @Test public void matchesOnJack_NonEffectiveCard() {
        JackCard otherCard = null;
        assertFalse(theCard.matchesOnJack(otherCard));
    }
    
    @Test public void matchesOnJoker_EffectiveCard() {
        JokerCard otherCard = new JokerCard();
        assertTrue(theCard.matchesOnJoker(otherCard));
    }
    
    @Test public void matchesOnJoker_NonEffectiveCard() {
        JokerCard otherCard = null;
        assertFalse(theCard.matchesOnJoker(otherCard));
    }
    
    @Test public void equals_NonEffectiveObject() {
        assertFalse(theCard.equals(null));
    }
    
    @Test public void equals_OtherKindOfObject() {
        assertFalse(theCard.equals(new Integer(3)));
    }

}
