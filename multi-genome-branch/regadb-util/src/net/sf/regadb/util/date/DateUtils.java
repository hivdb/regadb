package net.sf.regadb.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sf.regadb.util.settings.RegaDBSettings;


//TODO
//rethink these mechanisms
public class DateUtils 
{
    private static SimpleDateFormat europeanDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    public static String getHQLdateFormatString() {
    	return "DD-MM-YYYY";
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
