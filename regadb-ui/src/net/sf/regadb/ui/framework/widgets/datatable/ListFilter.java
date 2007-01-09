package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.regadb.db.Transaction;
import net.sf.witty.wt.widgets.WComboBox;
import net.sf.witty.wt.widgets.WContainerWidget;

public abstract class ListFilter extends WContainerWidget implements IFilter 
{
	private WComboBox combo_;
	private Transaction transaction_;
	
	public ListFilter(Transaction transaction)
	{
		super();
		
		transaction_ = transaction;
		
		combo_ = new WComboBox(this);
		combo_.addItem(tr("dataTable.filter.listFilter.noFilter"));
		
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

	public Transaction getTransaction()
	{
		return transaction_;
	}
}