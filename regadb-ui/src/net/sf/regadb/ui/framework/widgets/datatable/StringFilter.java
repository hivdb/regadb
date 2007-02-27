package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.witty.wt.widgets.WContainerWidget;

public class StringFilter extends WContainerWidget implements IFilter 
{
	private FilterOperatorCombo combo_;
	private FilterTF tf_;
	
	public final static String beginsWith_ = "dataTable.filter.stringFilter.beginsWith";
	public final static String endsWith_ = "dataTable.filter.stringFilter.endsWith";
	public final static String contains_ = "dataTable.filter.stringFilter.contains";
	public final static String sqlRegExp_ = "dataTable.filter.stringFilter.sqlRegExp";
	
	public StringFilter()
	{
		tf_ = new FilterTF(null);
		tf_.setInline(false);
		combo_ = new FilterOperatorCombo(tf_);
		combo_.setInline(false);
		
		addWidget(combo_);
		addWidget(tf_);
		
		//filling of the combo-box with operators		
		combo_.addItem(tr(beginsWith_));
		combo_.addItem(tr(endsWith_));
		combo_.addItem(tr(contains_));
		combo_.addItem(tr(sqlRegExp_));
	}
	
	public WContainerWidget getFilterWidget()
	{
		return this;
	}

	public String getHibernateString(String varName)
	{		
		String operator = combo_.currentText().key();
		
		if(operator.equals(beginsWith_))
		{
			return varName + " like '" + tf_.text() + "%'"; 
		}
		else if(operator.equals(endsWith_))
		{
			return varName + " like '%" + tf_.text() + "'"; 
		}
		else if(operator.equals(contains_))
		{
			return varName + " like '%" + tf_.text() + "%'"; 
		}
		else if(operator.equals(sqlRegExp_))
		{
			return varName + " like '" + tf_.text() + "'"; 
		}
		
		return null;
	}
}
