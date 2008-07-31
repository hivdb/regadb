package net.sf.regadb.ui.framework.forms;

import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.i8n.WArgMessage;
import net.sf.witty.wt.i8n.WMessage;

public class SelectForm extends WGroupBox implements IForm {
    
    public SelectForm(WMessage message) {
        super(formatMessage(message));
    }
    
    private static WMessage formatMessage(WMessage message) {
    	WArgMessage msg = new WArgMessage("general.form.select");
    	msg.addArgument("{item}", message.value());
    	return msg;
    }
    
    public void addFormField(IFormField field) {
        
    }

    public WContainerWidget getWContainer() {
        return this;
    }

    public WMessage leaveForm() {
        return null;
    }
}
