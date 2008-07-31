package net.sf.regadb.ui.framework.widgets.messagebox;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WIFrame;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.extra.WModalFloating;

public class MessageBox extends WModalFloating
{
    private WTable table_ = new WTable();
    
    private WText title_;
    private WText message_;
    
    private WImage image_;
    
    private WContainerWidget buttonList_;
        
    public MessageBox(WMessage title, WMessage message, WImage image)
    {
        WContainerWidget panel = new WContainerWidget();
    	
    	
        int colSpan = 1;
        if(image!=null)
        {
            colSpan = 2;
        }
        int row = 0;
        int col = 0;
        title_ = new WText(title, table_.elementAt(row, col));
        title_.setStyleClass("dialog-title");
        table_.elementAt(row, col).setColumnSpan(colSpan);
        table_.elementAt(row, col).setStyleClass("dialog-title-container");
        row++;
        
        if(image!=null)
        {
            image_ = image;
            table_.elementAt(row, col).addWidget(image_);
            table_.elementAt(row, col).setStyleClass("dialog-message-container");
            image_.setStyleClass("dialog-image");
            col++;
        }
        message_ = new WText(message, table_.elementAt(row, col));
        table_.elementAt(row, col).setStyleClass("dialog-message-container");
        message_.setStyleClass("dialog-message");
        row++;
        col = 0;
        
        buttonList_ = new WContainerWidget(table_.elementAt(row, col));
        buttonList_.setContentAlignment(WHorizontalAlignment.AlignCenter);
        table_.elementAt(row, col).setStyleClass("dialog-message-container");
        table_.elementAt(row, col).setColumnSpan(colSpan);
        WIFrame frame = new WIFrame(null, panel);
        panel.addWidget(table_);
        setFloatingWidget(panel);
    }
    
    public void addButton(WPushButton button)
    {
        buttonList_.addWidget(button);
    }
    
    public static MessageBox showWarningMessage(WMessage message)
    {
        final MessageBox mb = new MessageBox(tr("message.general.warning"), message, new WImage("pics/dialog-warning.png"));
        WPushButton ok = new WPushButton(tr("general.ok"));
        ok.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        mb.hide();
                    }
                });
        mb.addButton(ok);
        return mb;
    }
}
