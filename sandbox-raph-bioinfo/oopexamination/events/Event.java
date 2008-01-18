/**
 * Package to deal with events
 */
package oopexamination.events;

import oopexamination.Agenda;
import oopexamination.Day;
import oopexamination.Person;
import oopexamination.entries.Entry;

/** 
 * Class to deal with events -- little or no implementation because I do the project alone
 * @author rsanged0
 *
 */
public class Event extends Entry 
{


	/**
	 * Full Constructor?
	 * @param agenda
	 * @param description
	 * @param entryDay
	 * @param person
	 */
	public Event(Agenda agenda, String description, Day entryDay, Person person) 
	{
		super(agenda, description, entryDay, person);
		if(isOneWordDescription());
		setDescriptionToOneWord() ;
	}

	/**
	 * Set Description to one Word
	 *Set to  pattern.split(description.trim())[1].trim() 
	 *The first word in description
	 */
	private void setDescriptionToOneWord() 
	{
		java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[/W]+");
		description= pattern.split(description.trim())[1].trim();
	}

	/** (non-Javadoc)
	 * @see oopexamination.entries.Entry#getFirstSlot()
	 */
	@Override
	public int getFirstSlot() 
	{
		return 1;
	}

	/**
	 * Check wheather description of event is oneword
	 * @return true if pattern.split(description.trim()).length==1;
	 */
	private boolean isOneWordDescription()
	{
		java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[/W]+");
		return pattern.split(description.trim()).length==1;
	}

	/** (non-Javadoc)
	 * @see oopexamination.entries.Entry#startsBefore(oopexamination.entries.Entry)
	 */
	@Override
	public boolean startsBefore(Entry entry) 
	{
		return false;
	}

    @Override
    public boolean canHaveAsEntry() {
        return true;
    }

}
