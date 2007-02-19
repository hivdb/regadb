package net.sf.regadb.ui.framework.widgets.calendar;
import java.util.Date;

import net.sf.regadb.ui.forms.login.LoginForm;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WComboBox;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.WText;
import net.sf.witty.wt.widgets.event.WEmptyEvent;

public class Calendar extends WContainerWidget
{
	private WContainerWidget comboControls_;
	private WComboBox monthCombo_;
	private WComboBox yearCombo_;
	private boolean showPrevious_;
	private WTable calendarTable_;

	private ICalendarType calendarType_;
	
	public Calendar(WContainerWidget parent, int startYear, int endYear, boolean showPrevious, Date startDate)
	{
		super(parent);
		comboControls_ = new WContainerWidget(this);
		calendarType_ = new GregorianCalendarType();
		
		createComboControls(startYear, endYear);

		calendarTable_ = new WTable(this);
		createCalendarTable();
		showPrevious_=showPrevious;
		fillCalendarTable(startDate);
	}
	
	//Creates a calendar with the todays data as starting point
	public Calendar(WContainerWidget parent, int startYear, int endYear)
	{
		this(parent, startYear, endYear, true, new Date(System.currentTimeMillis()));
	}

	public Calendar(WContainerWidget parent, int startYear, int endYear, boolean showPrevious) 
	{
		this(parent, startYear, endYear, showPrevious, new Date(System.currentTimeMillis()));
	}

	private void createComboControls(int startYear, int endYear)
	{
		monthCombo_ = new WComboBox(comboControls_);
		
		for(WMessage month : calendarType_.getMonths())
		{
			monthCombo_.addItem(month);
		}
		
		monthCombo_.setCurrentIndex(calendarType_.getMonth(new Date(System.currentTimeMillis())));
		monthCombo_.changed.addListener(new SignalListener<WEmptyEvent>()
				{
					public void notify(WEmptyEvent a) 
					{
						refreshCalendar();
					}
				});
		
		yearCombo_ = new WComboBox(comboControls_);
		for (int i = startYear; i <= endYear; i++)
		{
			yearCombo_.addItem(lt(i + ""));
		
		}
		yearCombo_.setCurrentIndex(calendarType_.getYear(new Date(System.currentTimeMillis())));
		yearCombo_.changed.addListener (new SignalListener<WEmptyEvent>()
		{

			public void notify(WEmptyEvent a) 
			{
				refreshCalendar();
				
			}
			
		});
	}

	protected void refreshCalendar() 
	{
		int selectedMonth, selectedYear;
		selectedMonth=monthCombo_.currentIndex();
		selectedYear=Integer.parseInt(yearCombo_.currentText().keyOrValue());
		Date selectedDate=calendarType_.getDate(selectedMonth,selectedYear);
		fillCalendarTable(selectedDate);
	}

	private void createCalendarTable()
	{
		int row = 0;
		int col = 0;

		for(WMessage dayMsg : calendarType_.getWeekDays())
		{
			createDayNameCell(row, col, dayMsg);
			col++;
		}

		row++;
		col = 0;

		int amountOfWeekDays = calendarType_.getWeekDays().length;
		int maxOfWeekLines = calendarType_.getMaxAmountOfDaysInMonth()/amountOfWeekDays + 3;
		
		for (; row < maxOfWeekLines; row++)
		{
			for (col = 0; col < amountOfWeekDays; col++)
			{
				createDayCell(row, col);
			}
		}
	}

	private void createDayNameCell(int row, int col, WMessage dayName)
	{
		calendarTable_.elementAt(row, col).addWidget(new WText(dayName));
	}

	private void createDayCell(int row, int col)
	{
		calendarTable_.elementAt(row, col).addWidget(new WText());
	}

	private void fillCalendarTable(Date date)
	{
		int amountOfWeekDays = calendarType_.getWeekDays().length;
		int maxOfWeekLines = calendarType_.getMaxAmountOfDaysInMonth()/amountOfWeekDays + 2;
		int firstDayOfMonthPosition = calendarType_.getFirstDayOfMonthIndex(date);
		int amountOfDaysInThisMonth =calendarType_.getAmountOfDaysInMonth(date);
		int amountOfDaysInPreviousMonth = calendarType_.getAmountOfDaysInPreviousMonth(date);
		
		
		int row=1;
		if (showPrevious_)
		{
			for(int col = 0; col < firstDayOfMonthPosition; col++)
			{
				WText calendarField = (WText) calendarTable_.elementAt(row, col).children().get(0);
				calendarField.setText(lt(amountOfDaysInPreviousMonth-firstDayOfMonthPosition+col+1+""));
			}	
		}
		
		
		int startDate=1;
		
		int col = firstDayOfMonthPosition;
		boolean firstRoundUp = true;
		boolean breaking=false;
		for (; row < maxOfWeekLines+1; row++)
		{
			if(breaking)
			{
			break;	
			}
			if(firstRoundUp)
			{
				firstRoundUp = false;
			}
			else
			{
				col = 0;
			}
			for(; col<amountOfWeekDays; col++,startDate++)
			{
				if(!showPrevious_ && startDate>amountOfDaysInThisMonth)
				{
				breaking=true;	
				break;
				}
				if(startDate>amountOfDaysInThisMonth)
				{
					startDate = 1;
				}
				WText calendarField = (WText) calendarTable_.elementAt(row, col).children().get(0);
				calendarField.setText(lt(startDate + ""));
			}
		}
	}

	public ICalendarType getCalendarType() 
	{
		return calendarType_;
	}

	public void setCalendarType(ICalendarType calendarType) 
	{
		calendarType_ = calendarType;
	}
}