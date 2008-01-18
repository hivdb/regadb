package oopexamination.entries;

import java.util.Iterator;
import java.util.Set;

import oopexamination.Agenda;
import oopexamination.Day;
import oopexamination.Person;
import oopexamination.Slot;
import oopexamination.exceptions.AgendaOwnerEntryOwnerMismatchException;


/**
 * Meeting between two holders of agenda
 * @author rsanged0
 *
 */
public class Meeting  extends Appointment
{
	protected String notes;
	protected Person otherPerson;


	/**
	 * Restricted constructor
	 * @param agenda The agenda to which entry is made
	 * @param description Description of the entry
	 * @param entryDay The day of the entry
	 * @param person The owner of entry
	 * @param slot The slots of meeting
	 */
	public Meeting(Agenda agenda, String description, Day entryDay, 
			Person person, Slot slot,Agenda otherPersonAgenda) 
	{
		super(agenda, description, entryDay, person, slot);
//		getPerson().addAgenda(agenda); done in Appointment
	}

	/**
	 * Full constructor
	 *  @param agenda The agenda to which entry is made
	 * @param description Description of the entry
	 * @param entryDay The day of the entry
	 * @param person The owner of entry
	 * @param slot The slots of meeting
	 * @param notes Meeting notes
	 * @param otherPerson otherperson in this meeting
	 */
	public Meeting(Agenda agenda, String description, 
			Day entryDay, Person person, Slot slot, String notes, Person otherPerson, Agenda otherPersonAgenda)
	{
		super(agenda, description, entryDay, person, slot);
		this.notes = notes;
		this.otherPerson = otherPerson; 
		try {
			registerOtherPersonMeeting(otherPerson,otherPersonAgenda);
		} catch (AgendaOwnerEntryOwnerMismatchException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Copy constructor for a meeting
	 * @param meeting
	 */
	public Meeting(Meeting meeting) 
	{
		super(meeting.agenda, meeting.description,  meeting.entryDay,  meeting.person, meeting.slot);
		this.agenda=meeting.agenda;
		this.description=meeting.description;
		this.entryDay=meeting.entryDay;
		this.person=meeting.person;
		this.slot=meeting.slot;
		this.notes=meeting.notes;
		this.otherPerson=meeting.otherPerson;
		
	
	}

	//require that the other person has an agenda
	private void registerOtherPersonMeeting(Person otherPerson,Agenda otherPersonAgenda)  throws AgendaOwnerEntryOwnerMismatchException
	{
		Meeting tempMeeting=new Meeting(this); //copy the meeting
		tempMeeting.setPerson(otherPerson); //set unique parameters
		tempMeeting.setAgenda(otherPersonAgenda);
		tempMeeting.setDescription("Meeting with "+person.getName());
		try {
			otherPersonAgenda.addEntry(tempMeeting); // a bit confusing becausse it is called twice
		} catch (AgendaOwnerEntryOwnerMismatchException e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * Extend Meeting notes with specified string
	 * @param notes the notes to set
	 */
	public void addNotes(String notes) 
	{
		this.notes.concat(notes);
	}
	/**
	 * Set Meeting slot to a range start to end
	 * @param start
	 * @param end
	 */

	public void addSlot(int start, int end) 
	{
		slot.addInRange(start, end);
	}
	/**
	 * Return Meeting notes
	 * @return the notes
	 */
	public String getNotes()
	{
		return notes;
	}
	/**
	 * Return the person with whom this Meeting happens
	 * @return the otherPerson
	 */
	public Person getOtherPerson() 
	{
		return otherPerson;
	}
	/**
	 * Returns a Meeting that is registered in the other agenda to which a Meeting is related 
	 * @return Meeting for corresponding entry in otherperson agenda
	 */
	public Meeting getTwinEntry()
	{
		Meeting result=null;
		Set<Agenda> agenda =	getOtherPerson().getAgendas();
		Iterator<Agenda> iter=agenda.iterator();
		Set<Entry> entries;
		while(iter.hasNext())
		{
			entries=iter.next().getEntries();
			for(Entry entry:entries)
			{
				if((entry instanceof Meeting) && entry.equals(this) )
				{
					result=(Meeting) entry;
				}
			}
		}
		return result;
	}

	/**
	 * Set Meeting notes
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) 
	{
		this.notes = notes;
	}
	/**
	 * Specify the person with whom this Meeting happens
	 * @param otherPerson the otherPerson to set
	 */
	public void setOtherPerson(Person otherperson) 
	{
		this.otherPerson = otherperson;
	}
}
