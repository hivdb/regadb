package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class QueryDefinitionItem extends TreeMenuNode
{
    public QueryDefinitionItem(WTreeNode root)
    {
        super(tr("query.definition.plural"), root);
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
