/**
 * Package oopexamination
 * contains general classes
 */
package oopexamination;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import oopexamination.entries.Appointment;
import oopexamination.entries.Entry;
import oopexamination.entries.Meeting;
import oopexamination.entries.PersonalEntry;
import oopexamination.exceptions.AgendaOwnerEntryOwnerMismatchException;
import oopexamination.exceptions.DuplicateSlotException;
import oopexamination.exceptions.FullSlotException;

/** 
 * Agenda class to add entries to respective slots of agenda day
 * Agenda day depends on EntryDay
 * Need to check if slots on given day are free before adding entries
 * Agenda for adding entries
 * @author rsanged0
 *
 */
public class Agenda
{
	//List all entries at each slot
	private final Set<Entry> agendaEntries=new HashSet<Entry>();
	private final Person holder;

	/**
	 * Constructor to initialize the person who owns the agenda
	 * @param holder The owner of ageda
	 */
	public Agenda(final Person holder) 
	{
		this.holder = holder;
	}


	/**
	 * Add entry to this agenda keyvalue collection of slot,event --
	 * Events have null keys
	 * @param entry
	 */
	public void addEntry(Entry entry) throws AgendaOwnerEntryOwnerMismatchException
	{
		if(!(entry.getPerson().equals(this.holder)))
			throw new AgendaOwnerEntryOwnerMismatchException(entry.getPerson(),holder);
		if(canHaveAsEntry(entry))
		{
            agendaEntries.add(entry);
		}
	}


	/**
	 * Add known set of entries to agenda
	 * @param entries
	 */
	public void addEntry(Set<Entry> entries) throws AgendaOwnerEntryOwnerMismatchException
	{
		for(Entry entry:entries)
				addEntry(entry);
	}


	/**
	 * Validate that the entry can be added
	 * Checking that for the entry day the agenda slots are not occupied (are free)
	 * Capture error for entry not addable
	 * Mainly checks availability of slots comparing current entry
	 * @param entry
	 * @return
	 */
	public boolean canHaveAsEntry(Entry entry)
	{
	    return entry.canHaveAsEntry();
	}

	/**
	 * Return a unique set of entries from the HashMap of slot,entries
	 * @return the entries which are unique
	 */
	public Set<Entry> getEntries() 
	{ 
		return agendaEntries;
	}

	/**
	 * Holder of agenda
	 * @return the holder
	 */
	public Person getHolder() 
	{
		return holder;
	}



	/**
	 * Total number of entries registered in an agenda
	 * @return getEntries().size()
	 */
	public int getNbEntries()
	{
		return getEntries().size();
		//entrySlots.size(); //return totalnumber of slots in agenda
	}

	/**
	 * Check wheather all slots in an agenda day are full
	 * @param day
	 * @return totalSlotsCountOnDay>=Slot.MAXIMUM_SLOT_NUMBER 
	 */
	public boolean isFullDay(long day)
	{
		Set<Entry> entriesOnDay=getEntriesOnDay(day);
		int totalSlotsCountOnDay=getEntriesSlotCount(entriesOnDay);
		return totalSlotsCountOnDay>=Slot.MAXIMUM_SLOT_NUMBER ;
	}
	/**
	 * Add entries in agenda with additional collection
	 * @param entries the entries to set
	 * @throws AgendaOwnerEntryOwnerMismatchException 
	 */
	public void setEntries(Set<Entry> entries) throws AgendaOwnerEntryOwnerMismatchException 
	{
		addEntry(entries);
	}

	/**
	 * Days in the current agenda
	 * @return list of all days in given agenda
	 */
	private List getAgendaDays() 
	{
		List<Long> result=new ArrayList<Long>();
		Iterator<Entry>iter=getEntries().iterator();
		while(iter.hasNext())
		{
			result.add(iter.next().getDay());
		}
		return result;
	}

	/** 
	 * Entries occuring on a given day
	 * @param day
	 * @return all entries in agenda which are on the given day
	 */
	public Set<Entry> getEntriesOnDay(long day) 
	{
		Set <Entry>result =new HashSet<Entry>();

		for(Entry entry:getEntries())
			if (entry.getEntryDay().getDay()==day)
				result.add(entry);
		return result;
	}
	/**
	 * Return count of slots in a given agenda day
	 * @param entriesOnDay
	 * @return count+=appointment.getSlot().size();
	 */
	private int getEntriesSlotCount(Set<Entry> entriesOnDay) 
	{
		int count=0;
		Iterator<Entry> iter=entriesOnDay.iterator();
		while(iter.hasNext())
		{
			Entry currentEntry=iter.next();
			if (Appointment.class.isAssignableFrom(currentEntry.getClass()))
			{
				Appointment appointment= (Appointment) currentEntry;
				count+=appointment.getSlot().size();
			}
		}
		return count;
	}
	

	/**
	 * Get entry at given index counts 1 to getNbEntries+1
	 * @param index the index 
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public Entry entryAt(int index) throws ArrayIndexOutOfBoundsException
	{
		Entry result=null;
		if(index>getNbEntries())
			throw new ArrayIndexOutOfBoundsException("Index "+index +" Out of entries bound");
		else
			result= (Entry) getEntries().toArray()[index-1];
		return result;
	}

}

