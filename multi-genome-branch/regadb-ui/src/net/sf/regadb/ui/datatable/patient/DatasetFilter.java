package net.sf.regadb.ui.datatable.patient;

import java.util.Set;

import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
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
		Set<DatasetAccess> datasets = getTransaction().getSettingsUser(RegaDBMain.getApp().getLogin().getUid()).getDatasetAccesses();
		for(DatasetAccess ds : datasets )
		{
			combo.addItem(lt(ds.getId().getDataset().getDescription()));
		}
	}
}