package expressions.exceptions;

/**
 * A class for signaling illegal addresses.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public class IllegalAddressException extends Exception {

	/**
	 * Initialize this new illegal address exception with given address.
	 *
	 * @param   address
	 *          The address for this new illegal address exception.
	 * @post    The address for this new illegal address exception
	 *          is equal to the given address.
	 *          | new.getAddress() == address
	 */
	public IllegalAddressException(int address) {
		this.address = address;
	}

	/**
	 * Return the address of this illegal address exception.
	 */
	public int getAddress() {
		return this.address;
	}

	/**
	 * Variable registering the address of this illegal address exception.
	 */
	private final int address;

	/**
	 * The Java API strongly recommends to explicitly define a version
	 * number for classes that implement the interface Serializable.
	 * At this stage, that aspect is of no concern to us. 
	 */
	private static final long serialVersionUID = 2003001L;

}
