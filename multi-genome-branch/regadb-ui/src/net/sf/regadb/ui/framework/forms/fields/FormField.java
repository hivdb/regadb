package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WInteractWidget;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WValidator;
import eu.webtoolkit.jwt.WWidget;

public abstract class FormField extends WContainerWidget implements IFormField
{
    private WText _fieldView;
    private boolean _unique=false;
    
    private IForm form;
    
    public FormField(IForm form) {
    	this.form = form;
    }
    
    public void initViewWidget()
    {
        _fieldView = new WText();
        _fieldView.setTextFormat(TextFormat.PlainText);
        
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
        boolean valid=true;

        if(getFormWidget().validator()!=null)
        {
            valid = getFormWidget().validator().validate(getFormText()) == WValidator.State.Valid;
        }
        
        if(valid && isUnique()){
            valid = checkUniqueness();
        }
        return valid;
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
        return getFormWidget()!=null?getFormText():getViewMessage().value();
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
                setViewMessage(lt(text));
            }
    }
    
    protected void setViewMessage(WString message)
    {
    	_fieldView.setText(message);
    }
    
    protected WString getViewMessage()
    {
    	return _fieldView.text();
    }
    
    public WWidget getWidget()
    {
        return this;
    }
    
    public void setConfirmAction(Signal.Listener se) {
        if(getFormWidget()!=null) {
        getFormWidget().enterPressed().removeAllListeners();
        if(se!=null)
            getFormWidget().enterPressed().addListener(this, se);
        }
    }
    
    public boolean isUnique(){
        return _unique;
    }
    
    public void setUnique(boolean unique){
        _unique = unique;
    }
    
    public boolean checkUniqueness(){
        return false;
    }
    
    public void setTextFormat(TextFormat tf) {
    	if(_fieldView!=null) {
    		_fieldView.setTextFormat(tf);
    	}
    }
    
    public IForm getForm() {
    	return form;
    }
}