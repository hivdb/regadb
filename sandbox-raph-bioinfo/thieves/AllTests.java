package thieves;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import thieves.cards.*;
import thieves.decks.*;

@RunWith(Suite.class)
@Suite.SuiteClasses( { CardDeckTest.class, TargetDeckTest.class,
    FromDeckTest.class, HelpDeckTest.class, SourceDeckTest.class,
    CardTest.class, JokerCardTest.class, JackCardTest.class, NumberedCardTest.class })
public class AllTests {
}