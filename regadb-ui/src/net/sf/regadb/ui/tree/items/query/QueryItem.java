package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTreeNode;

public class QueryItem extends TreeMenuNode
{
    public QueryItem(WTreeNode root)
    {
        super(tr("menu.query"), root);
    }
    
    public QueryItem(WString label, WTreeNode root)
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
    public boolean isDisabled()
    {
        return RegaDBMain.getApp().getLogin() == null || RegaDBMain.getApp().getRole().isSinglePatientView();
    }
}
