package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WFormWidget;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.validation.WValidator;

public class CheckBox extends WContainerWidget implements IFormField
{
    private WCheckBox checkBox = new WCheckBox();
    
    public CheckBox(InteractionState state, IForm form)
    {
        super();
        addWidget(checkBox);
        checkBox.setEnabled(state == InteractionState.Adding || state == InteractionState.Editing);
        
        if(form!=null)
        {
            form.addFormField(this);
        }
    }
    
    public void flagErroneous() 
    {
        
    }

    public void flagValid() 
    {
        
    }

    public String getFormText() 
    {
        return null;
    }

    public WFormWidget getFormWidget() 
    {
        return null;
    }

    public WWidget getViewWidget() 
    {
        return checkBox;
    }

    public WWidget getWidget() 
    {
        return checkBox;
    }

    public boolean isMandatory() 
    {
        if(checkBox.validator()!=null)
        {
            return checkBox.validator().isMandatory();
        }
        return false;
    }

    public void setFormText(String text) 
    {
        
    }

    public void setMandatory(boolean mandatory) 
    {
        if(checkBox.validator()!=null)
        {
            checkBox.setValidator(new WValidator());
        }
        checkBox.validator().setMandatory(mandatory);
    }

    public boolean validate() 
    {
        return true;
    }
}
