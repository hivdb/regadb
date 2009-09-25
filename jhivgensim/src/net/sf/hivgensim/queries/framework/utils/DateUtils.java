package net.sf.hivgensim.queries.framework.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static boolean betweenInterval(Date d, Date start, Date end) {
		return d.after(start) && d.before(end);
	}

	public static boolean betweenOrEqualsInterval(Date d, Date start, Date end) {
		return (d.after(start) || d.equals(start)) && (d.before(end) || d.equals(end));
	}

	public static Date getWindowEndDateFor(Date therapyStop){
		Calendar c = Calendar.getInstance();
		c.setTime(therapyStop);
		c.add(Calendar.MONTH, 1); // <= edit the window time here
		return c.getTime();
	}
	
	public static Date addDaysToDate(Date d, int daysToAdd) {
		Calendar c = Calendar.getInstance();

		c.setTime(d);
		c.add(Calendar.DAY_OF_MONTH, daysToAdd);

		return c.getTime();
	}
	
	public static long daysBetween(Date d1, Date d2){
		return (d2.getTime()-d1.getTime())/(1000*60*60*24);		
	}

}
