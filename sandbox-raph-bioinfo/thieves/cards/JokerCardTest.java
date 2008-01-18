package thieves.cards;


import static org.junit.Assert.*;

import org.junit.*;


public class JokerCardTest {
    
    private static JokerCard theJokerCard;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        theJokerCard = new JokerCard();
    }
    
    @Test public void matchesOn_TrueCase() {
        Card otherCard = new NumberedCard(7);
        assertTrue(theJokerCard.matchesOn(otherCard));
    }
    
    @Test public void matchesOn_NonEffectiveCard() {
        assertFalse(theJokerCard.matchesOn(null));
    }
    
    @Test public void matchesOnNumbered_TrueCase() {
        assertTrue(theJokerCard.matchesOnNumbered(new NumberedCard(8)));
    }
    
    @Test public void matchesOnNumbered_NonEffectiveCard() {
        assertFalse(theJokerCard.matchesOnNumbered(null));
    }
    
    @Test public void matchesOnJack_TrueCase() {
        assertTrue(theJokerCard.matchesOn(new JackCard()));
    }
    
    @Test public void matchesOnJack_NonEffectiveCard() {
        assertFalse(theJokerCard.matchesOn(null));
    }
    
    @Test public void toString_SingleCase() {
        assertEquals("Joker", theJokerCard.toString());
    }
    
    @Test public void equals_EqualCards() {
        assertTrue(theJokerCard.equals(new JokerCard()));
    }
    
    @Test public void equals_NonEffectiveCard() {
        assertFalse(theJokerCard.equals(null));
    }
    
    @Test public void equals_NonNumberedCards() {
        assertFalse(theJokerCard.equals(new NumberedCard(5)));
    }

    @Test public void hashCode_SingleCase() {
        assertEquals(0, theJokerCard.hashCode());
    }

}
