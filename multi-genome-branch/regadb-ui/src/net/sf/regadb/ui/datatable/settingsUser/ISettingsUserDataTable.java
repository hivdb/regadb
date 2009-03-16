package net.sf.regadb.ui.datatable.settingsUser;

import java.util.List;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import eu.webtoolkit.jwt.WString;

public class ISettingsUserDataTable implements IDataTable<SettingsUser>
{
    private static WString [] _colNames = {
        WString.tr("dataTable.settingsUser.colName.uid"),
        WString.tr("dataTable.settingsUser.colName.firstname"),
        WString.tr("dataTable.settingsUser.colName.lastname"), 
        WString.tr("dataTable.settingsUser.colName.email"),
        WString.tr("dataTable.settingsUser.colName.admin"),
        WString.tr("dataTable.settingsUser.colName.enabled")};
    
    private static String[] filterVarNames_ = { "settingsUser.uid", "settingsUser.firstName", "settingsUser.lastName", "settingsUser.email", "settingsUser.admin", "settingsUser.enabled"};
        
    private static boolean [] sortable_ = {true, true, true, true, true, true};
    private static int[] colWidths = {15,15,15,35,10,10};
    private IFilter[] filters_ = new IFilter[6];
    
    private boolean enabledUsers_;
    
    public ISettingsUserDataTable(boolean enabledUsers)
    {
        enabledUsers_ = enabledUsers;
    }
    
    public WString[] getColNames()
    {
        return _colNames;
    }

    public List<SettingsUser> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean ascending)
    {
        return t.getUsersByEnabled(startIndex, amountOfRows, filterVarNames_[sortIndex], ascending, enabledUsers_, HibernateStringUtils.filterConstraintsQuery(this));
    }

    public long getDataSetSize(Transaction t)
    {
        return t.getSettingsUserCountByEnabled(HibernateStringUtils.filterConstraintsQuery(this), enabledUsers_);
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
        String [] row = new String[6];
        
        row[0] = type.getUid();
        row[1] = type.getFirstName();
        row[2] = type.getLastName();
        row[3] = type.getEmail();
        row[4] = type.getAdmin() + "";
        row[5] = type.getEnabled() == null ? "not yet activated":type.getEnabled() + "";
        
        return row;
    }

    public void init(Transaction t)
    {
        filters_[0] = new StringFilter();
        filters_[1] = new StringFilter();
        filters_[2] = new StringFilter();
        filters_[3] = new StringFilter();
        filters_[4] = null;
        filters_[5] = null;
    }

    public void selectAction(SettingsUser selectedItem)
    {
        if(enabledUsers_)
        {
            RegaDBMain.getApp().getTree().getTreeContent().registeredUserSelected.setSelectedItem(selectedItem);
            RegaDBMain.getApp().getTree().getTreeContent().registeredUserSelected.expand();
            RegaDBMain.getApp().getTree().getTreeContent().registeredUserSelected.refreshAllChildren();
            RegaDBMain.getApp().getTree().getTreeContent().registeredUsersView.selectNode();
        }
        else
        {
            RegaDBMain.getApp().getTree().getTreeContent().notRegisteredUserSelected.setSelectedItem(selectedItem);
            RegaDBMain.getApp().getTree().getTreeContent().notRegisteredUserSelected.expand();
            RegaDBMain.getApp().getTree().getTreeContent().notRegisteredUserSelected.refreshAllChildren();
            RegaDBMain.getApp().getTree().getTreeContent().notRegisteredUsersView.selectNode();
        }
    }

    public boolean[] sortableFields()
    {
        return sortable_;
    }

	public int[] getColumnWidths() {
		return colWidths;
	}

	public String[] getRowTooltips(SettingsUser type) {
		return null;
	}
}
