package ownable.filters;

import java.math.BigInteger;

/**
 * A class for extracting big integer values out of objects.
 * 
 * @version  2.0
 * @author   Eric Steegmans
 */
public abstract class BigIntegerExtractor {
	
	/**
	 * Return the value resulting from applying this big integer extractor to
	 * the given object.
	 * 
	 * @param   object
	 *          The object to evaluate.
	 * @return  The resulting value is effective.
	 *          | result != null
	 */
	public abstract BigInteger getValueFor(Object object); 

}
