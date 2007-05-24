package net.sf.regadb.ui.tree.items.queryDefinition;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class QueryRunItem extends TreeMenuNode
{
    public QueryRunItem(WTreeNode root)
    {
        super(tr("menu.query.run"), root);
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
