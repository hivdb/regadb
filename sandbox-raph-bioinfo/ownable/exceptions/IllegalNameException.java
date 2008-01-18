package ownable.exceptions;

/**
 * A class for signaling illegal names.
 * 
 * @version  2.0	
 * @author   Eric Steegmans
 */
public class IllegalNameException extends Exception {

	/**
	 * Initialize this new illegal name exception with given name.
	 *
	 * @param   name
	 *          The name for this new illegal name exception.
	 * @post    The name for this new illegal name exception
	 *          is equal to the given name.
	 *          | new.getName().equals(name)
	 */
	public IllegalNameException(String name) {
		this.name = name;
	}

	/**
	 * Return the name of this illegal name exception.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Variable registering the name of this illegal name exception.
	 */
	private final String name;

	/**
	 * The Java API strongly recommends to explicitly define a version
	 * number for classes that implement the interface Serializable.
	 * At this stage, that aspect is of no concern to us. 
	 */
	private static final long serialVersionUID = 2003001L;

}