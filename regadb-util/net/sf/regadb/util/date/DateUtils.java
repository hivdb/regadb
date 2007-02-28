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
}
