package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class QueryDefinitionRunSelectedItem extends GenericSelectedItem<QueryDefinitionRun>
{
    public QueryDefinitionRunSelectedItem(WTreeNode parent)
    {
        super(parent, "query.definition.run.form", "{queryDefinitionRunSelectedItem}");
    }

    @Override
    public String getArgument(QueryDefinitionRun queryDefinitionRun) 
    {
        return "" + queryDefinitionRun.getName();
    }
}
