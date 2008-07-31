package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectQueryDefinitionRunForm extends SelectForm
{
    private DataTable<QueryDefinitionRun> dataTable_;
    private ISelectQueryDefinitionRunDataTable dataTableI_;
    
    public SelectQueryDefinitionRunForm()
    {
        super(tr("query.definition.run.form"));
        
        init();
    }

    public void init()
    {
        dataTableI_ = new ISelectQueryDefinitionRunDataTable();
        dataTable_ = new DataTable<QueryDefinitionRun>(dataTableI_, 10);
        
        addWidget(dataTable_);
    }
}