package net.sf.regadb.ui.datatable.query;

import java.util.List;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.db.QueryDefinitionRunStatus;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DefaultDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import eu.webtoolkit.jwt.WString;

public class ISelectQueryDefinitionRunDataTable extends DefaultDataTable<QueryDefinitionRun>
{
    public ISelectQueryDefinitionRunDataTable(
			SelectForm<QueryDefinitionRun> form) {
		super(form);
	}

	private static WString [] _colNames = {
        WString.tr("dataTable.queryDefinitionRun.colName.name"),
        WString.tr("dataTable.queryDefinitionRun.colName.query"),
        WString.tr("dataTable.queryDefinitionRun.colName.description"),
        WString.tr("dataTable.queryDefinitionRun.colName.status")};
    
    private static String[] filterVarNames_ = {"queryDefinitionRun.name", "queryDefinitionRun.queryDefinition.name", "queryDefinitionRun.queryDefinition.description", "queryDefinitionRun.status"};
        
    private static boolean [] sortable_ = {true, true, true, true};
    private static int[] colWidths = {20,20,40,20};
    private IFilter[] filters_ = new IFilter[4];
    
    public CharSequence[] getColNames()
    {
        return _colNames;
    }

    public List<QueryDefinitionRun> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean ascending)
    {
        return t.getQueryDefinitionRuns(startIndex, amountOfRows, filterVarNames_[sortIndex], HibernateStringUtils.filterConstraintsQuery(this), ascending, RegaDBMain.getApp().getLogin().getUid());
    }

    public long getDataSetSize(Transaction t)
    {
        return t.getQueryDefinitionRunCount(HibernateStringUtils.filterConstraintsQuery(this));
    }
    
    public String[] getFieldNames()
    {
        return filterVarNames_;
    }

    public IFilter[] getFilters()
    {
        return filters_;
    }

    public String[] getRowData(QueryDefinitionRun queryDefinitionRun)
    {
        String[] row = new String[4];
        
        row[0] = queryDefinitionRun.getName();
        row[1] = queryDefinitionRun.getQueryDefinition().getName();
        row[2] = queryDefinitionRun.getQueryDefinition().getDescription();
        row[3] = QueryDefinitionRunStatus.getQueryDefinitionRunStatus(queryDefinitionRun).toString();
        
        return row;
    }

    public void init(Transaction t)
    {
        filters_[0] = new StringFilter();
        filters_[1] = new StringFilter();
        filters_[2] = new StringFilter();
        filters_[3] = new StringFilter();
    }

    public boolean[] sortableFields()
    {
        return sortable_;
    }

	public int[] getColumnWidths() {
		return colWidths;
	}

	public String[] getRowTooltips(QueryDefinitionRun type) {
		return null;
	}
}
