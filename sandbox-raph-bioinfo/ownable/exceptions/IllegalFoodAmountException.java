package ownable.exceptions;

/**
 * A class for signaling illegal food amounts.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public class IllegalFoodAmountException extends Exception {

	/**
	 * Initialize this new illegal food amount exception with given food amount.
	 *
	 * @param   foodAmount
	 *          The food amount for this new illegal food amount exception.
	 * @post    The food amount for this new illegal food amount exception
	 *          is equal to the given food amount.
	 *          | new.getFoodAmount() == foodAmount
	 */
	public IllegalFoodAmountException(int foodAmount) {
		this.foodAmount = foodAmount;
	}

	/**
	 * Return the food amount of this illegal food amount exception.
	 */
	public int getFoodAmount() {
		return this.foodAmount;
	}

	/**
	 * Variable registering the food amount of this illegal food amount exception.
	 */
	private final int foodAmount;

	/**
	 * The Java API strongly recommends to explicitly define a version
	 * number for classes that implement the interface Serializable.
	 * At this stage, that aspect is of no concern to us. 
	 */
	private static final long serialVersionUID = 2003001L;

}