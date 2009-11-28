package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.regadb.util.pair.Pair;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDate;
import eu.webtoolkit.jwt.WDatePicker;
import eu.webtoolkit.jwt.WDateValidator;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WString;

public class DateFilter extends WContainerWidget implements IFilter 
{
	private FilterOperatorCombo combo;
	private FilterTF tf1;
	private WImage calendarIcon1 = new WImage("pics/calendar.png");
	private FilterTF tf2;
	private WImage calendarIcon2 = new WImage("pics/calendar.png");
	
	public final static String equals = "dataTable.filter.dateFilter.equals";
	public final static String before = "dataTable.filter.dateFilter.before";
	public final static String after = "dataTable.filter.dateFilter.after";
	public final static String between = "dataTable.filter.dateFilter.between";
	
	private String dateFormat;
	
	/**
	 * Constructor, accepting a dateFormat, which the DateFilter will use to do the validation.
	 * DateFilter allows to filter dates, by equals, greater then, smaller then, and between constraints.
	 * 
	 * @param dateFormat
	 */
	public DateFilter(String dateFormat)
	{
		this.dateFormat = dateFormat;
		
		setDateField1(new FilterTF(new WDateValidator(dateFormat)){
			public void setEnabled(boolean enabled) {
				super.setEnabled(enabled);
				calendarIcon1.setHidden(!enabled);
			}
		});
		setDateField2(new FilterTF(new WDateValidator(dateFormat)){
			public void setEnabled(boolean enabled) {
				super.setEnabled(enabled);
				calendarIcon2.setHidden(!enabled);
			}
		});

		getDateField1().setEnabled(false);
		getDateField2().setEnabled(false);

		combo = new FilterOperatorCombo(getDateField1());
		combo.setInline(false);
		
		WContainerWidget div;
		WDatePicker dp;

		div = new WContainerWidget(this);
		div.addWidget(combo);

		div = new WContainerWidget(this);
		div.addWidget(getDateField1());
		div.addWidget(dp = new WDatePicker(calendarIcon1, getDateField1(), false));
		dp.setFormat(dateFormat);
		dp.getCalendar().selectionChanged().addListener(this, new Signal.Listener()
		{
			public void trigger() 
			{
				FilterTools.findDataTable(tf1).applyFilter();
			}
		});


		div = new WContainerWidget(this);
		div.addWidget(getDateField2());
		div.addWidget(dp = new WDatePicker(calendarIcon2, getDateField2(), false));
		dp.setFormat(dateFormat);
		dp.getCalendar().selectionChanged().addListener(this, new Signal.Listener()
		{
			public void trigger() 
			{
				FilterTools.findDataTable(tf1).applyFilter();
			}
		});

		setStyleClass("datefield");
		
		//filling of the combo-box with operators		
		combo.addItem(tr(equals));
		combo.addItem(tr(before));
		combo.addItem(tr(after));
		combo.addItem(tr(between));
		
		combo.changed().addListener(this, new Signal.Listener() {
			public void trigger()
			{
				getDateField1().setEnabled(combo.getCurrentIndex() > 0);
				getDateField2().setEnabled(combo.getCurrentText().getKey().equals(between));
			}
		});
	}
	
	/**
	 * Returns this {@link WContainerWidget}
	 * 
	 * @see nl.rivm.mpf.ui.framework.forms.datatable.IFilter#getFilterWidget()
	 */
	public WContainerWidget getFilterWidget()
	{
		return this;
	}
	
	public Object getFirstDate()
	{
		WDate d = WDate.fromString(getDateField1().getText(), dateFormat);
		return d == null ? null : d.getDate();
	}
	
	public Object getSecondDate()
	{
		WDate d = WDate.fromString(getDateField2().getText(), dateFormat);
		return d == null ? null : d.getDate();
	}
	
	public WString getComboState()
	{
		return combo.getCurrentText();
	}

	private void setDateField1(FilterTF tf1) {
        this.tf1 = tf1;
    }

	FilterTF getDateField1() {
        return tf1;
    }

	private void setDateField2(FilterTF tf2) {
        this.tf2 = tf2;
    }

	FilterTF getDateField2() {
        return tf2;
    }

	public HibernateFilterConstraint getConstraint(String varName, int filterIndex) {
		HibernateFilterConstraint constraint = new HibernateFilterConstraint();
		
		String operator = getComboState().getKey();
			
		if(operator.equals(DateFilter.equals))
		{
			constraint.clause_ = varName + " = :param" + filterIndex;
			constraint.arguments_.add(new Pair<String, Object>("param" + filterIndex, getFirstDate()));
		}
		else if(operator.equals(DateFilter.before))
		{
			constraint.clause_ = varName + " < :param" + filterIndex;
			constraint.arguments_.add(new Pair<String, Object>("param" + filterIndex, getFirstDate()));
		}
		else if(operator.equals(DateFilter.after))
		{
			constraint.clause_ = varName + " > :param" + filterIndex;
			constraint.arguments_.add(new Pair<String, Object>("param" + filterIndex, getFirstDate()));
		}
		else if(operator.equals(DateFilter.between))
		{
			constraint.clause_ = varName + " between :paramA" + filterIndex + " and :paramB" + filterIndex;
			constraint.arguments_.add(new Pair<String, Object>("paramA" + filterIndex, getFirstDate()));
			constraint.arguments_.add(new Pair<String, Object>("paramB" + filterIndex, getSecondDate()));
		}
			
		return constraint;
	}
}