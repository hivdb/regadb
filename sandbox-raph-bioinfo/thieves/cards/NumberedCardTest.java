package thieves.cards;

import static org.junit.Assert.*;

import org.junit.*;

public class NumberedCardTest {

    private static NumberedCard theCardWithNumber7;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        theCardWithNumber7 = new NumberedCard(7);
    }

    @Test
    public void isValidValue_LegalValue() {
        assertTrue(NumberedCard.isValidValue(7));
    }

    @Test
    public void isValidValue_NonPositiveValue() {
        assertFalse(NumberedCard.isValidValue(0));
    }

    @Test
    public void isValidValue_ValueAboveHighestValue() {
        assertFalse(NumberedCard.isValidValue(11));
    }

    @Test
    public void constructor_LegalCase() {
        NumberedCard theCard = new NumberedCard(7);
        assertEquals(7, theCard.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_IllegalCase() throws Exception {
        new NumberedCard(0);
    }

    @Test
    public void matchesOn_TrueCase() {
        assertTrue(theCardWithNumber7.matchesOn(new NumberedCard(theCardWithNumber7.getValue() + 1)));
    }

    @Test
    public void matchesOn_NonEffectiveCard() {
        assertFalse(theCardWithNumber7.matchesOn(null));
    }

    @Test
    public void matchesOn_NonMatchingCard() {
        assertFalse(theCardWithNumber7.matchesOn(new JackCard()));
    }

    @Test
    public void matchesOnNumbered_TrueCase() {
        assertTrue(theCardWithNumber7.matchesOnNumbered(new NumberedCard(theCardWithNumber7
            .getValue() + 1)));
    }

    @Test
    public void matchesOnNumbered_NonEffectiveCard() {
        assertFalse(theCardWithNumber7.matchesOnNumbered(null));
    }

    @Test
    public void matchesOnNumbered_NonMatchingCard() {
        assertFalse(theCardWithNumber7.matchesOnNumbered(new NumberedCard(theCardWithNumber7
            .getValue())));
    }

    @Test
    public void matchesOnJack_TrueCase() {
        assertTrue(new NumberedCard(10).matchesOnJack(new JackCard()));
    }

    @Test
    public void matchesOnJack_NonEffectiveCard() {
        assertFalse(theCardWithNumber7.matchesOnJack(null));
    }

    @Test
    public void matchesOnJack_NonMatchingCard() {
        assertFalse(theCardWithNumber7.matchesOnJack(new JackCard()));
    }

    @Test
    public void toString_SingleCase() {
        assertEquals("Card: 7", theCardWithNumber7.toString());
    }

    @Test
    public void equals_EqualCards() {
        assertTrue(theCardWithNumber7.equals(new NumberedCard(7)));
    }

    @Test
    public void equals_NonEqualCards() {
        assertFalse(theCardWithNumber7.equals(new NumberedCard(8)));
    }

    @Test
    public void equals_NonEffectiveCard() {
        assertFalse(theCardWithNumber7.equals(null));
    }

    @Test
    public void equals_NonNumberedCards() {
        assertFalse(theCardWithNumber7.equals(new JackCard()));
    }

    @Test
    public void hashCode_SingleCase() {
        assertEquals(7, theCardWithNumber7.hashCode());
    }

}
