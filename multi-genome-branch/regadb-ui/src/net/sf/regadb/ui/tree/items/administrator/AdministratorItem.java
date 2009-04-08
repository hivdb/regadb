package net.sf.regadb.ui.tree.items.administrator;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.util.settings.RegaDBSettings;
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
        if(RegaDBMain.getApp().getLogin()!=null)
        {
            Transaction t = RegaDBMain.getApp().getLogin().createTransaction();

            return RegaDBSettings.getInstance().getAccessPolicyConfig().getRole(
            		t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid()).getUid()).isAdmin();
        }
        else
        {
            return false;
        }
    }
}
