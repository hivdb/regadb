package net.sf.regadb.ui.datatable.datasetSettings;

import java.util.List;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.DateFilter;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;

public class IDatasetDataTable implements IDataTable<Dataset> 
{
	private static String [] _colNames = {"dataTable.dataset.colName.description", 										 
										  "dataTable.dataset.colName.creationDate",
										  "dataTable.dataset.colName.closedDate", 
										  "dataTable.dataset.colName.revision"};
	private static String[] filterVarNames_ = {"dataset.description", 
											   "dataset.creationDate",
											   "dataset.closedDate",
											   "dataset.revision"};
	private IFilter[] filters_ = new IFilter[4];

	private static boolean [] sortable_ = {true, true,true,true};

	public String[] getColNames() 
	{
		return _colNames;
	}

	public List<Dataset> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending) 
	{
		return t.getDatasets(startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
	}

	public long getDataSetSize(Transaction t)
	{
		return t.getDatasetCount(HibernateStringUtils.filterConstraintsQuery(this));
	}

	public String[] getFieldNames() 
	{
		return filterVarNames_;
	}

	public IFilter[] getFilters() 
	{
		return filters_;
	}

	public String[] getRowData(Dataset dataset) 
	{
		String [] row = new String[4];
        
        row[0] = dataset.getDescription();
        row[1] = String.valueOf(dataset.getCreationDate());
        row[2] = String.valueOf(dataset.getClosedDate());
        row[3] = String.valueOf(dataset.getRevision());
             
        return row;
	}

	public void init(Transaction t) 
	{
		filters_[0] = new StringFilter();
        filters_[1] = new DateFilter();
        filters_[2] = new DateFilter();
        filters_[3] = new StringFilter();
	}

	public void selectAction(Dataset selectedItem) 
	{
		RegaDBMain.getApp().getTree().getTreeContent().datasetSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().datasetSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().datasetSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().datasetView.selectNode();
	}

	public boolean[] sortableFields() 
	{
		return sortable_;
	}

	public boolean stillExists(Dataset selectedItem) 
	{
		Transaction trans = RegaDBMain.getApp().createTransaction();
        boolean state = trans.datasetStillExists(selectedItem);
        trans.commit();
        return state;
	}
}