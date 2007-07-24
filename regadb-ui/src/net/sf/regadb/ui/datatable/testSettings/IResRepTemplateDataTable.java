package net.sf.regadb.ui.datatable.testSettings;

import java.util.List;

import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;

public class IResRepTemplateDataTable implements IDataTable<ResistanceInterpretationTemplate>
{
    private static String [] _colNames = {"datatable.resistance.report.interpretation.colName.name"};
    private static String[] filterVarNames_ = {"resistanceInterpretationTemplate.name"};
    
    private IFilter[] filters_ = new IFilter[1];
    
    private static boolean [] sortable_ = {true};
    
    public String[] getColNames()
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

    public void selectAction(ResistanceInterpretationTemplate selectedItem)
    {
        RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateView.selectNode();
    }

    public boolean[] sortableFields()
    {
        return sortable_;
    }
}
