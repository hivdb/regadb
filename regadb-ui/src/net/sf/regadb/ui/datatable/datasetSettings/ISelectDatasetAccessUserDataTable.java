package net.sf.regadb.ui.datatable.datasetSettings;

import java.util.List;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;

public class ISelectDatasetAccessUserDataTable implements IDataTable<SettingsUser>
{
    private static String [] _colNames = {"dataTable.settingsUser.colName.uid", "dataTable.settingsUser.colName.firstname","dataTable.settingsUser.colName.lastname"};
    
    private static String[] filterVarNames_ = { "settingsUser.uid", "settingsUser.firstName", "settingsUser.lastName"};
        
    private static boolean [] sortable_ = {true, true, true};
    
    private IFilter[] filters_ = new IFilter[3];
    
    public ISelectDatasetAccessUserDataTable()
    {
        
    }
    
    public String[] getColNames()
    {
        return _colNames;
    }

    public List<SettingsUser> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean ascending)
    {
        return t.getUsersWhitoutLoggedin(startIndex, amountOfRows, filterVarNames_[sortIndex], HibernateStringUtils.filterConstraintsQuery(this), RegaDBMain.getApp().getLogin().getUid());
    }

    public long getDataSetSize(Transaction t)
    {
        return t.getSettingsUserCount(HibernateStringUtils.filterConstraintsQuery(this));
    }
    
    public String[] getFieldNames()
    {
        return filterVarNames_;
    }

    public IFilter[] getFilters()
    {
        return filters_;
    }

    public String[] getRowData(SettingsUser type)
    {
        String [] row = new String[3];
        
        row[0] = type.getUid();
        row[1] = type.getFirstName();
        row[2] = type.getLastName();
        
        return row;
    }

    public void init(Transaction t)
    {
        filters_[0] = new StringFilter();
        filters_[1] = new StringFilter();
        filters_[2] = new StringFilter();
    }

    public void selectAction(SettingsUser selectedItem)
    {
        RegaDBMain.getApp().getTree().getTreeContent().datasetAccessSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().datasetAccessSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().datasetAccessSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().datasetAccessView.selectNode();
    }

    public boolean[] sortableFields()
    {
        return sortable_;
    }
}
