package net.sf.regadb.ui.datatable.query;

import java.util.List;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;

public class ISelectQueryDefinitionDataTable implements IDataTable<QueryDefinition>
{
    private static String [] _colNames = {"dataTable.queryDefinition.colName.name", "dataTable.queryDefinition.colName.description", "dataTable.queryDefinition.colName.uid"};
    
    private static String[] filterVarNames_ = {"queryDefinition.name", "queryDefinition.description", "queryDefinition.settingsUser.uid"};
        
    private static boolean [] sortable_ = {true, true, true};
    
    private IFilter[] filters_ = new IFilter[3];
    
    public String[] getColNames()
    {
        return _colNames;
    }

    public List<QueryDefinition> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean ascending)
    {
        return t.getQueryDefinitions(startIndex, amountOfRows, filterVarNames_[sortIndex], HibernateStringUtils.filterConstraintsQuery(this), ascending);
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

    public void selectAction(QueryDefinition selectedItem)
    {
    	RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelectedView.selectNode();
    }

    public boolean[] sortableFields()
    {
        return sortable_;
    }
}
