package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectQueryDefinitionForm extends SelectForm<QueryDefinition>
{
    public SelectQueryDefinitionForm(ObjectTreeNode<QueryDefinition> node)
    {
        super(tr("form.query.definition.select"),node);
    }

    public DataTable<QueryDefinition> createDataTable()
    {
        return new DataTable<QueryDefinition>(new SelectHqlQueryDefinitionDatatable(this), 10);
    }
}