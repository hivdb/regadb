package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class QueryRunSelectedItem extends GenericSelectedItem<QueryDefinition>
{
    public QueryRunSelectedItem(WTreeNode parent)
    {
        super(parent, "menu.query.run.selectedItem", "{queryRunSelectedItem}");
    }

    @Override
    public String getArgument(QueryDefinition queryDefinition) 
    {
        return "" + queryDefinition.getName();
    }
}
