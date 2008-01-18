/**
 * General classes
 * 
 */
package oopexamination;


/**
 * Class Day
 * @author rsanged0
 * 
 */
@SuppressWarnings("deprecation")
public class Day 
{
	public static final java.util.Date firstday =new java.util.Date(1,1,1900);
	/**
	 * Return the first day as reference to convert days to Gregorian (normal) calendar
	 * 	 * @return the firstday
	 */
	public static java.util.Date getFirstday() 
	{
		return firstday;
	}
	
	private  final long day;

	/**
	 * Full constructor
	 * @param day
	 */
	public Day(long day) 
	{
		this.day = day;
	}
	public long getDay() 
	{
		return day;
	}

}
