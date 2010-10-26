package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectQueryDefinitionRunForm extends SelectForm<QueryDefinitionRun>
{
    private DataTable<QueryDefinitionRun> dataTable_;
    private ISelectQueryDefinitionRunDataTable dataTableI_;
    
    public SelectQueryDefinitionRunForm(ObjectTreeNode<QueryDefinitionRun> node)
    {
        super(tr("form.query.definition.run.select"),node);
        
        init();
    }

    public void init()
    {
        dataTableI_ = new ISelectQueryDefinitionRunDataTable(this);
        dataTable_ = new DataTable<QueryDefinitionRun>(dataTableI_, 10);
        
        addWidget(dataTable_);
    }
}