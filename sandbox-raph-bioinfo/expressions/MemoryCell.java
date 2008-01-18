package expressions;

import be.kuleuven.cs.som.annotate.Raw;
import expressions.exceptions.IllegalAddressException;

/**
 * A class of memory cells involving an address and
 * a contents that serves as the value of a memory cell.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public class MemoryCell extends BasicExpression {

	/**
	 * Initialize this new memory cell with given address
	 * and initial value of 0.
	 * 
	 * @param   address
	 *          The address of this new memory cell.
	 * @post    The address of this new memory cell is equal to
	 *          to the given address.
	 *          | new.getAddress() == address
	 * @post    The value of this new memory cell is equal to 0
	 *          | new.getValue() == 0
	 * @throws  IllegalAddressException [must]
	 *          The given address is not a valid address for a
	 *          memory cell.
	 *          | ! isValidAddress(address)
	 */
	public MemoryCell(int address) throws IllegalAddressException {
		if (!isValidAddress(address))
			throw new IllegalAddressException(address);
		this.address = address;
		setValue(0);
	}

	/**
	 * Check whether this memory cell is equal to the object.
	 *
	 * @return  True if and only if the other object is an effective memory cell
	 *          with the same address and the same value as this memory cell;
	 *          false otherwise.
	 *          | result ==
	 *          |   (other instanceof MemoryCell) &&
	 *          |   (this.getAddress() == ((MemoryCell)other).getAddress()) &&
	 *          |   (this.getValue() == ((MemoryCell)other).getValue())
	 */
	@Override public boolean equals(Object other) {
		try {
			MemoryCell otherCell = (MemoryCell) other;
			return (this.getAddress() == otherCell.getAddress()) &&
				(this.getValue() == otherCell.getValue());
		} catch (RuntimeException exc) {
			assert (! (other instanceof MemoryCell));
			return false;
		}
	}

	/**
	 * Check whether the state of this memory cell can be changed.
	 * 
	 * @return  Always true.
	 *          | result == true
	 */
	@Override public boolean isMutable() {
		return true;
	}

	/**
	 * Return the address of this memory cell.
	 */
	@Raw public int getAddress() {
		return address;
	}

	/**
	 * Check whether the given address is a valid address for
	 * a memory cell.
	 * 
	 * @param   address
	 *          The address to check.
	 * @return  True if the given address is not negative; false
	 *          otherwise.
	 *          | result == (address >= 0)
	 */
	public static boolean isValidAddress(int address) {
		return address >= 0;
	}

	/**
	 * Variable registering the address of this memory cell.
	 * 
	 * @invar   The registered address is a valid address for a
	 *          memory cell.
	 *          | isValidAddress(address)
	 */
	private final int address;

	/**
	 * Return the value stored in this memory cell.
	 */
	@Override public long getValue() {
		return value;
	}

	/**
	 * Set the value stored in this memory cell to the given value.
	 * 
	 * @param   value
	 *          The value to register.
	 * @post    The value stored in this memory cell is equal to the
	 *          given value.
	 *          | new.getValue() == value
	 */
	public void setValue(long value) {
		this.value = value;
	}

	/**
	 * Variable registering the value stored in this memory cell.
	 */
	private long value;

	/**
	 * Return a textual representation of this memory cell.
	 *
	 * @return  The textual representation of the address of this
	 *          memory cell, preceded by a capital M.
	 *          | result.equals("M"+getAddress().toString())
	 */
	@Override public String toString() {
		return "M" + getAddress();
	}

}
