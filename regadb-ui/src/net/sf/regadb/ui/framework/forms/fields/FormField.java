package net.sf.regadb.ui.framework.forms.fields;

import net.sf.witty.wt.validation.WValidator;
import net.sf.witty.wt.validation.WValidatorState;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WInteractWidget;
import net.sf.witty.wt.widgets.WText;
import net.sf.witty.wt.widgets.WWidget;

public abstract class FormField extends WContainerWidget implements IFormField
{
    private WText _fieldView;
    
    public void initViewWidget()
    {
        _fieldView = new WText();
        addWidget(_fieldView);
    }
    
    public WInteractWidget getViewWidget()
    {
        return _fieldView;
    }
    
    public boolean isMandatory()
    {
        if(getFormWidget()==null)
        {
            return false;
        }
        else
        {
            WValidator validator = getFormWidget().validator();
            if(validator==null)
                return false;
            else
                return validator.isMandatory();
        }
    }
    
    public boolean validate()
    {
        if(getFormWidget().validator()!=null)
        {
            return getFormWidget().validator().validate(getFormText(), null) == WValidatorState.Valid;
        }
        return true;
    }
    
    public void setValidator(WValidator validator)
    {
        getFormWidget().setValidator(validator);
    }
    

    public void setMandatory(boolean mandatory)
    {
        if(getFormWidget()!=null && getFormWidget().validator()==null)
        {
            getFormWidget().setValidator(new WValidator());
        }
        if(getFormWidget()!=null)
        {
            getFormWidget().validator().setMandatory(mandatory);
        }
    }
    
    public String text()
    {
        return getFormWidget()!=null?getFormText():_fieldView.text().value();
    }
    
    public void setText(String text)
    {
        if(text==null)
        {
        text = "";    
        }
        
        if(getFormWidget()!=null)
            {
                setFormText(text);
            }
        else
            {
                _fieldView.setText(lt(text));
            }
    }
    
    public WWidget getWidget()
    {
        return this;
    }
}