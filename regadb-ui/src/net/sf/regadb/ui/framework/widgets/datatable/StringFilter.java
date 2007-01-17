package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.witty.wt.widgets.WContainerWidget;

public class StringFilter extends WContainerWidget implements IFilter 
{
	private FilterOperatorCombo combo_;
	private FilterTF tf_;
	
	public StringFilter()
	{
		tf_ = new FilterTF(null);
		tf_.setInline(false);
		combo_ = new FilterOperatorCombo(tf_);
		combo_.setInline(false);
		
		addWidget(combo_);
		addWidget(tf_);
		
		//filling of the combo-box with operators		
		combo_.addItem(tr("dataTable.filter.stringFilter.beginsWith"));
		combo_.addItem(tr("dataTable.filter.stringFilter.endsWith"));
		combo_.addItem(tr("dataTable.filter.stringFilter.contains"));
		combo_.addItem(tr("dataTable.filter.stringFilter.sqlRegExp"));
	}
	
	public WContainerWidget getFilterWidget()
	{
		return this;
	}

	public String getHibernateString(String varName)
	{		
		String operator = combo_.currentText().key();
		
		if(operator.equals("dataTable.filter.stringFilter.beginsWith"))
		{
			return varName + " like '" + tf_.text() + "%'"; 
		}
		else if(operator.equals("dataTable.filter.stringFilter.endsWith"))
		{
			return varName + " like '%" + tf_.text() + "'"; 
		}
		else if(operator.equals("dataTable.filter.stringFilter.contains"))
		{
			return varName + " like '%" + tf_.text() + "%'"; 
		}
		else if(operator.equals("dataTable.filter.stringFilter.sqlRegExp"))
		{
			return varName + " like '" + tf_.text() + "'"; 
		}
		
		return null;
	}
	
	public void setVisible(boolean vis)
	{
		combo_.setHidden(!vis);
		tf_.setHidden(!vis);
	}
	
	public boolean isVisible()
	{
		return !combo_.isHidden();
	}
}
