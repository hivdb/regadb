package net.sf.regadb.ui.framework.forms;

import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.i8n.WMessage;

public class SelectForm extends WGroupBox implements IForm {
    
    public SelectForm(WMessage message) {
        super(message);
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
