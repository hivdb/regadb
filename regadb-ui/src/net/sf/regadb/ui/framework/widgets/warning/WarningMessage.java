package net.sf.regadb.ui.framework.widgets.warning;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

public class WarningMessage extends WContainerWidget {

	private WText text;
	private WContainerWidget content;
	
	public enum MessageType {
		ERROR,
		INFO
	}
	
	public WarningMessage(WImage image, WString text, MessageType type) {
        addWidget(image);
        this.text = new WText(text);
        addWidget(this.text);
        setMessageType(type);
	}
	
	public void setText(WString msg) {
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
