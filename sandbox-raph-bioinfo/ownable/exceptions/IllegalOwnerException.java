package ownable.exceptions;

import ownable.ownings.Ownable;
import ownable.persons.Person;

/**
 * A class for signaling illegal owners.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public class IllegalOwnerException extends Exception {

	/**
	 * Initialize this new illegal owner exception with given owner
	 * and given ownable.
	 *
	 * @param   owner
	 *          The owner for this new illegal owner exception.
	 * @param   ownable
	 *          The ownable for this new illegal owner exception.
	 * @post    The owner for this new illegal owner exception
	 *          is the same as the given owner.
	 *          | new.getOwner() == owner
	 * @post    The ownable for this new illegal owner exception
	 *          is the same as the given ownable.
	 *          | new.getOwnable() == ownable
	 */
	public IllegalOwnerException(Person owner, Ownable ownable) {
		this.owner = owner;
		this.ownable = ownable;
	}

	/**
	 * Return the owner of this illegal owner exception.
	 */
	public Person getOwner() {
		return this.owner;
	}

	/**
	 * Variable registering the owner of this illegal owner exception.
	 */
	private final Person owner;

	/**
	 * Return the ownable of this illegal owner exception.
	 */
	public Ownable getOwnable() {
		return this.ownable;
	}

	/**
	 * Variable registering the ownable of this illegal owner exception.
	 */
	private final Ownable ownable;

	/**
	 * The Java API strongly recommends to explicitly define a version
	 * number for classes that implement the interface Serializable.
	 * At this stage, that aspect is of no concern to us. 
	 */
	private static final long serialVersionUID = 2003001L;

}