package net.sf.regadb.ui.datatable.attributeSettings;

import java.util.List;

import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;

public class IAttributeGroupDataTable implements IDataTable<AttributeGroup>
{
    private static String [] _colNames = {"dataTable.attributeGroup.colName.groupName"};
    private static String[] filterVarNames_ = { "attributeGroup.groupName"};
    
    private IFilter[] filters_ = new IFilter[1];
    
    private static boolean [] sortable_ = {true};
    
    public String[] getColNames()
    {
        return _colNames;
    }

    public List<AttributeGroup> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending)
    {
        return t.getAttributeGroups(startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
    }

    public long getDataSetSize(Transaction t)
    {
        return t.getAttributeGroupCount(HibernateStringUtils.filterConstraintsQuery(this));
    }

    public String[] getFieldNames()
    {
        return filterVarNames_;
    }

    public IFilter[] getFilters()
    {
        return filters_;
    }

    public String[] getRowData(AttributeGroup attribute)
    {
        String [] row = new String[1];
        
        row[0] = attribute.getGroupName();
        
        return row;
    }

    public void init(Transaction t)
    {
        filters_[0] = new StringFilter();
    }

    public void selectAction(AttributeGroup selectedItem)
    {
        RegaDBMain.getApp().getTree().getTreeContent().attributeGroupsSelected.setSelectedAttributeGroup(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().attributeGroupsSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().attributeGroupsSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().attributeGroupsView.selectNode();
    }

    public boolean[] sortableFields()
    {
        return sortable_;
    }

    public boolean stillExists(AttributeGroup selectedItem)
    {
        Transaction trans = RegaDBMain.getApp().createTransaction();
        boolean state = trans.attributeGroupStillExists(selectedItem);
        trans.commit();
        return state;
    }
}
