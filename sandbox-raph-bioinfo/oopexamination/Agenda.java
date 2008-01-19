/**
 * Package oopexamination
 * contains general classes
 */
package oopexamination;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oopexamination.entries.Entry;
import oopexamination.exceptions.AgendaOwnerEntryOwnerMismatchException;

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
	private final Set<Entry> entries=new HashSet<Entry>();
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
			entries.add(entry);
		}
	}


	/**
	 * Add known set of entries to agenda
	 * @param entries
	 */
	public void addEntry(Set<Entry> entries) 
	{
		for(Entry entry:entries)
			try {
				addEntry(entry);
			} catch (AgendaOwnerEntryOwnerMismatchException e) 
			{
				e.printStackTrace();
			}
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
	 * Get entry at given index counts 1 to getNbEntries+1
	 * @param index the index 
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public Entry entryAt(int index) throws ArrayIndexOutOfBoundsException
	{
		if(index>getNbEntries())
			throw new ArrayIndexOutOfBoundsException("Index "+index +" Out of entries bound");
		else
			return (Entry) entries.toArray()[index-1];
	}

	/**
	 * Days in the current agenda
	 * @return list of all days in given agenda
	 */
	public List<Long> getAgendaDays() 
	{
		List<Long> result=new ArrayList<Long>();
		for(Entry entry:entries)
		{
			if(!(result.contains(entry.getDay())))
				result.add(entry.getDay());
		}
		return result;
	}



	/**
	 * Return a unique set of entries from the HashMap of slot,entries
	 * @return the entries which are unique
	 */
	public Set<Entry> getEntries() 
	{ 
		return entries;
	}

	/** 
	 * Entries occuring on a given day
	 * @param day
	 * @return all entries in agenda which are on the given day
	 */
	public Set<Entry> getEntriesOnDay(long day) 
	{
		Set <Entry>result =new HashSet<Entry>();
		for(Entry entry:entries)
			if (entry.getDay()==day)
				result.add(entry);
		return result;
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
		return entries.size();
	}

	/**
	 * Return count of slots in a given agenda day
	 * @param entriesOnDay
	 * @return count+=appointment.getSlot().size();
	 */
	public Slot getSlotsOnDay(long day) 
	{
		Slot SlotsOnDay=new Slot();
		for(Entry entry:entries)
		{
			if(entry.getDay()==day)
			{
				Slot entrySlot=entry.getSlot();

				if(!(entrySlot==null)) //not one without slot
					SlotsOnDay.addInRange(entrySlot.first().intValue(),entrySlot.last().intValue());
			}
		}
		return SlotsOnDay;
	}
	public boolean isFreeAt(Slot slot, long day) 
	{
		return (!(getSlotsOnDay(day).containsAll(slot)));
	}

	/**
	 * Check wheather all slots in an agenda day are full
	 * @param day
	 * @return totalSlotsCountOnDay>=Slot.MAXIMUM_SLOT_NUMBER 
	 */
	public boolean isFullDay(long day)
	{
		int slotsOccupiedOnDayNb=0;
		for(Entry entry:entries)
		{
			if(entry.getDay()==day)
			{
				Slot entrySlot=entry.getSlot();

				if(!(entrySlot==null)) //not one without slot
					slotsOccupiedOnDayNb+=entrySlot.size();
			}
		}
		return slotsOccupiedOnDayNb>=Slot.MAXIMUM_SLOT_NUMBER ;
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


}

