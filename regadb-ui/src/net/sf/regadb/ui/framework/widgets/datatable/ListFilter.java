package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.witty.wt.widgets.WComboBox;
import net.sf.witty.wt.widgets.WContainerWidget;

public abstract class ListFilter extends WContainerWidget implements IFilter 
{
	private WComboBox combo_;
	
	public ListFilter()
	{
		super();
		
		combo_ = new WComboBox(this);
		setComboBox(combo_);
	}

	public WContainerWidget getFilterWidget()
	{
		return this;
	}

	public String getHibernateString(String varName)
	{
		if(combo_.currentIndex()==0)
			return null;
		else
			return "varName == " + combo_.currentText().value();
	}
	
	public abstract void setComboBox(WComboBox combo);
}
