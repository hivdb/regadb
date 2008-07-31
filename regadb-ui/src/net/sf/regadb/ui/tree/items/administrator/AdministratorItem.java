package net.sf.regadb.ui.tree.items.administrator;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class AdministratorItem extends TreeMenuNode
{
    public AdministratorItem(WTreeNode root)
    {
        super(tr("account.administrator"), root);
    }
    
    @Override
    public ITreeAction getFormAction()
    {
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        if(RegaDBMain.getApp().getLogin()!=null)
        {
            Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
            
            return t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid()).getAdmin();
        }
        else
        {
            return false;
        }
    }
}
