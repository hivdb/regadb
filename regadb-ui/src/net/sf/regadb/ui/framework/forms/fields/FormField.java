package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.framework.forms.IConfirmForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.settings.RegaDBSettings;
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
    
    private Signal.Listener confirmListener = null;
    
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
            WValidator validator = getFormWidget().getValidator();
            if(validator==null)
                return false;
            else
                return validator.isMandatory();
        }
    }
    
    public boolean validate()
    {
        boolean valid=true;

        if(getFormWidget().getValidator()!=null)
        {
            valid = getFormWidget().getValidator().validate(getFormText()) == WValidator.State.Valid;
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
        if(getFormWidget()!=null && getFormWidget().getValidator()==null)
        {
            getFormWidget().setValidator(new WValidator());
        }
        if(getFormWidget()!=null)
        {
            getFormWidget().getValidator().setMandatory(mandatory);
        }
    }
    
    public String text()
    {
        return getFormWidget()!=null?getFormText():getViewMessage().getValue();
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
                setViewMessage(text);
            }
    }
    
    protected void setViewMessage(CharSequence message)
    {
    	_fieldView.setText(message);
    }
    
    protected WString getViewMessage()
    {
    	return _fieldView.getText();
    }
    
    public WWidget getWidget()
    {
        return this;
    }
    
    protected void setDefaultConfirmAction(){
    	if(form instanceof IConfirmForm){
    		setConfirmAction(new Signal.Listener() {
				@Override
				public void trigger() {
					((IConfirmForm)form).confirmAction();
				}
			});
    	}
    }
    
    public void setConfirmAction(Signal.Listener se) {
    	if(confirmListener != null)
    		getFormWidget().enterPressed().removeListener(confirmListener);
    	
    	confirmListener = se;
    	
        if(getFormWidget()!=null && se!=null)
            getFormWidget().enterPressed().addListener(this, se);
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
    
    @SuppressWarnings("unchecked")
	public static FormField getTextField(ValueTypes type, InteractionState state, IForm form)
    {
        switch(type)
        {
        case STRING:
        	return new TextField(state,form);
        case TEXT:
        	return new TextAreaField(state,form);
        case NUMBER:
        	return new TextField(state, form, FieldType.DOUBLE);
        case LIMITED_NUMBER:
        	return new LimitedNumberField(state, form, FieldType.DOUBLE);
        case DATE:
            return new DateField(state, form, RegaDBSettings.getInstance().getDateFormat());
        case NOMINAL_VALUE:
            return new ComboBox(state, form);
        }
        
        return null;
    }
}
