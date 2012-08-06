package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectQueryDefinitionRunForm extends SelectForm<QueryDefinitionRun>
{
    public SelectQueryDefinitionRunForm(ObjectTreeNode<QueryDefinitionRun> node)
    {
        super(tr("form.query.definition.run.select"),node);
    }

    public DataTable<QueryDefinitionRun> createDataTable()
    {
        return new DataTable<QueryDefinitionRun>(new ISelectQueryDefinitionRunDataTable(this), 10);
    }
}