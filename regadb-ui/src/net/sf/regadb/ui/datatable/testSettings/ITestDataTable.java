package net.sf.regadb.ui.datatable.testSettings;

import java.util.List;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import eu.webtoolkit.jwt.WString;

public class ITestDataTable implements IDataTable<Test>
{
	private static WString [] _colNames = {
	    WString.tr("dataTable.test.colName.description"),
	    WString.tr("dataTable.test.colName.testType"),
	    WString.tr("dataTable.test.colName.genome") };
	
	private static String[] filterVarNames_ = {"test.description", "test.testType.description", "case when genome is null then '' else genome.organismName end" };

	private IFilter[] filters_ = new IFilter[3];

	private static boolean [] sortable_ = {true, true, true};
	private static int[] colWidths = {45,45,10};
	public CharSequence[] getColNames() 
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
		String [] row = new String[3];
        
        row[0] = test.getDescription();
        row[1] = test.getTestType().getDescription();
        row[2] = (test.getTestType().getGenome() == null ? "":test.getTestType().getGenome().getOrganismName());
             
        return row;
	}

	public void init(Transaction t)
	{
		filters_[0] = new StringFilter();
        filters_[1] = new StringFilter();
        filters_[2] = new StringFilter();
	}

	public void selectAction(Test selectedItem) 
	{
		RegaDBMain.getApp().getTree().getTreeContent().testSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.refresh();
        RegaDBMain.getApp().getTree().getTreeContent().testView.selectNode();
	}

	public boolean[] sortableFields()
	{
		return sortable_;
	}

	public int[] getColumnWidths() {
		return colWidths;
	}

	public String[] getRowTooltips(Test type) {
		return null;
	}
}
