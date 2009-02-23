package net.sf.regadb.ui.datatable.query;

import java.util.List;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;

public abstract class ISelectQueryDefinitionDataTable implements IDataTable<QueryDefinition>
{
	
    private static String [] _colNames = {"dataTable.queryDefinition.colName.name", "dataTable.queryDefinition.colName.description", "dataTable.queryDefinition.colName.uid"};
    
    private static String[] filterVarNames_ = {"queryDefinition.name", "queryDefinition.description", "queryDefinition.settingsUser.uid"};
        
    private static boolean [] sortable_ = {true, true, true};
    private static int[] colWidths = {30,50,20};
    private IFilter[] filters_ = new IFilter[3];
    
    public String[] getColNames()
    {
        return _colNames;
    }

    public List<QueryDefinition> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean ascending)
    {
        return t.getQueryDefinitions(startIndex, amountOfRows, filterVarNames_[sortIndex], HibernateStringUtils.filterConstraintsQuery(this), ascending, getQueryType());
    }

    public long getDataSetSize(Transaction t)
    {
        return t.getQueryDefinitionCount(HibernateStringUtils.filterConstraintsQuery(this));
    }
    
    public String[] getFieldNames()
    {
        return filterVarNames_;
    }

    public IFilter[] getFilters()
    {
        return filters_;
    }

    public String[] getRowData(QueryDefinition queryDefinition)
    {
        String[] row = new String[3];
        
        row[0] = queryDefinition.getName();
        row[1] = queryDefinition.getDescription();
        row[2] = queryDefinition.getSettingsUser().getUid();
        
        return row;
    }

    public void init(Transaction t)
    {
        filters_[0] = new StringFilter();
        filters_[1] = new StringFilter();
        filters_[2] = new StringFilter();
    }

    public abstract void selectAction(QueryDefinition selectedItem);
    
    public abstract int getQueryType();

    public boolean[] sortableFields()
    {
        return sortable_;
    }

    public int[] getColumnWidths() {
		return colWidths;
	}
    
	public String[] getRowTooltips(QueryDefinition type) {
		return null;
	}
}
