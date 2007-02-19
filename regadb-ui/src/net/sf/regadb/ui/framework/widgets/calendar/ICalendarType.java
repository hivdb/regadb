package net.sf.regadb.ui.framework.widgets.calendar;

import java.util.Date;

import net.sf.witty.wt.i8n.WMessage;

public interface ICalendarType 
{
	public WMessage[] getWeekDays();
	public WMessage[] getMonths();
	public int getFirstDayOfMonthIndex(Date currentDate);
	public int getMaxAmountOfDaysInMonth();
	public int getAmountOfDaysInMonth(Date currentDate);
	public int getAmountOfDaysInPreviousMonth(Date currentDate);
	public Date getDate(int mm,int yyyy);
	public int getMonth(Date date);
	public int getYear(Date date);
}
