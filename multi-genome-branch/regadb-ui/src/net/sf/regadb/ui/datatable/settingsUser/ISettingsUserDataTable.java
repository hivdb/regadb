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
        WString.tr("dataTable.settingsUser.colName.role")};
    
    private static String[] filterVarNames_ = { "settingsUser.uid", "settingsUser.firstName", "settingsUser.lastName", "settingsUser.email", "settingsUser.role"};
        
    private static boolean [] sortable_ = {true, true, true, true, true};
    private static int[] colWidths = {15,15,15,35,20};
    private IFilter[] filters_ = new IFilter[5];
    
    public ISettingsUserDataTable()
    {
    }
    
    public CharSequence[] getColNames()
    {
        return _colNames;
    }

    public List<SettingsUser> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean ascending)
    {
        return t.getSettingsUsers(startIndex, amountOfRows, filterVarNames_[sortIndex], ascending, HibernateStringUtils.filterConstraintsQuery(this));
    }

    public long getDataSetSize(Transaction t)
    {
        return t.getSettingsUsersCount(HibernateStringUtils.filterConstraintsQuery(this));
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
        String [] row = new String[5];
        
        row[0] = type.getUid();
        row[1] = type.getFirstName();
        row[2] = type.getLastName();
        row[3] = type.getEmail();
        row[4] = type.getRole() == null ? "not yet activated":type.getRole();
        
        return row;
    }

    public void init(Transaction t)
    {
        filters_[0] = new StringFilter();
        filters_[1] = new StringFilter();
        filters_[2] = new StringFilter();
        filters_[3] = new StringFilter();
        filters_[4] = new StringFilter();
    }

    public void selectAction(SettingsUser selectedItem)
    {
    	RegaDBMain.getApp().getTree().getTreeContent().userSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().userSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().userSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().usersView.selectNode();
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
