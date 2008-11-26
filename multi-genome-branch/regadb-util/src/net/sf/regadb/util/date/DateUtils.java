package net.sf.regadb.util.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils 
{
    private static SimpleDateFormat europeanDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
    public static String getHQLdateFormatString() {
    	return "DD-MM-YYYY";
    }
    
    public static Date parseEuropeanDate(String europeanDate)
    {
    	if (europeanDate.trim().equals("")) {
    		return null;
    	}
    	
        try{
            return europeanDateFormat.parse(europeanDate);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public static String getEuropeanFormat(Date date)
    {
    	if(date == null)
    		return "";
    	else
    		return europeanDateFormat.format(date);
    }
    
    public static int compareDates(Date date1, Date date2)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date1).compareTo(sdf.format(date2));
    }
    
    public static Date parseDate(String timestamp){
        long l = Long.parseLong(timestamp);
        return new Date(l);
    }
    
    public static String getEuropeanFormat(String timestamp){
        return getEuropeanFormat(parseDate(timestamp));
    }
    
    public static Date getDateOffset(Date base, int calendarUnit, int offset){
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(calendarUnit, offset);
        return cal.getTime();
    }
}
