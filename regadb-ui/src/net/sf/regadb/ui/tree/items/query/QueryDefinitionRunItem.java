package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class QueryDefinitionRunItem extends TreeMenuNode
{
    public QueryDefinitionRunItem(WTreeNode root)
    {
        super(tr("menu.query.definition.run"), root);
    }
    
    @Override
    public ITreeAction getFormAction()
    {
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return RegaDBMain.getApp().getLogin() != null;
    }
}
