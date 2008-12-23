package net.sf.regadb.ui.framework.widgets.datatable;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDate;
import eu.webtoolkit.jwt.WDateValidator;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;

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
	
	private String dateFormat;
	
	public DateFilter(String dateFormat)
	{
		this.dateFormat = dateFormat;
		
		setDateField1(new FilterTF(new WDateValidator(dateFormat)));
		setDateField2(new FilterTF(new WDateValidator(dateFormat)));
		getDateField2().setEnabled(false);
		combo_ = new FilterOperatorCombo(getDateField1());
		combo_.setInline(false);
		
		addWidget(combo_);
		WTable w1 = new WTable(this);
		w1.setStyleClass("date-field");
		w1.elementAt(0, 0).addWidget(getDateField1());
		w1.elementAt(0,1).addWidget(calendarIcon1);
		w1.elementAt(1, 0).addWidget( getDateField2());
		w1.elementAt(1, 1).addWidget( calendarIcon2);
        w1.elementAt(0, 1).resize(new WLength(24, WLength.Unit.Pixel), new WLength());
        w1.elementAt(1, 1).resize(new WLength(24, WLength.Unit.Pixel), new WLength());
		
		//filling of the combo-box with operators		
		combo_.addItem(tr(equals_));
		combo_.addItem(tr(before_));
		combo_.addItem(tr(after_));
		combo_.addItem(tr(between_));
		
		combo_.changed.addListener(this, new Signal.Listener()
				{
					public void trigger()
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
		return WDate.fromString(getDateField1().text(), dateFormat).getDate();
	}
	
	public Object getSecondDate()
	{
		return WDate.fromString(getDateField2().text(), dateFormat).getDate();
	}
	
	public WString getComboState()
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
