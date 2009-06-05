package net.sf.regadb.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sf.regadb.util.settings.RegaDBSettings;

public class DateUtils 
{
    public static String getHQLdateFormatString() {
    	return "DD-MM-YYYY";
    }
    
    public static String format(String timestamp){
    	return format(parseDate(timestamp));
    }
    
    public static String format(Date date){
        if(date == null)
            return "";
        
    	SimpleDateFormat sdf = new SimpleDateFormat(RegaDBSettings.getInstance().getDateFormat());
    	return sdf.format(date);
    }
    
    public static Date parse(String date){
    	if(date == null || date.trim().length() == 0)
    		return null;
    	
    	SimpleDateFormat sdf = new SimpleDateFormat(RegaDBSettings.getInstance().getDateFormat());
    	try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public static int compareDates(Date date1, Date date2)
    {
    	if(date1 == date2)
    		return 0;
    	if(date1 == null)
    		return 1;
    	if(date2 == null)
    		return -1;
    	
    	if(date1.before(date2))
    		return -1;
    	if(date1.after(date2))
    		return 1;
    	
    	return 0;
    }
    
    public static Date parseDate(String timestamp){
        long l = Long.parseLong(timestamp);
        return new Date(l);
    }
    
    public static Date getDateOffset(Date base, int calendarUnit, int offset){
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(calendarUnit, offset);
        return cal.getTime();
    }

    public static boolean equals(Date d1, Date d2){
    	return equals(d1, d2, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
    }
    public static boolean equals(Date d1, Date d2, int... calenderFields){
    	if(d1 == d2)
    		return true;
    	if(d1 == null || d2 == null)
    		return false;
    	
    	if(d1.equals(d2))
    		return true;
    	
    	Calendar c1 = Calendar.getInstance();
    	c1.setTime(d1);
        	
    	Calendar c2 = Calendar.getInstance();
    	c2.setTime(d2);
        	
    	for(int field : calenderFields)
    		if(c1.get(field) != c2.get(field))
    			return false;
    	return true;
    }
    
    public static void main(String args[]) throws InterruptedException{
    	Date d1 = new Date();
    	Thread.sleep(1000);
    	Date d2 = new Date();
    	
    	System.out.println(equals(d1,d2));
    }
    
    public boolean isValidDate(Date d){
    	return isValidDate(d,
    			RegaDBSettings.getInstance().getInstituteConfig().getMinYear(),
    			RegaDBSettings.getInstance().getInstituteConfig().getMaxDaysFuture());
    }
    public boolean isValidDate(Date d, int minYear, int maxDays){
    	if(minYear > -1){ 
        	Calendar cal = Calendar.getInstance();
        	cal.setTime(d);

    		if(cal.get(Calendar.YEAR) < minYear)
    			return false;
    	}

    	if(maxDays > -1){
	       	Calendar cal = Calendar.getInstance();
	       	cal.add(Calendar.DAY_OF_MONTH, maxDays+1);
	       	
	       	if(d.after(cal.getTime()))
	       		return false;
    	}
    	return true;
    }
}
