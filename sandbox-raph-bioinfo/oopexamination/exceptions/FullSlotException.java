/**
 * 
 */
package oopexamination.exceptions;

import oopexamination.Slot;

/**
 * FullSlotException
 * @author rsanged0
 *
 */
@SuppressWarnings("serial")
public class FullSlotException extends Exception 
{
	private final int intSlot;
	private final Slot theSlot;

	public FullSlotException(int intSlot, Slot slot)
	{
		this.intSlot=intSlot;
		theSlot=slot;
	}

	/**
	 * @return the intSlot
	 */
	public int getIntSlot() {
		return intSlot;
	}

	/**
	 * @return the theSlot
	 */
	public Slot getTheSlot() {
		return theSlot;
	}

}
