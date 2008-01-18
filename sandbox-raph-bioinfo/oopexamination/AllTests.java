package oopexamination;
import oopexamination.entries.AppointmentTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
/**
 * All oopexamination Tests suite
 * @author rsanged0
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( {AppointmentTest.class, AgendaTest.class ,SlotTest.class})

public class AllTests 
{
	
}
