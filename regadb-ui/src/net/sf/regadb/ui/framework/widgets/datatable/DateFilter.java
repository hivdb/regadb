package net.sf.regadb.ui.framework.widgets.datatable;

import java.util.Date;

import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.validation.WEuropeanDateValidator;

public class DateFilter extends WContainerWidget implements IFilter 
{
	private FilterOperatorCombo combo_;
	private FilterTF tf1_;
	private WImage calendarIcon1 = new WImage("pics/calendar.png");
	private FilterTF tf2_;
	private WImage calendarIcon2 = new WImage("pics/calendar.png");
	
	public final static String equals_ = "dataTable.filter.dateFilter.equals";
	public final static String before_ = "dataTable.filter.dateFilter.before";
	public final static String after_ = "dataTable.filter.dateFilter.after";
	public final static String between_ = "dataTable.filter.dateFilter.between";
	
	public DateFilter()
	{
		tf1_ = new FilterTF(new WEuropeanDateValidator());
		//tf1_.setInline(false);
		tf2_ = new FilterTF(new WEuropeanDateValidator());
		//tf2_.setInline(false);
		tf2_.setEnabled(false);
		combo_ = new FilterOperatorCombo(tf1_);
		combo_.setInline(false);
		
		addWidget(combo_);
		WContainerWidget w1 = new WContainerWidget();
		w1.addWidget(tf1_);
		w1.addWidget(calendarIcon1);
		w1.setInline(false);
		addWidget(w1);
		WContainerWidget w2 = new WContainerWidget();
		w2.addWidget(tf2_);
		w2.addWidget(calendarIcon2);
		w2.setInline(false);
		addWidget(w2);
		
		//filling of the combo-box with operators		
		combo_.addItem(tr(equals_));
		combo_.addItem(tr(before_));
		combo_.addItem(tr(after_));
		combo_.addItem(tr(between_));
		
		combo_.changed.addListener(new SignalListener<WEmptyEvent>()
				{
					public void notify(WEmptyEvent a)
					{
						tf1_.setText("");
						tf1_.setText("");
						
						tf2_.setEnabled(combo_.currentText().key().equals(between_));
					}
				});
	}
	
	public WContainerWidget getFilterWidget()
	{
		return this;
	}
	
	public Date getFirstDate()
	{
		return DateUtils.parserEuropeanDate(tf1_.text());
	}
	
	public Date getSecondDate()
	{
		return DateUtils.parserEuropeanDate(tf2_.text());
	}
	
	public WMessage getComboState()
	{
		return combo_.currentText();
	}
}
