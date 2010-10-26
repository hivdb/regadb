package net.sf.regadb.ui.datatable.attributeSettings;

import java.util.List;

import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DefaultDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import eu.webtoolkit.jwt.WString;

public class IAttributeGroupDataTable extends DefaultDataTable<AttributeGroup>
{
    public IAttributeGroupDataTable(SelectForm<AttributeGroup> form) {
		super(form);
	}

	private static WString [] _colNames = {WString.tr("dataTable.attributeGroup.colName.groupName")};
    private static String[] filterVarNames_ = { "attributeGroup.groupName"};
    private static int[] colWidths = {100};
    
    private IFilter[] filters_ = new IFilter[1];
    
    private static boolean [] sortable_ = {true};
    
    public CharSequence[] getColNames()
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

    public boolean[] sortableFields()
    {
        return sortable_;
    }

	public int[] getColumnWidths() {
		return colWidths;
	}

	public String[] getRowTooltips(AttributeGroup type) {
		return null;
	}
}
