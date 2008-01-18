
package oopexamination.entries;

import static org.junit.Assert.*;

import oopexamination.Agenda;
import oopexamination.Day;
import oopexamination.Person;
import oopexamination.Slot;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppointmentTest 
{
	private  Appointment appointment1,pEntry;
	private static Agenda firstAgenda,secondAgenda;
	private static Person firstPerson,secondPerson;
	private Day meetingDay;
	private Slot meetingSlot,pEntrySlot;
	private String meetingDescription,pEntryDescription,notes;
	
	@BeforeClass	public static void setUpBeforeClass() throws Exception
	{
		firstPerson=new Person("John");
		secondPerson=new Person("Mary");
		firstAgenda=new Agenda(firstPerson);
		secondAgenda=new Agenda(secondPerson);
	}
	

	@Before	public void setUp() throws Exception 
	{
		 meetingDay=new Day(100);
		 meetingSlot=new Slot();
		 pEntrySlot=new Slot();
		meetingSlot.addInRange(12,14);
		 meetingDescription=new String("Meeting with Mary");
		 pEntryDescription  ="";
		 notes  ="";
		 secondPerson=new Person("Mary");
		
		 appointment1=new Meeting(firstAgenda, meetingDescription,meetingDay, firstPerson,meetingSlot 
					,notes, secondPerson, secondAgenda );
		 
		pEntrySlot.addInRange(5,11);
			pEntrySlot.addInRange(18,20);
			
			
			 pEntry=new PersonalEntry(firstAgenda,pEntryDescription,meetingDay, 
					 firstPerson, pEntrySlot);
				 pEntry.addSlot(23);
				 
				 firstAgenda.addEntry(appointment1);
				firstAgenda.addEntry(pEntry);
	}
	@Test	public void testGetNbSlots() 
	{			
		assertTrue("Number of slots in this personal entry not 11",pEntry.getNbSlots()==11);
	}
	
	@Test	public void testOccupies() 
	{
		assertTrue("This appointment does not occupies slots 13 to 14",appointment1.occupies(13, 14));
	}

	@Test	public void testStartsBefore() 
	{
		assertFalse(appointment1.startsBefore(pEntry));
	}
	
}
