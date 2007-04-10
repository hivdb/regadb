package net.sf.regadb.ui.framework.forms.fields;

import net.sf.witty.wt.WFormWidget;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WLabel;
import net.sf.witty.wt.i8n.WMessage;

public class Label extends WLabel
{
	private static WImage asterisk_ = new WImage("pics/formAsterisk.gif");
	
	static
	{
		asterisk_.setAlternateText(tr("form.label.alternateText.isRequired"));
	}
	
	public Label(WMessage labelText)
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
}
