package net.sf.regadb.ui.tree.items.administrator;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import eu.webtoolkit.jwt.WTreeNode;

public class AdministratorItem extends TreeMenuNode
{
    public AdministratorItem(WTreeNode root)
    {
        super(tr("menu.administrator.administrator"), root);
    }
    
    @Override
    public ITreeAction getFormAction()
    {
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return RegaDBMain.getApp().getLogin()!=null
            && RegaDBMain.getApp().getRole().isAdmin();
    }
}
