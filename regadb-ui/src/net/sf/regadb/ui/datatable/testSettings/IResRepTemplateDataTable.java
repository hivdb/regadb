package net.sf.regadb.ui.datatable.testSettings;

import java.util.List;

import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DefaultDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import eu.webtoolkit.jwt.WString;

public class IResRepTemplateDataTable extends DefaultDataTable<ResistanceInterpretationTemplate>
{
    public IResRepTemplateDataTable(
			SelectForm<ResistanceInterpretationTemplate> form) {
		super(form);
	}

	private static WString [] _colNames = {WString.tr("datatable.resistance.report.interpretation.colName.name")};
    private static String[] filterVarNames_ = {"resistanceInterpretationTemplate.name"};
    
    private IFilter[] filters_ = new IFilter[1];
    
    private static boolean [] sortable_ = {true};
    private static int[] colWidths = {100};
    
    public CharSequence[] getColNames()
    {
        return _colNames;
    }

    public List<ResistanceInterpretationTemplate> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending)
    {
        return t.getResRepTemplates(startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
    }

    public long getDataSetSize(Transaction t)
    {
        return t.getResRepTemplatesCount(HibernateStringUtils.filterConstraintsQuery(this));
    }

    public String[] getFieldNames()
    {
        return filterVarNames_;
    }

    public IFilter[] getFilters()
    {
        return filters_;
    }

    public String[] getRowData(ResistanceInterpretationTemplate resRepTemplate)
    {
        String [] row = new String[1];
        
        row[0] = resRepTemplate.getName();
      
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

	public String[] getRowTooltips(ResistanceInterpretationTemplate type) {
		return null;
	}
}
