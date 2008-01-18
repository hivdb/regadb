package demo;

/*demo*
 * A class illustrating support from Eclipse in generating
 * documentation using 'javadoc'.
 * 
 * @author   Eric Steegmans
 */
public class Person {

	// Generate documentation file for this class,
	// using "Generate Javadoc" from Project menu.

	/**
	 * Initialize this new person with given first name and given last name.
	 * 
	 * <P>
	 * If the given first name is effective, the first name of this new person
	 * is set to the given first name. Otherwise, the first name of this new
	 * person is set to the empty string.
	 * <P>
	 * The last name of this new person is set to the given last name.
	 * 
	 * @param firstName
	 *            The first name for this new person.
	 * @param lastName
	 *            The last name for this new person.
	 * @throws IllegalArgumentException
	 *             The given last name is not effective.
	 */
	public Person(String firstName, String lastName)
			throws IllegalArgumentException {
		if (lastName == null)
			throw new IllegalArgumentException("No effective last name!");
		if (firstName == null)
			firstName = "";
		this.firstName = firstName;
		this.lastName = lastName;
	}

	/**
	 * Variables referencing the first name, respectively the last name of this
	 * person.
	 */
	private final String firstName;

	private final String lastName;

}
