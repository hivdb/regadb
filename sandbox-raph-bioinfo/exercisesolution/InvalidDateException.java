package exercisesolution;

import java.util.Date;

/**
 * An exception indicating an invalid date has been used
 * 
 * @author Rutger Claes
 */
@SuppressWarnings("serial")
public class InvalidDateException extends Exception {

	/*
	 * The date causing this exception
	 */
	private final Date date;

	/**
	 * Create a new InvalidDateException object
	 * 
	 * @param date
	 *            the date object causing this exception
	 * 
	 * @post the date will be set | new.getDate() == date
	 */
	public InvalidDateException(Date date) {
		this.date = date;
	}

	/**
	 * @return the date object that caused this exception
	 */
	public Date getDate() {
		return this.date;
	}
}
