package oopexamination;

import static org.junit.Assert.*;

import oopexamination.entries.Appointment;
import oopexamination.entries.Entry;
import oopexamination.entries.Meeting;
import oopexamination.entries.PersonalEntry;
import oopexamination.exceptions.AgendaOwnerEntryOwnerMismatchException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for agenda
 * @author rsanged0
 *
 */
public class AgendaTest
{
	private Appointment appointment,pEntry,appointment2;
	private Person firstPerson,secondPerson,personNoAgenda;
	private Agenda firstAgenda,secondAgenda,blankAgenda;
	private Day meetingDay;
	private Slot meetingSlot,personalSlot;
	private String meetingDescription,strNote;
	private String pEntryDescription;
	private Slot pEntrySlot;
	
	
	@BeforeClass public static void setUpBeforeClass() throws Exception 
	{
	}

	@Before	public void setUp() throws Exception 
	{
		 firstPerson=new Person("John");
		 secondPerson=new Person("Mary");
		 personNoAgenda=new Person("someonewithnoagenda");
		firstAgenda=new Agenda(firstPerson);
		secondAgenda=new Agenda(secondPerson);
		blankAgenda=new Agenda(secondPerson);
		 meetingDay=new Day(100);
		 meetingSlot=new Slot();
		 personalSlot=new Slot();
		meetingSlot.addInRange(12,14);
		
		 meetingDescription=new String("Meeting with Mary");
		 strNote=new String("Some note");
		 pEntryDescription=new String("");
		 
			appointment=new Meeting(firstAgenda,meetingDescription,
					meetingDay , firstPerson, meetingSlot, strNote, secondPerson, secondAgenda);
			
			appointment2=new Meeting(firstAgenda,meetingDescription,
					meetingDay , firstPerson, meetingSlot, strNote, personNoAgenda, blankAgenda);
			firstAgenda.addEntry(appointment);

			personalSlot.add(4);
			pEntry=new PersonalEntry(firstAgenda,pEntryDescription,meetingDay , 
					firstPerson, personalSlot);
		 
		 pEntrySlot=new Slot();
		pEntrySlot.addInRange(5,11);
		pEntrySlot.addInRange(18,20);
		pEntry.addSlot(23);
		
		firstAgenda.addEntry(pEntry);

	}

	@Test	public void testGetNbEntries() 
	{
		assertTrue((firstAgenda.getNbEntries()==2));
	}

	@Test	public void testIsFreeAtIntIntLong() 
	{
		assertTrue("Agenda is not free at 15,16 and 17 slots :",firstAgenda.isFreeAt(15, 17, 100));
	}

	@Test	public void testIsFreeAtSlotLong() 
	{
		Slot someSlot=new Slot();
		someSlot.addInRange(1, 3);
		assertTrue(firstAgenda.isFreeAt(someSlot, 100));
	}

	@Test	public void testIsFullDay() 
	{
		assertFalse((firstAgenda.isFullDay(100)));
	}
	@Test(expected=ArrayIndexOutOfBoundsException.class)	public void testEntryAt()
	{
		secondAgenda.entryAt(2); //there is only one entry in second agenda registered by the firstpersons meeting
	}
	@Test(expected=AgendaOwnerEntryOwnerMismatchException.class)	public void testAddEntry() 	
	throws Exception
	{
		blankAgenda.addEntry(appointment2);
	}
	
}
