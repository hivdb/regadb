package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class QueryDefinitionSelectedItem extends GenericSelectedItem<QueryDefinition>
{
    public QueryDefinitionSelectedItem(String text, WTreeNode parent)
    {
        super(parent, text, "{queryDefinitionSelectedItem}");
    }

    @Override
    public String getArgument(QueryDefinition queryDefinition) 
    {
        return "" + queryDefinition.getName();
    }
    
    public String getQueryDefinitionCreator(QueryDefinition queryDefinition) 
    {
        return "" + queryDefinition.getSettingsUser().getUid();
    }
}
