package net.sf.regadb.ui.datatable.testSettings;

import java.util.List;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;

public class ITestDataTable implements IDataTable<Test>
{
	private static String [] _colNames = {"dataTable.test.colName.description", "dataTable.test.colName.testType" };
	private static String[] filterVarNames_ = {"test.description", "test.testType.description" };

	private IFilter[] filters_ = new IFilter[2];

	private static boolean [] sortable_ = {true, true};
	private static int[] colWidths = {50,50};
	public String[] getColNames() 
	{
		return _colNames;
	}

	public List<Test> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending) 
	{
		return t.getTests(startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
	}

	public long getDataSetSize(Transaction t) 
	{
		return t.getTestCount(HibernateStringUtils.filterConstraintsQuery(this));
	}

	public String[] getFieldNames() 
	{
		return filterVarNames_;
	}

	public IFilter[] getFilters() 
	{
		return filters_;
	}

	public String[] getRowData(Test test) 
	{
		String [] row = new String[2];
        
        row[0] = test.getDescription();
        row[1] = test.getTestType().getDescription();
             
        return row;
	}

	public void init(Transaction t)
	{
		filters_[0] = new StringFilter();
        filters_[1] = new StringFilter();
	}

	public void selectAction(Test selectedItem) 
	{
		RegaDBMain.getApp().getTree().getTreeContent().testSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().testView.selectNode();
	}

	public boolean[] sortableFields()
	{
		return sortable_;
	}

	public int[] getColumnWidths() {
		return colWidths;
	}
}
