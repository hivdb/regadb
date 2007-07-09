package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.regadb.db.Transaction;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.i8n.WMessage;

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
		
		combo_.changed.addListener(new SignalListener<WEmptyEvent>()
				{
					public void notify(WEmptyEvent a)
					{
						FilterTools.findDataTable(combo_).applyFilter();
					}
				});
	}

	public WContainerWidget getFilterWidget()
	{
		return this;
	}

	public abstract void setComboBox(WComboBox combo);

	public Transaction getTransaction()
	{
		return transaction_;
	}
	
	/*
	 * Returns the selected value,
	 * if nofilter is selected null is returned.
	 */
	public WMessage getComboValue()
	{
		if(combo_.currentIndex()==0)
			return null;
		else
			return combo_.currentText();
	}
}