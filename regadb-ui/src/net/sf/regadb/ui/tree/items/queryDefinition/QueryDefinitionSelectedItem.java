package net.sf.regadb.ui.tree.items.queryDefinition;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class QueryDefinitionSelectedItem extends GenericSelectedItem<QueryDefinition>
{
    public QueryDefinitionSelectedItem(WTreeNode parent)
    {
        super(parent, "menu.query.definition.selectedItem", "{queryDefinitionSelectedItem}");
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
