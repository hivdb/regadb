package oopexamination;

import oopexamination.exceptions.DuplicateSlotException;
import oopexamination.exceptions.FullSlotException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for class slot
 * @author rsanged0
 *
 */
public class SlotTest 
{
	private Slot slot;

	@BeforeClass	public static void setUpBeforeClass() throws Exception 
	{

	}

	@Before	public void setUp() throws Exception 
	{
		slot=new Slot();
	}

	@Test(expected=IllegalArgumentException.class)	public void testAddInt_InvalidSlot() 
	throws Exception
	{
		slot=new Slot();
		slot.addSlot(50);
	}
	@Test(expected=DuplicateSlotException.class)	public void testAddInt_DuplicateSlot() 
	throws Exception
	{
		slot=new Slot();
		slot.addSlot(12);
		slot.addSlot(12);
	}
	@Test(expected=FullSlotException.class)	public void testAddInt_FullSlot() 
	throws Exception
	{
		slot=new Slot();
		slot.addInRange(1,Slot.MAXIMUM_SLOT_NUMBER);
		slot.addSlot(12);
	}
}
