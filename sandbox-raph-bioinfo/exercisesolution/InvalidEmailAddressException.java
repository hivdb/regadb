package exercisesolution;

/**
 * An exception indicating the use of an invalid email address
 * 
 * @author Rutger Claes
 */
@SuppressWarnings("serial")
public class InvalidEmailAddressException extends Exception {

	/*
	 * The email address that caused this exception
	 */
	private final String emailAddress;

	/**
	 * Create a new InvalidEmailAddressException
	 * 
	 * @param emailAddress
	 *            the email address causing the exception
	 * 
	 * @post the email address will be set | new.getEmailAddress() ==
	 *       emailAddress
	 */
	public InvalidEmailAddressException(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the email address that caused this exception
	 */
	public String getEmailAddress() {
		return this.emailAddress;
	}
}
