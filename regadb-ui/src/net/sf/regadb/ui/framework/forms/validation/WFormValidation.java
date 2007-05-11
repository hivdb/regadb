package net.sf.regadb.ui.framework.forms.validation;

import java.util.ArrayList;

import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.WWidget;

public class WFormValidation 
{
    private WImage warningImage_ = new WImage("pics/formWarning.gif");
    private WText warningText_ = new WText(WWidget.tr("form.validationProblem.warning.mainText"));
    private WContainerWidget warningWidget_ = new WContainerWidget();
    
    public void init(WContainerWidget parent)
    {
        parent.addWidget(warningWidget_);
        warningWidget_.addWidget(warningImage_);
        warningWidget_.addWidget(warningText_);
        warningWidget_.setHidden(true);
    }
    
    public boolean validate(ArrayList<IFormField> formFields)
    {
        boolean erroneousInput = false;
        
        for(IFormField ff : formFields)
        {
            if(ff.getFormWidget()!=null)
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
