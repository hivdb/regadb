package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class QueryItem extends TreeMenuNode
{
    public QueryItem(WTreeNode root)
    {
        super(tr("query.plural"), root);
    }
    
    public QueryItem(WMessage label, WTreeNode root)
    {
        super(label, root);
    }
    
    @Override
    public ITreeAction getFormAction()
    {
		return new ITreeAction()
		{
			public void performAction(TreeMenuNode node)
			{
			    getChildren().get(0).prograSelectNode();
			}
		};
    }

    @Override
    public boolean isEnabled()
    {
        return RegaDBMain.getApp().getLogin() != null;
    }
}
