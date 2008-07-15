package net.sf.regadb.ui.datatable.attributeSettings;

import java.util.List;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;

public class IAttributeDataTable implements IDataTable<Attribute>
{
    private static String [] _colNames = {"dataTable.attribute.colName.name","dataTable.attribute.colName.group", "dataTable.attribute.colName.valueType"};
    private static String[] filterVarNames_ = { "attribute.name", "attribute.attributeGroup.groupName", "attribute.valueType.description"};
    
    private IFilter[] filters_ = new IFilter[3];
    
    private static boolean [] sortable_ = {true, true, true};
    
    public String[] getColNames()
    {
        return _colNames;
    }

    public List<Attribute> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending)
    {
        return t.getAttributes(startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
    }

    public long getDataSetSize(Transaction t)
    {
        return t.getAttributeCount(HibernateStringUtils.filterConstraintsQuery(this));
    }

    public String[] getFieldNames()
    {
        return filterVarNames_;
    }

    public IFilter[] getFilters()
    {
        return filters_;
    }

    public String[] getRowData(Attribute attribute)
    {
        String [] row = new String[3];
        
        row[0] = attribute.getName();
        row[1] = attribute.getAttributeGroup().getGroupName();
        row[2] = attribute.getValueType().getDescription();
        
        return row;
    }

    public void init(Transaction t)
    {
        filters_[0] = new StringFilter();
        filters_[1] = new StringFilter();
        filters_[2] = new StringFilter();
    }

    public void selectAction(Attribute selectedItem)
    {
        RegaDBMain.getApp().getTree().getTreeContent().attributesSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().attributesSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().attributesSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().attributesView.selectNode();
    }

    public boolean[] sortableFields()
    {
        return sortable_;
    }
}