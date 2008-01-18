package oopexamination;



import java.util.TreeSet;

import oopexamination.exceptions.DuplicateSlotException;
import oopexamination.exceptions.FullSlotException;


/**
 * Slots for an Entry
 * @author rsanged0
 *
 */
@SuppressWarnings("serial")
public class Slot extends TreeSet<Integer>
{
	public final static int MAXIMUM_SLOT_NUMBER =24;

	/**
	 * Add additional slot to exisiting slot collection
	 * @param slot-slot number to add to slot
	 * @return true if add was successful
	 * @see java.util.TreeSet#add(java.lang.Object)
	 */

	public boolean addSlot(int intSlot) throws FullSlotException,IllegalArgumentException, DuplicateSlotException
	{
		if((isFull()) )
			throw new FullSlotException(intSlot,this);
		if (!(isValidSlot(intSlot)))
			throw new IllegalArgumentException("Invalid slot");
		if(this.contains(intSlot))
			throw new DuplicateSlotException(intSlot,this);
		return add(intSlot);
	}
	/**
	 * Check wheather the maximum slot is reached
	 * @return true if size()>=MAXIMUM_SLOT_NUMBER ;
	 */
	private boolean isFull()
	{
		return size()>=MAXIMUM_SLOT_NUMBER ;
	}
	/**
	 * Check wheather the slot int is less than max slot and more than 0 first slot is 1
	 * @param slot
	 * @return true if slot<0 && slot>=MAXIMUM_SLOT_NUMBER
	 */
	private boolean isValidSlot(int slot)
	{
		return slot>0 && slot<=MAXIMUM_SLOT_NUMBER ;
	}
	/**
	 * Add slots between start and end
	 * @param start
	 * @param end
	 *  * @throws DuplicateSlotException 
	 * @throws FullSlotException 
	 * @throws InvalidParameterException 
	 */
	public void addInRange(int start, int end)  
	{
		for(;start<=end;start++)
		{
			try {
				addSlot(start);
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
	}

}
