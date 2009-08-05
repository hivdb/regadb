package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import eu.webtoolkit.jwt.WFormWidget;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WString;

public class Label extends WLabel
{
	private static WImage asterisk_ = new WImage("pics/formAsterisk.gif");
	
	static
	{
		asterisk_.setAlternateText(tr("form.label.alternateText.isRequired"));
	}
	
	public Label(CharSequence labelText)
	{
		super(labelText);
        this.setStyleClass("form-label");
	}
	
	public void setBuddy(IFormField formField)
	{
		WFormWidget formWidget = formField.getFormWidget();
		if(formWidget!=null)
		{
			setBuddy(formField.getFormWidget());
		}
		if(formField.isMandatory())
		{
			setImage(asterisk_);
		}	
	}
    
    public void setLabelUIMandatory(FormWidget form)
    {
        if(form.getInteractionState()==InteractionState.Editing || form.getInteractionState()==InteractionState.Adding)
            setImage(asterisk_);
    }
}
