package thieves.cards;


import static org.junit.Assert.*;

import org.junit.*;


public class JackCardTest {
    
    private static JackCard theJackCard;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        theJackCard = new JackCard();
    }

    @Test public void matchesOn_TrueCase() {
        assertTrue(theJackCard.matchesOn(new NumberedCard(10)));
    }
    
    @Test public void matchesOn_NonEffectiveCard() {
        assertFalse(theJackCard.matchesOn(null));
    }
    
    @Test public void matchesOn_NonMatchingCard() {
        assertFalse(theJackCard.matchesOn(new NumberedCard(5)));
    }
    
    @Test public void matchesOnNumbered_TrueCase() {
        assertTrue(theJackCard.matchesOnNumbered(new NumberedCard(10)));
    }
    
    @Test public void matchesOnNumbered_NonEffectiveCard() {
        assertFalse(theJackCard.matchesOnNumbered(null));
    }
    
    @Test public void matchesOnNumbered_NonMatchingCard() {
        assertFalse(theJackCard.matchesOnNumbered(new NumberedCard(7)));
    }
    
    @Test public void matchesOnJack_SingleCase() {
        assertFalse(theJackCard.matchesOnJack(new JackCard()));
    }

    @Test public void toString_SingleCase() {
        assertEquals("Jack", theJackCard.toString());
    }
    
    @Test public void equals_EqualCards() {
        assertTrue(theJackCard.equals(new JackCard()));
    }
    
    @Test public void equals_NonEffectiveCard() {
        assertFalse(theJackCard.equals(null));
    }
    
    @Test public void equals_NonJackCard() {
        assertFalse(theJackCard.equals(new JokerCard()));
    }

    @Test public void hashCodeSingleCase() {
        assertEquals(11, theJackCard.hashCode());
    }

}
