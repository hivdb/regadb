/**
 * 
 */
package oopexamination;

import oopexamination.entries.Appointment;
import oopexamination.entries.Meeting;
import oopexamination.entries.PersonalEntry;

/**
 * Main test usage variables
 * @author rsanged0
 *
 */
public class MainTest 
{
	private  Meeting meeting;
	PersonalEntry pEntry;
	private  Agenda firstAgenda,secondAgenda;
	private  Person firstPerson,secondPerson,otherPerson;
	private Day meetingDay;
	private String meetingDescription,pEntryDescription,notes;
	/**
	 * @return the meeting
	 */
	public Meeting getMeeting() 
	{
		return meeting;
	}
	/**
	 * @return the firstAgenda
	 */
	public Agenda getFirstAgenda() 
	{
		return firstAgenda;
	}
	/**
	 * @return the firstPerson
	 */
	public Person getFirstPerson() 
	{
		return firstPerson;
	}
	/**
	 * @return the meetingDay
	 */
	public Day getMeetingDay() 
	{
		return meetingDay;
	}
	/**
	 * @return the meetingDescription
	 */
	public String getMeetingDescription() 
	{
		return meetingDescription;
	}
	
	/**
	 * @return the notes
	 */
	public String getNotes() 
	{
		return notes;
	}
	/**
	 * @return the otherPerson
	 */
	public Person getOtherPerson() 
	{

		return otherPerson;
	}
	/**
	 * @return the pEntry
	 */
	public Appointment getPEntry() 
	{
		return pEntry;
	}
	/**
	 * @return the pEntryDescription
	 */
	public String getPEntryDescription() {
		return pEntryDescription;
	}
	
	/**
	 * @return the secondAgenda
	 */
	public Agenda getSecondAgenda() {
		return secondAgenda;
	}
	/**
	 * @return the secondPerson
	 */
	public Person getSecondPerson() {
		return secondPerson;
	}
	/**
	 * @param meeting the meeting to set
	 */
	public void setMeeting(Meeting meeting) 
	{
		this.meeting = meeting;
	}
	/**
	 * @param firstAgenda the firstAgenda to set
	 */
	public void setFirstAgenda(Agenda firstAgenda) 
	{
		this.firstAgenda = firstAgenda;
	}
	/**
	 * @param firstPerson the firstPerson to set
	 */
	public void setFirstPerson(Person firstPerson) 
	{
		this.firstPerson = firstPerson;
	}
	/**
	 * @param meetingDay the meetingDay to set
	 */
	public void setMeetingDay(Day meetingDay)
	{
		this.meetingDay = meetingDay;
	}
	/**
	 * @param meetingDescription the meetingDescription to set
	 */
	public void setMeetingDescription(String meetingDescription) 
	{
		this.meetingDescription = meetingDescription;
	}
	
	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) 
	{
		this.notes = notes;
	}
	/**
	 * @param otherPerson the otherPerson to set
	 */
	public void setOtherPerson(Person otherPerson) 
	{
		this.otherPerson = otherPerson;
	}
	/**
	 * @param entry the pEntry to set
	 */
	public void setPEntry(PersonalEntry entry) 
	{
		pEntry = entry;
	}
	/**
	 * @param entryDescription the pEntryDescription to set
	 */
	public void setPEntryDescription(String entryDescription) 
	{
		pEntryDescription = entryDescription;
	}
	
	/**
	 * @param secondAgenda the secondAgenda to set
	 */
	public void setSecondAgenda(Agenda secondAgenda) {
		this.secondAgenda = secondAgenda;
	}
	/**
	 * @param secondPerson the secondPerson to set
	 */
	public void setSecondPerson(Person secondPerson)
	{
		this.secondPerson = secondPerson;
	}
}
