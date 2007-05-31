package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;

public class SelectQueryDefinitionRunForm extends WGroupBox implements IForm
{
    private DataTable<QueryDefinitionRun> dataTable_;
    private ISelectQueryDefinitionRunDataTable dataTableI_;
    
    public SelectQueryDefinitionRunForm()
    {
        super(tr("form.query.definition.run.select"));
        
        init();
    }
    
    public void addFormField(IFormField field)
    {
        
    }

    public WContainerWidget getWContainer()
    {
        return this;
    }

    public void init()
    {
        dataTableI_ = new ISelectQueryDefinitionRunDataTable();
        dataTable_ = new DataTable<QueryDefinitionRun>(dataTableI_, 10);
        
        addWidget(dataTable_);
    }
}