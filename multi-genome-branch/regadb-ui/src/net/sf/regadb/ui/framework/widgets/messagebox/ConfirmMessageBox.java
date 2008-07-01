package net.sf.regadb.ui.framework.widgets.messagebox;

import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.i8n.WMessage;

public class ConfirmMessageBox extends MessageBox
{
    public WPushButton yes;
    public WPushButton no;
    
    public ConfirmMessageBox(WMessage message)
    {
        super(tr("msg.warning"), message, new WImage("pics/dialog-warning.png"));
        yes = new WPushButton(tr("msg.warning.button.yes"));
        no = new WPushButton(tr("msg.warning.button.no"));
        addButton(yes);
        addButton(no);
    }
}
