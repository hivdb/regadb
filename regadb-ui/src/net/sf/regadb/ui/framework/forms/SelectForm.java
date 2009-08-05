package net.sf.regadb.ui.framework.forms;

import net.sf.regadb.ui.framework.forms.fields.IFormField;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;

public class SelectForm extends WGroupBox implements IForm {
    
    public SelectForm(WString message) {
        super(message);
    }
    
    public void addFormField(IFormField field) {
        
    }

    public WContainerWidget getWContainer() {
        return this;
    }

    public WString leaveForm() {
        return null;
    }

	public void removeFormField(IFormField field) {
		
	}
}
