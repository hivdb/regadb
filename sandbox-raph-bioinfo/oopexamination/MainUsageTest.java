/**
 * 
 */
package oopexamination;

import oopexamination.entries.Entry;
import oopexamination.entries.Meeting;
import oopexamination.entries.PersonalEntry;
import oopexamination.exceptions.AgendaOwnerEntryOwnerMismatchException;

/**
 * @author rsanged0
 *Test the functionality of electronic agenda
 */
public class MainUsageTest 
{

	/**
	 * Main method testing the functionality of agenda
	 * @param args
	 */
	public static void main(String[] args) 
	{
		MainTest usageTest=new MainTest();
		//persons
		usageTest.setFirstPerson(new Person("John"));
		usageTest.setSecondPerson(new Person("Mary"));
		//agenda
		usageTest.setFirstAgenda(new Agenda(usageTest.getFirstPerson()));
		usageTest.setSecondAgenda(new Agenda(usageTest.getSecondPerson()));
		
		System.out.println("Number of entries  in second  agenda before meeting is set is "+usageTest.getSecondAgenda().getNbEntries()+ " Expected 0 \n");
		//Meeting day
		usageTest.setMeetingDay(new Day(100));
		//description
		usageTest.setMeetingDescription(new String("Meeting with Mary"));
		usageTest.setNotes(new String(""));
		usageTest.setPEntryDescription(new String(""));
		//slots
		Slot MeetingSlot=new Slot();
		Slot pEntrySlot=new Slot();
		MeetingSlot.addInRange(12, 14);
		//Test number of slots of meeting
		System.out.println("Number of slots in meeting slot is "+MeetingSlot.size()+" :Expected =3 \n");
		
		pEntrySlot.addInRange(5, 11);
		pEntrySlot.addInRange(18, 20);
		//test number of slots
		System.out.println("Number of slots in personal entry slot is "+pEntrySlot.size()+" :Expected =10 \n");
		
		//entries
		
		usageTest.setMeeting(new Meeting(usageTest.getFirstAgenda(), usageTest.getMeetingDescription(), 
				usageTest.getMeetingDay(), usageTest.getFirstPerson(), MeetingSlot, usageTest.getNotes(),
				usageTest.getSecondPerson(), usageTest.getSecondAgenda()));
		System.out.println(usageTest.getFirstPerson().getName()+" vs "+usageTest.getSecondPerson().getName());
		System.out.println(usageTest.getFirstAgenda().getHolder().getName()+" vs "+usageTest.getMeeting().getPerson().getName());
		
		usageTest.setPEntry(new PersonalEntry(usageTest.getFirstAgenda(), usageTest.getPEntryDescription(), 
			usageTest.getMeetingDay(), usageTest.getFirstPerson(), pEntrySlot));
		
		//add slot 23 to PEntry
		usageTest.getPEntry().addSlot(23);
		//test again number of slots
		System.out.println("Number of slots in personal entry slot after adding another one slot is "+usageTest.getPEntry().getSlot().size()+" :Expected =11 \n");
		
		//test number of entries in first agenda
		System.out.println("Number of entries before any entry is  "+usageTest.getFirstAgenda().getNbEntries()+ " Expected 0 \n");
		///add entries to agendas 
		try {
			usageTest.getFirstAgenda().addEntry(usageTest.getMeeting());
		} catch (AgendaOwnerEntryOwnerMismatchException e) {
	
			e.printStackTrace();
		}
		System.out.println("Number of entries after one entry is "+usageTest.getFirstAgenda().getNbEntries()+ " Expected 1 \n");
		try {
			usageTest.getFirstAgenda().addEntry(usageTest.getPEntry());
		} catch (AgendaOwnerEntryOwnerMismatchException e)
		{
			e.printStackTrace();
		}
		System.out.println("Number of entries after one more entry is "+usageTest.getFirstAgenda().getNbEntries()+ " Expected 2 \n");
		System.out.println("Number of entries  in second  agenda is "+usageTest.getSecondAgenda().getNbEntries()+ " Expected 1 \n");
		////check if  the entry starts before another entry
		Entry entryInSecondAgenda= usageTest.getSecondAgenda().entryAt(1);
		System.out.println("The personal entry of  " +usageTest.getPEntry().getPerson().getName()  +
				(usageTest.getPEntry().startsBefore(entryInSecondAgenda)?" is early":"is late")+" than meeting of "+
				entryInSecondAgenda.getPerson().getName()+" in second agenda \n");
		System.out.println("Expected output is :The personal entry of  John is early than meeting of Mary in second agenda");
	}

}
