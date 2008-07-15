package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCssDecorationStyle;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFormWidget;
import net.sf.witty.wt.WLineEdit;
import net.sf.witty.wt.WLineEditEchoMode;
import net.sf.witty.wt.validation.WDoubleValidator;
import net.sf.witty.wt.validation.WEmailValidator;
import net.sf.witty.wt.validation.WIntValidator;

public class TextField extends FormField
{
	private WLineEdit _fieldEdit;
    private WLineEditEchoMode echoMode_;
	
	public TextField(InteractionState state, IForm form, FieldType type)
	{
		super();
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
			_fieldEdit = new WLineEdit();
            ConfirmUtils.addConfirmAction(form, _fieldEdit);
            
            addWidget(_fieldEdit);
			flagValid();
		}
		else
		{
		    initViewWidget();
		}
		
        if(form!=null)
        {
            form.addFormField(this);
        }
        
		if(_fieldEdit!=null)
		{
	        switch(type)
	        {
	            case DOUBLE:
	                _fieldEdit.setValidator(new WDoubleValidator());
	                break;
	            case INTEGER:
	                _fieldEdit.setValidator(new WIntValidator());
	                break;
                case EMAIL:
                    _fieldEdit.setValidator(new WEmailValidator());
                    break;
	        }
		}
	}
    
    public TextField(InteractionState state, IForm form)
    {
        this(state, form, FieldType.ALFANUMERIC);
    }
	
	public void setEchomode(WLineEditEchoMode mode)
	{
		echoMode_ = mode;
        if(_fieldEdit!=null)
        {
            _fieldEdit.setEchoMode(mode);
        }
	}

	@Override
	public void setText(String text) {
    	if(echoMode_ == WLineEditEchoMode.Password && _fieldEdit==null) {
    		String passwordText = "";
    		
    		for(int i = 0; i < text.length(); i++) {
    			passwordText += '*';
    		}
    		
    		text = passwordText;
    	}
		super.setText(text);
	}

	public WFormWidget getFormWidget()
	{
		return _fieldEdit;
	}
	
	public void flagErroneous()
	{
		_fieldEdit.setStyleClass("form-field-textfield-edit-invalid");
	}

	public void flagValid()
	{
		_fieldEdit.setStyleClass("form-field-textfield-edit-valid");
	}

    public String getFormText() 
    {
        return _fieldEdit.text();
    }
    
    public void setFormText(String text) 
    {
        _fieldEdit.setText(text);
    }
    
    public WCssDecorationStyle decorationStyle()
    {
		if(_fieldEdit!=null)
		{
			return _fieldEdit.decorationStyle();
		}
		else
		{
			return getViewWidget().decorationStyle();
		}
    }
    
    public void addChangeListener(SignalListener<WEmptyEvent> listener)
    {
        if(_fieldEdit!=null)
        {
            _fieldEdit.changed.addListener(listener);
        }
    }
    
    public void setEnabled(boolean enabled)
    {
        if(_fieldEdit!=null)
        {
            _fieldEdit.setEnabled(enabled);
        }
    }
    
    public void setHidden(boolean hide)
    {
        if(_fieldEdit!=null)
        {
            _fieldEdit.setHidden(hide);
        }
        else
        {
            getViewWidget().setHidden(hide);
        }
    }
}