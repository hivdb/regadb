package net.sf.regadb.ui.framework.forms.validation;

import java.util.ArrayList;

import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.warning.WarningMessage;
import net.sf.regadb.ui.framework.widgets.warning.WarningMessage.MessageType;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class WFormValidation 
{
    private WImage warningImage_ = new WImage("pics/formWarning.gif");
    private WString warningText_ = WWidget.tr("form.validationProblem.warning.mainText");
    private WContainerWidget warningWidget_ = new WarningMessage(warningImage_, warningText_, MessageType.ERROR);
    
    public void init(WContainerWidget parent)
    {
        parent.addWidget(warningWidget_);
        warningWidget_.setHidden(true);
    }
    
    public boolean validate(ArrayList<IFormField> formFields)
    {
        boolean erroneousInput = false;
        
        for(IFormField ff : formFields)
        {
            if(ff.getFormWidget()!=null && !ff.getFormWidget().isHidden())
            {
                if(!ff.validate())
                {
                    erroneousInput = true;
                    ff.flagErroneous();
                }
                else
                {
                    ff.flagValid();
                }
            }
        }
        
        return !erroneousInput;
    }
    
    public void setHidden(boolean hidden)
    {
        warningWidget_.setHidden(hidden);   
    }
}
