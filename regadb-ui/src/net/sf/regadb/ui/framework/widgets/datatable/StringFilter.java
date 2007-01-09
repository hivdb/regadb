package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WText;

public class StringFilter extends WContainerWidget implements IFilter 
{
	private WText label_;
	private FilterOperatorCombo combo_;
	private FilterTF tf_;
	
	public StringFilter(WMessage label)
	{
		label_ = new WText(label, this);
		tf_ = new FilterTF(null, this);
		combo_ = new FilterOperatorCombo(tf_, this);
		
		setInline(false);
		
		//filling of the combo-box with operators
		combo_.addItem(tr("dataTable.filter.listFilter.noFilter"));
		
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
			return varName + "like '" + tf_.text() + "%'"; 
		}
		else if(operator.equals("dataTable.filter.stringFilter.endsWith"))
		{
			return varName + "like '%" + tf_.text() + "'"; 
		}
		else if(operator.equals("dataTable.filter.stringFilter.contains"))
		{
			return varName + "like '%" + tf_.text() + "%'"; 
		}
		else if(operator.equals("dataTable.filter.stringFilter.sqlRegExp"))
		{
			return varName + "like '" + tf_.text() + "'"; 
		}
		
		return null;
	}
	
}
