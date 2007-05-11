package net.sf.regadb.ui.tree.items.myAccount;

import net.sf.regadb.ui.forms.login.LoginForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.i8n.WMessage;
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
                confirmMessage(tr("menu.myAccount.logout.warning"));
            }
        };
    }

    @Override
    public boolean isEnabled()
    {
        return RegaDBMain.getApp().getLogin()!=null;
    }
    
    public MessageBox confirmMessage(WMessage message)
    {
        final MessageBox mb = new MessageBox(tr("msg.warning"), message, new WImage("pics/dialog-warning.png"));
        WPushButton yes = new WPushButton(tr("msg.warning.button.yes"));
        WPushButton no = new WPushButton(tr("msg.warning.button.no"));
        yes.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        RegaDBMain.getApp().getFormContainer().setForm(new LoginForm());
                        RegaDBMain.getApp().logout();
                        RegaDBMain.getApp().getTree().getRootTreeNode().refreshAllChildren();
                        mb.hide();
                    }
                });
        no.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        mb.hide();
                    }
                });
        mb.addButton(yes);
        mb.addButton(no);
        return mb;
    }
}
