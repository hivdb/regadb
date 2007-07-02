package net.sf.regadb.util.date;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class DateUtils 
{
    private static Calendar calendar_ = new GregorianCalendar();
    
    public static Date parserEuropeanDate(String europeanDate)
    {
        StringTokenizer st = new StringTokenizer(europeanDate, "-");
        try
        {
            int day = Integer.parseInt(st.nextToken());
            int month = Integer.parseInt(st.nextToken());
            int year = Integer.parseInt(st.nextToken());
            if(month<1 || month>12)
            {
                return null; 
            }
            
            calendar_.setTime(new Date(System.currentTimeMillis()));
            calendar_.set(Calendar.DAY_OF_MONTH, 1);
            calendar_.set(Calendar.YEAR, year);
            calendar_.set(Calendar.MONTH, month-1);
            calendar_.set(Calendar.MINUTE, 0);
            calendar_.set(Calendar.SECOND, 0);
            int amountOfDays = calendar_.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            
            if(day<1 || day>amountOfDays)
            {
                return null;
            }
            
            calendar_.set(Calendar.DAY_OF_MONTH, day);
            return calendar_.getTime();
        }
        catch(NoSuchElementException nsee)
        {
            return null;
        }
        catch(NumberFormatException nfe)
        {
            return null;
        }
    }
    
    public static String getEuropeanFormat(Date date)
    {
        if(date==null)
        return null;
        
        calendar_.setTime(date);
        String dd = ""+calendar_.get(Calendar.DAY_OF_MONTH);
        if(dd.length()==1)
        {
            dd = "0" + dd;
        }
        String mm = ""+(calendar_.get(Calendar.MONTH)+1);
        if(mm.length()==1)
        {
            mm = "0" + mm;
        }
        String yyyy = ""+calendar_.get(Calendar.YEAR);
        int yLength = yyyy.length();
        for(int i = yLength; i<4 ; i++)
        {
            yyyy = "0" + yyyy;
        }
        
        return dd + "-" + mm + "-" + yyyy;
    }
    
    public static boolean compareDates(Date date1, Date date2)
    {
        int d1d, d2d, d1m, d2m, d1y, d2y;
        calendar_.setTime(date1);
        d1d = calendar_.get(Calendar.DAY_OF_MONTH);
        d1m = calendar_.get(Calendar.MONTH)+1;
        d1y = calendar_.get(Calendar.YEAR);
        
        calendar_.setTime(date2);
        d2d = calendar_.get(Calendar.DAY_OF_MONTH);
        d2m = calendar_.get(Calendar.MONTH)+1;
        d2y = calendar_.get(Calendar.YEAR);
        
        if(d1d == d2d && d1m == d2m && d1y == d2y)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
