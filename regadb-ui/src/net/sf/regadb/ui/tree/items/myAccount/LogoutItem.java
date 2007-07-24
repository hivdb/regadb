package net.sf.regadb.ui.tree.items.myAccount;

import net.sf.regadb.ui.forms.login.LoginForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.framework.widgets.messagebox.ConfirmMessageBox;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class LogoutItem extends TreeMenuNode
{
    public LogoutItem(WTreeNode root)
    {
        super(tr("menu.myAccount.logout"), root);
    }
    
    @Override
    public ITreeAction getFormAction()
    {
        return new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                final ConfirmMessageBox cmb = new ConfirmMessageBox(tr("menu.myAccount.logout.warning"));
                cmb.yes.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        RegaDBMain.getApp().getFormContainer().setForm(new LoginForm());
                        RegaDBMain.getApp().logout();
                        RegaDBMain.getApp().getWindow().newTree();
                        //RegaDBMain.getApp().getTree().getRootTreeNode().refreshAllChildren();
                        cmb.hide();
                    }
                });
                cmb.no.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        cmb.hide();
                    }
                });
            }
        };
    }

    @Override
    public boolean isEnabled()
    {
        return RegaDBMain.getApp().getLogin()!=null;
    }
}
