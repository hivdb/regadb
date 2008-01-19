package oopexamination.entries;

import oopexamination.Agenda;
import oopexamination.Day;
import oopexamination.Person;
import oopexamination.Slot;

/**
 * Abstract entry for an agenda
 * base for events,meetings,personal entries ...
 * @author rsanged0
 *
 */
public abstract class Entry
{
	protected  Agenda agenda; //
	protected String description; //Multiple words you need to validate that for event this is single word 
	protected Day entryDay;
	protected Person person;
	
	/**
	 * Full cronstructor to initialize agenda and the entry day
	 * @param agenda The agenda to which entry is made
	 * @param description Description of the entry
	 * @param entryDay The day of the entry
	 * @param person The owner of entry
	 */
	public Entry(final Agenda agenda, String description, Day entryDay, Person person) 
	{
		this.agenda = agenda;
		this.description = description;
		this.entryDay = entryDay;
		this.person = person;
	}
	
    public abstract boolean canHaveAsEntry() ;
	/**
	 * Get agenda for this entry
	 * @return the agenda
	 */
	public Agenda getAgenda()
	{
		return agenda;
	}
	public long getDay() 
	{

		return getEntryDay().getDay();
	}
	/**
	 * Get entry description
	 */
	public  String getDescription()
	{
		return description;
	}

	/**
	 * Return the Entry day
	 * @return the entryDay of type Day
	 */
	public Day getEntryDay() 
	{
		return entryDay;
	}


	public abstract int getFirstSlot();
	/**
	 * Get the person owning the appointment
	 * @return the person
	 */
	public Person getPerson() 
	{
		return person;
	}
	/** 
	 * Set description of an entry
	 * @param description
	 */
	public  void setDescription(String description)
	{
		this.description=description;
	}
	/**
	 * Set Entry day to specified Entryday (Day)
	 * @param entryDay the entryDay to set
	 */
	public void setEntryDay(Day entryDay) 
	{
		this.entryDay = entryDay;
	}
	/**
	 * Set owner of appointment
	 * @param person the person to set
	 */
	public void setPerson(Person person) 
	{
		this.person = person;
	}
	public abstract boolean startsBefore(Entry entry);

	/**
	 * @param agenda the agenda to set
	 */
	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}

	public abstract Slot  getSlot();
	
}
