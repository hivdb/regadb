package ownable.filters;

import java.math.BigInteger;

import ownable.ownings.Dog;


/**
 * A class for filtering food amounts out of objects.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public class FoodAmountExtractor extends BigIntegerExtractor {
	
	/**
	 * Return the daily food amount needed by the given object
	 * according to this food amount extractor.
	 * 
	 * @return  If the given object is a dog, the daily food
	 *          amount needed by that dog is returned; otherwise
	 *          0 is returned.
	 *          | if (object instanceof Dog)
	 *          |   then result == BigInteger.valueOf(((Dog) object).getDailyFoodAmount())
	 *          |   else result == BigInteger.ZERO
	 * @see		superclass
	 */
	public BigInteger getValueFor(Object object) {
		try {
			return BigInteger.valueOf(((Dog) object).getDailyFoodAmount());
		}
		catch (RuntimeException exc) {
		    assert (object == null) || (! (object instanceof Dog));
			return BigInteger.ZERO;
		}
	}

}
