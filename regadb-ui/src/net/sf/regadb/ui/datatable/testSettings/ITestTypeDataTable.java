package net.sf.regadb.ui.datatable.testSettings;

import java.util.List;

import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import eu.webtoolkit.jwt.WString;

public class ITestTypeDataTable implements IDataTable<TestType>
{
    private static WString [] _colNames = {
        WString.tr("dataTable.testType.colName.description"),
        WString.tr("dataTable.testType.colName.organismName"),
        WString.tr("dataTable.testType.colName.testObject"),
        WString.tr("dataTable.testType.colName.valueType")};
    
    private static String[] filterVarNames_ = { "testType.description", "case when genome is null then '' else genome.organismName end", "testType.testObject.description", "testType.valueType.description"};
    
    private IFilter[] filters_ = new IFilter[4];
    private static int[] colWidths = {30,10,30,30};
    private static boolean [] sortable_ = {true, true, true, true};
    
    public CharSequence[] getColNames()
    {
        return _colNames;
    }

    public List<TestType> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending)
    {
        return t.getTestTypes(startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
    }

    public long getDataSetSize(Transaction t)
    {
        return t.getTestTypeCount(HibernateStringUtils.filterConstraintsQuery(this));
    }

    public String[] getFieldNames()
    {
        return filterVarNames_;
    }

    public IFilter[] getFilters()
    {
        return filters_;
    }

    public String[] getRowData(TestType testType)
    {
        String [] row = new String[4];
        
        row[0] = testType.getDescription();
        row[1] = (testType.getGenome() == null ? "":testType.getGenome().getOrganismName());
        row[2] = testType.getTestObject().getDescription();
        row[3] = testType.getValueType().getDescription();
        
      
        return row;
    }

    public void init(Transaction t)
    {
        filters_[0] = new StringFilter();
        filters_[1] = new StringFilter();
        filters_[2] = new StringFilter();
        filters_[3] = new StringFilter();
    }

    public void selectAction(TestType selectedItem)
    {
        RegaDBMain.getApp().getTree().getTreeContent().testTypeSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().testTypeSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().testTypeSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().testTypesView.selectNode();
    }

    public boolean[] sortableFields()
    {
        return sortable_;
    }

	public int[] getColumnWidths() {
		return colWidths;
	}

	public String[] getRowTooltips(TestType type) {
		return null;
	}
}
