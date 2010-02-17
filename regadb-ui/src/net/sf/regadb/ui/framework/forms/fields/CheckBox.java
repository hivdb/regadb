package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WFormWidget;
import eu.webtoolkit.jwt.WValidator;
import eu.webtoolkit.jwt.WWidget;

public class CheckBox extends WContainerWidget implements IFormField
{
    private WCheckBox checkBox_ = new WCheckBox();
    private IForm form;
    
    public CheckBox(InteractionState state, IForm form, CharSequence text)
    {
        super();
        checkBox_.setText(text);
        addWidget(checkBox_);
        checkBox_.setEnabled(state == InteractionState.Adding || state == InteractionState.Editing);
        
        this.form = form;
        
        if(state!=InteractionState.Viewing)
        {
            ConfirmUtils.addConfirmAction(form, checkBox_);
        }
        
        if(form!=null)
        {
            form.addFormField(this);
        }
    }
    
    public CheckBox(InteractionState state, IForm form) {
    	this(state, form, "");
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
        return checkBox_;
    }

    public WWidget getViewWidget() 
    {
        return checkBox_;
    }

    public WWidget getWidget() 
    {
        return this;
    }

    public boolean isMandatory() 
    {
        if(checkBox_.getValidator()!=null)
        {
            return checkBox_.getValidator().isMandatory();
        }
        return false;
    }

    public void setFormText(String text) 
    {
        
    }

    public void setMandatory(boolean mandatory) 
    {
        if(checkBox_.getValidator()==null)
        {
            checkBox_.setValidator(new WValidator());
        }
        checkBox_.getValidator().setMandatory(mandatory);
    }

    public boolean validate() 
    {
        return true;
    }
    
    public boolean isChecked()
    {
        return checkBox_.isChecked();
    }
    
    public void setChecked(boolean checked)
    {
        checkBox_.setChecked(checked);
    }

    public void setConfirmAction(Signal.Listener se) {
//        if(getFormWidget()!=null) {
//        getFormWidget().enterPressed().removeAllListeners();
//        if(se != null)
//            getFormWidget().enterPressed().addListener(this, se);
//        }
    }

    public IForm getForm() {
    	return form;
    }
}
