package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class QueryDefinitionRunSelectedItem extends GenericSelectedItem<QueryDefinitionRun>
{
    public QueryDefinitionRunSelectedItem(WTreeNode parent)
    {
        super(parent, "menu.query.definition.run.selectedItem");
    }

    @Override
    public String getArgument(QueryDefinitionRun queryDefinitionRun) 
    {
        return "" + queryDefinitionRun.getName();
    }
}
