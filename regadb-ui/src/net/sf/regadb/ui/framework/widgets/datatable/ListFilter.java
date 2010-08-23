package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.widgets.MyComboBox;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.regadb.util.pair.Pair;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WString;

public abstract class ListFilter extends WContainerWidget implements IFilter 
{
	private MyComboBox combo_;
	private Transaction transaction_;
	
	public ListFilter(){
	    super();
	}
	
	public ListFilter(Transaction transaction)
	{
		super();
		
		transaction_ = transaction;
		
		init();
	}
	
	public void setTransaction(Transaction transaction){
	    transaction_ = transaction;
	}
	
	public void init(){
	    combo_ = new MyComboBox(this);
        
        setComboBox(combo_);
        combo_.sort();
        combo_.insertItem(0, tr("dataTable.filter.listFilter.noFilter"));
        
        combo_.changed().addListener(this, new Signal.Listener()
                {
                    public void trigger()
                    {
                        FilterTools.findDataTable(combo_).applyFilter();
                    }
                });
	}

	public WContainerWidget getFilterWidget()
	{
		return this;
	}

	public abstract void setComboBox(MyComboBox combo);

	public Transaction getTransaction()
	{
		return transaction_;
	}
	
	/*
	 * Returns the selected value,
	 * if nofilter is selected null is returned.
	 */
	public WString getComboValue()
	{
		if(combo_.getCurrentIndex()==0)
			return null;
		else
			return combo_.getCurrentText();
	}
	
	public HibernateFilterConstraint getConstraint(String varName, int filterIndex) {
		HibernateFilterConstraint constraint = new HibernateFilterConstraint();
		
		WString message = getComboValue();
		if(message!=null)
		{
		constraint.clause_ = " " + varName+" = :param" + filterIndex;
		constraint.arguments_.add(new Pair<String, Object>("param" + filterIndex, message.getValue()));
		}
		
		return constraint;
	}
	
	public boolean isValid(){
		return true;
	}
}