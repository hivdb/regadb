package net.sf.regadb.io.db.ghb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GhbUtils {
    public static DateFormat LISDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    public static DateFormat filemakerDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
    private static Date earliestDate = getEarliestDate();

    public static boolean isValidDateOrNull(Date d){
    	return d == null || isValidDate(d);
    }
    public static boolean isValidDate(Date d){
    	if(earliestDate.after(d))
    		return false;
    	
    	if(new Date().before(d))
    		return false;
    	
    	return true;
    }
    
    private static Date getEarliestDate(){
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.YEAR, 1800);
    	cal.set(Calendar.MONTH, 1);
    	cal.set(Calendar.DAY_OF_MONTH, 1);
    	
    	return cal.getTime();
    }
}
