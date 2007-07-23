package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;

public class SelectQueryDefinitionRunForm extends SelectForm
{
    private DataTable<QueryDefinitionRun> dataTable_;
    private ISelectQueryDefinitionRunDataTable dataTableI_;
    
    public SelectQueryDefinitionRunForm()
    {
        super(tr("form.query.definition.run.select"));
        
        init();
    }

    public void init()
    {
        dataTableI_ = new ISelectQueryDefinitionRunDataTable();
        dataTable_ = new DataTable<QueryDefinitionRun>(dataTableI_, 10);
        
        addWidget(dataTable_);
    }
}