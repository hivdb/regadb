package net.sf.regadb.ui.datatable.patient;

import java.util.List;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.widgets.datatable.ListFilter;
import net.sf.witty.wt.WComboBox;

public class DatasetFilter extends ListFilter
{
	public DatasetFilter(Transaction transaction)
	{
		super(transaction);
	}
	
	@Override
	public void setComboBox(WComboBox combo)
	{
		List<Dataset> datasets = getTransaction().getCurrentUsersDatasets();
		for(Dataset ds : datasets )
		{
			combo.addItem(lt(ds.getDescription()));
		}
	}
}
