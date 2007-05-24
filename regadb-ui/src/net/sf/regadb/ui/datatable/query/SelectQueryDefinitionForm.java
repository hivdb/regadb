package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;

public class SelectQueryDefinitionForm extends WGroupBox implements IForm
{
    private DataTable<QueryDefinition> dataTable_;
    private ISelectQueryDefinitionDataTable dataTableI_;
    
    public SelectQueryDefinitionForm()
    {
        super(tr("form.query.definition.select"));
        
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
        dataTableI_ = new ISelectQueryDefinitionDataTable();
        dataTable_ = new DataTable<QueryDefinition>(dataTableI_, 10);
        
        addWidget(dataTable_);
    }
}