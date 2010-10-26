package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectQueryDefinitionForm extends SelectForm<QueryDefinition>
{
    private DataTable<QueryDefinition> dataTable_;
    private ISelectQueryDefinitionDataTable dataTableI_;
    
    public SelectQueryDefinitionForm(ObjectTreeNode<QueryDefinition> node)
    {
        super(tr("form.query.definition.select"),node);
        
        init();
    }

    public void init()
    {
        dataTableI_ = new SelectHqlQueryDefinitionDatatable(this);
        dataTable_ = new DataTable<QueryDefinition>(dataTableI_, 10);
        
        addWidget(dataTable_);
    }
}