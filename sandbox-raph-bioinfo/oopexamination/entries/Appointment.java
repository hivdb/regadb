
package oopexamination.entries;


import oopexamination.Agenda;
import oopexamination.Day;
import oopexamination.Person;
import oopexamination.Slot;
import oopexamination.exceptions.DuplicateSlotException;
import oopexamination.exceptions.FullSlotException;
/**
 * Appointment entry
 * base for Meeting and PersonalEntry classes
 * @author rsanged0
 *
 */
public class Appointment extends Entry
{

	protected Slot slot;



	/**
	 * Full Appointment constructor
	 * 
	 * @param agenda The agenda to which entry is made
	 * @param description Description of the entry
	 * @param entryDay The day of the entry
	 * @param person The owner of entry ?redundant? also in agenda?
	 * @param slot The slots of appointments
	 */
	public Appointment(Agenda agenda, String description, 
			Day entryDay, Person person, Slot slot) 
	{
		super(agenda, description, entryDay, person);
		this.slot = slot;
	}


	/**
	 * Add slot to collection
	 * @param newslot
	 * Slot to add to the collection
	 */
	public void addSlot(int newslot)
	{
		try {
			slot.addSlot(newslot);
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (FullSlotException e) 
		{
			e.printStackTrace();
		} catch (DuplicateSlotException e) 
		{
			e.printStackTrace();
		}
	}


	public int getFirstSlot() 
	{
		return slot.first();
	}
	/** Return last slot in a collection
	 * @return  slot.last();
	 */
	public int getLastSlot()
	{
		return slot.last();
	}
	/**
	 * Return number of slots in a slot collection
	 * @return
	 */
	public int getNbSlots()
	{
		return slot.size();
	}



	/**
	 * Get slots occupied by this appointment
	 * @return the slot
	 */
	public Slot getSlot() 
	{
		return slot;
	}

	/* (non-Javadoc)
	 * @see oopexamination.entries.Entry#getFirstSlot()
	 * @return  slot.first();
	 */

	//	require start<end 
//	that start and end are valid time slots
	/**
	 * Check whether a given personalentry or meeting occupies a given slot in range start to end
	 * @param start
	 * @param end
	 * @return true if slot.containsAll slots in that range
	 */
	public boolean occupies(int start,int end)
	{
		Slot slotSlice=new Slot();
		slotSlice.addInRange(start, end);
		return (slot.containsAll(slotSlice));
	}


	/**
	 * Set slot to given slots collection
	 * @param slot the slot to set
	 */
	public void setSlot(Slot slot) 
	{
		this.slot = slot;
	}

	/**
	 * (non-Javadoc)
	 * @see oopexamination.entries.Entry#startsBefore(oopexamination.entries.Entry)
	 * @param end
	 * @return true if slot.containsAll slots in that range
	 */
	@Override
	public boolean startsBefore(Entry entry) 
	{
		if (this.getDay()<entry.getDay())
			return true;
		if ((this.getDay()==entry.getDay()) && (this.getFirstSlot()<entry.getFirstSlot()))
			return true;
		return false;

	}
}
