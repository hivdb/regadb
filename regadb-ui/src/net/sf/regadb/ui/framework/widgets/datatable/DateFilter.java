package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WLengthUnit;
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
		setDateField1(new FilterTF(new WEuropeanDateValidator()));
		setDateField2(new FilterTF(new WEuropeanDateValidator()));
		getDateField2().setEnabled(false);
		combo_ = new FilterOperatorCombo(getDateField1());
		combo_.setInline(false);
		
		addWidget(combo_);
		WTable w1 = new WTable(this);
		w1.setStyleClass("date-field");
		w1.putElementAt(0, 0, getDateField1());
		w1.putElementAt(0,1, calendarIcon1);
		w1.putElementAt(1, 0, getDateField2());
		w1.putElementAt(1, 1, calendarIcon2);
        w1.elementAt(0, 1).resize(new WLength(24, WLengthUnit.Pixel), new WLength());
        w1.elementAt(1, 1).resize(new WLength(24, WLengthUnit.Pixel), new WLength());
		
		//filling of the combo-box with operators		
		combo_.addItem(tr(equals_));
		combo_.addItem(tr(before_));
		combo_.addItem(tr(after_));
		combo_.addItem(tr(between_));
		
		combo_.changed.addListener(new SignalListener<WEmptyEvent>()
				{
					public void notify(WEmptyEvent a)
					{
						getDateField1().setText("");
						getDateField1().setText("");
						
						getDateField2().setEnabled(combo_.currentText().key().equals(between_));
					}
				});
	}
	
	public WContainerWidget getFilterWidget()
	{
		return this;
	}
	
	public Object getFirstDate()
	{
		return DateUtils.parserEuropeanDate(getDateField1().text());
	}
	
	public Object getSecondDate()
	{
		return DateUtils.parserEuropeanDate(getDateField2().text());
	}
	
	public WMessage getComboState()
	{
		return combo_.currentText();
	}

    protected void setDateField1(FilterTF tf1_) {
        this.tf1_ = tf1_;
    }

    protected FilterTF getDateField1() {
        return tf1_;
    }

    protected void setDateField2(FilterTF tf2_) {
        this.tf2_ = tf2_;
    }

    protected FilterTF getDateField2() {
        return tf2_;
    }
}
