package net.sf.regadb.ui.framework.widgets.warning;

import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;

public class WarningMessage extends WContainerWidget {

	private WText text;
	private WContainerWidget content;
	
	public enum MessageType {
		ERROR,
		INFO
	}
	
	public WarningMessage(WImage image, WMessage text, MessageType type) {
        addWidget(image);
        this.text = new WText(text);
        addWidget(this.text);
        setMessageType(type);
	}
	
	public void setText(WMessage msg) {
		text.setText(msg);
	}
	
	public void setMessageType(MessageType type) {
        if (type == MessageType.ERROR) {
        	setStyleClass("message-error");
        }
        else {
        	setStyleClass("message-info");
        }
	}
	
	public WContainerWidget getContentArea() {
		if (content == null) {
			content = new WContainerWidget(this);
		}
		return content;
	}
}
