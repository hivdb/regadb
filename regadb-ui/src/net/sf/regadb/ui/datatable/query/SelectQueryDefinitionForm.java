package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectQueryDefinitionForm extends SelectForm
{
    private DataTable<QueryDefinition> dataTable_;
    private ISelectQueryDefinitionDataTable dataTableI_;
    
    public SelectQueryDefinitionForm()
    {
        super(tr("form.query.definition.select"));
        
        init();
    }

    public void init()
    {
        dataTableI_ = new ISelectQueryDefinitionDataTable();
        dataTable_ = new DataTable<QueryDefinition>(dataTableI_, 10);
        
        addWidget(dataTable_);
    }
}