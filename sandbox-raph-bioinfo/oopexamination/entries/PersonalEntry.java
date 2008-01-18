package oopexamination.entries;

import oopexamination.Agenda;
import oopexamination.Day;
import oopexamination.Person;
import oopexamination.Slot;

/**
 * Class PersonalEntry to deal with appointment where the second person has no agenda
 * @author rsanged0
 *
 */
public class PersonalEntry  extends Appointment
{

	/**
	 * Full PersonalEntry constructor
	 * @param agenda
	 * @param description
	 * @param entryDay
	 * @param person
	 * @param slot
	 */
	public PersonalEntry(Agenda agenda, String description, 
			Day entryDay, Person person, Slot slot)
	{
		super(agenda, description, entryDay, person, slot);
	}
	

	
}
