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
            calendar_.set(Calendar.MONTH, month);
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
        calendar_.setTime(date);
        String dd = ""+calendar_.get(Calendar.DAY_OF_MONTH);
        if(dd.length()==1)
        {
            dd = "0" + dd;
        }
        String mm = ""+calendar_.get(Calendar.MONTH);
        if(mm.length()==1)
        {
            mm = "0" + mm;
        }
        String yyyy = ""+calendar_.get(Calendar.MONTH);
        int yLength = yyyy.length();
        for(int i = yLength; i<4 ; i++)
        {
            yyyy = "0" + yyyy;
        }
        
        return dd + "-" + mm + "-" + yyyy;
    }
}
