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
	private final HashMap<Integer, Entry> entrySlots=new HashMap<Integer, Entry>();
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
			if (Appointment.class.isAssignableFrom(entry.getClass()))
			{
				Appointment app= (Appointment) entry;
				Slot slots=app.getSlot();
				Iterator iter= slots.iterator();
				while(iter.hasNext())
				{
					entrySlots.put((Integer) iter.next(),entry);
				}
			}
			else
			{
				// slot is null for events
				entrySlots.put(null,entry);
			}
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
		boolean result = false;
		if(entry==null)
			return false;
		if(entrySlots.isEmpty())
			return true; //The first entry can freely added
		if (Appointment.class.isAssignableFrom(entry.getClass()))
		{
			Appointment appointment= (Appointment) entry;
			long day=appointment.getDay();
			if(Meeting.class.isAssignableFrom(appointment.getClass()))
			{
				//ok
				result=isFreeAt(appointment.getFirstSlot(),appointment.getLastSlot(),day);	
			}
			else //if PersonalEntry its slots do not have a range
				if(PersonalEntry.class.isAssignableFrom(appointment.getClass()))
				{
					result=isFreeAt(appointment.getSlot(),day);
				}

		}
		else //for entry no need to check slots availability
		{
			result=true; 
		}
		return result;

	}

	/**
	 * Return a unique set of entries from the HashMap of slot,entries
	 * @return the entries which are unique
	 */
	public Set<Entry> getEntries() 
	{
		Collection <Entry>entries;
		Set<Entry> result=new HashSet<Entry>();
		entries =entrySlots.values(); //these include duplicate entries

		for(Entry entry:entries)
		{
			if(!(result.contains(entry)))
			{
				try 
				{
					result.add(entry); //Convert collection to Set 
				} catch (RuntimeException e) 
				{
					e.printStackTrace();
				}
			}
		}
		return result;
	}


	/**
	 * Entry occupying the slot at a given day
	 * @param slot
	 * @return enry
	 */
	//	mind that slots are not applicable for entries of type entry but only appointments
	public Entry getEntryAt(int slot, long day)
	{
		Entry result=null;
		List daysInAgenda=getAgendaDays();
		if(daysInAgenda.contains(day))
		{
			HashMap<Integer, Entry> entriesSlotsOnDay =new HashMap<Integer,Entry>();
			entriesSlotsOnDay=getEntriesSlotsOnDay(day);
			if(entriesSlotsOnDay.containsKey(slot))
				result=entriesSlotsOnDay.get(slot);
		}
		else
		{
			//The day is not in agenda
		}

		return result;
	}

	/**
	 * Return key values for slot entry pair
	 * @return The entrySlots
	 */
	public HashMap getEntrySlots() 
	{
		return entrySlots;
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
	 * Check if slots in given range are free in a day
	 * //	require start<end 
	 * //	that start and end are valid time slots
	 * // mind that slots are not applicable for entries of type entry
	 * //  parameter day was not originally provided
	 * @param start
	 * @param end
	 * @param day
	 * @return true if the slot range is free in agenda for a given day
	 * 
	 *
	 */
	public boolean isFreeAt(int start, int end, long day)
	{
		//add up all entries slots into a sliceslots -no duplicates of course -- a TreeSet
		Slot slotSlice=new Slot();

		slotSlice.addInRange(start, end);
		Slot slotInAgenda=getAgendaSlotsOnDay(day);
		return (!(slotInAgenda.containsAll(slotSlice)));

	}
	/**
	 * Check if the given slotslice is occupied for a given day 
	 * @param slotSlice slot to check
	 * @param day day to check
	 * @return
	 */
	public boolean isFreeAt(Slot slotSlice, long day)
	{
		Slot slot=getAgendaSlotsOnDay(day);
		return(! (slot.containsAll(slotSlice)));	//return false if not free;
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
	 */
	public void setEntries(Set<Entry> entries) 
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
	 * Get Slots on specified agenda day
	 * @param day
	 * @return slot in appointment
	 */
	private Slot getAgendaSlotsOnDay(long day) 
	{

		Slot result=new Slot();
		Set<Entry> entriesOnDay =new HashSet<Entry>();
		entriesOnDay=getEntriesOnDay(day);
		// DEBUG System.out.println(entriesOnDay.size());//0?

		Iterator<Entry> iter=entriesOnDay.iterator();
		while(iter.hasNext())
		{
			Entry currentEntry=iter.next();
			if (Appointment.class.isAssignableFrom(currentEntry.getClass()))
			{
				Appointment app= (Appointment) currentEntry;
				result.addAll(app.getSlot());
			}
		}
		return result;
	}
	/** 
	 * Entries occuring on a given day
	 * @param day
	 * @return all entries in agenda which are on the given day
	 */
	private Set<Entry> getEntriesOnDay(long day) 
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
	 * Return subset of entry slots for a given day in agenda
	 * @param day entries and slots pair in agenda for a specified day 
	 * @return result.put((Integer)currentKey, currentEntry);
	 */
	private HashMap<Integer, Entry> getEntriesSlotsOnDay(long day) 
	{
		HashMap<Integer, Entry> result=new HashMap<Integer, Entry> ();
		Iterator iter=entrySlots.keySet().iterator();
		while(iter.hasNext())
		{
			Object currentKey=iter.next();
			Entry currentEntry=entrySlots.get(currentKey);
			if(currentEntry.getDay()==day)
				result.put((Integer)currentKey, currentEntry);
		}

		return result;
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

