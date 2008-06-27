package net.sf.regadb.ui.framework.widgets.calendar;

import java.util.Date;
import java.util.GregorianCalendar;

import net.sf.witty.wt.i8n.WMessage;

public class GregorianCalendarType implements ICalendarType
{
	private java.util.Calendar calendar_ = new GregorianCalendar();
	private WMessage [] weeks ;
	private WMessage [] months;
	
	public int getAmountOfDaysInMonth(Date currentDate) 
	{
		calendar_.setTime(currentDate);
		
		return calendar_.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
	}

	public int getFirstDayOfMonthIndex(Date currentDate) 
	{
		calendar_.setTime(currentDate);
		
		calendar_.set(java.util.Calendar.DAY_OF_MONTH, 1);
		
		int firstDayOfWeek = calendar_.get(java.util.Calendar.DAY_OF_WEEK);
		
		if ((firstDayOfWeek == java.util.Calendar.SUNDAY))
		{
			return 6;
		}
		else
		{
			return firstDayOfWeek-2;
		}
	}

	public int getMaxAmountOfDaysInMonth()
	{
		return 31;
	}

	public WMessage[] getMonths()
	{
		if(months==null)
		{
		months = new WMessage[12];
		months[0] = new WMessage("calendar.month.january");
		months[1] = new WMessage("calendar.month.february");
		months[2] = new WMessage("calendar.month.march");
		months[3] = new WMessage("calendar.month.april");
		months[4] = new WMessage("calendar.month.may");
		months[5] = new WMessage("calendar.month.june");
		months[6] = new WMessage("calendar.month.july");
		months[7] = new WMessage("calendar.month.august");
		months[8] = new WMessage("calendar.month.september");
		months[9] = new WMessage("calendar.month.october");
		months[10] = new WMessage("calendar.month.november");
		months[11] = new WMessage("calendar.month.december");
		}
		
		return months;
	}

	public WMessage[] getWeekDays() 
	{
		if(weeks==null)
		{
		weeks = new WMessage[7];
		weeks[0] = new WMessage("calendar.day.monday");
		weeks[1] = new WMessage("calendar.day.tuesday");
		weeks[2] = new WMessage("calendar.day.wednesday");
		weeks[3] = new WMessage("calendar.day.thursday");
		weeks[4] = new WMessage("calendar.day.friday");
		weeks[5] = new WMessage("calendar.day.saturday");
		weeks[6] = new WMessage("calendar.day.sunday");
		}
		
		return weeks;
	}

	public int getAmountOfDaysInPreviousMonth(Date currentDate) 
	{
		calendar_.setTime(currentDate);
		calendar_.add(java.util.Calendar.MONTH, -1);
		return getAmountOfDaysInMonth(calendar_.getTime()); 
	}

	public Date getDate(int mm, int yyyy) 
	{
		calendar_.set(java.util.Calendar.DAY_OF_MONTH, 1);
		calendar_.set(java.util.Calendar.MONTH, mm);
		calendar_.set(java.util.Calendar.YEAR, yyyy);
		return calendar_.getTime();
	}

	public int getMonth(Date date) 
	{
		calendar_.setTime(date);
		return calendar_.get(java.util.Calendar.MONTH);
	}

	public int getYear(Date date)
	{
		calendar_.setTime(date);
		return calendar_.get(java.util.Calendar.YEAR);
	}

	public int getYearIndex(Date date, int startYear)
	{
		
		return getYear(date)-startYear;
	}

	public Date getDate(int dd, int mm, int yyyy) 
	{
		calendar_.set(java.util.Calendar.DAY_OF_MONTH, dd);
		calendar_.set(java.util.Calendar.MONTH, mm);
		calendar_.set(java.util.Calendar.YEAR, yyyy);
		return calendar_.getTime();
	}
}
