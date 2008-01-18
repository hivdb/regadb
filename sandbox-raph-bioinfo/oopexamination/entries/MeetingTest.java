package oopexamination.entries;

import static org.junit.Assert.*;

import oopexamination.Agenda;
import oopexamination.Day;
import oopexamination.Person;
import oopexamination.Slot;
import oopexamination.exceptions.AgendaOwnerEntryOwnerMismatchException;
import oopexamination.exceptions.FullSlotException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MeetingTest 
{

	private Appointment appointment,pEntry;
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
	@Test(expected=AgendaOwnerEntryOwnerMismatchException.class)	public void testAddEntry() 	
	throws Exception
	{
		
		
	}

}
