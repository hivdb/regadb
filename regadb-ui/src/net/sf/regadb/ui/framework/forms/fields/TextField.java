package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WCssDecorationStyle;
import eu.webtoolkit.jwt.WDoubleValidator;
import eu.webtoolkit.jwt.WFormWidget;
import eu.webtoolkit.jwt.WIntValidator;
import eu.webtoolkit.jwt.WLineEdit;

public class TextField extends FormField
{
	private WLineEdit _fieldEdit;
    private WLineEdit.EchoMode echoMode_;
	
	public TextField(InteractionState state, IForm form, FieldType type)
	{
		super(form);
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
			_fieldEdit = new WLineEdit();
            setDefaultConfirmAction();
            
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
                    _fieldEdit.setValidator(new EmailValidator());
                    break;
	        }
		}
		setText(null);
	}
    
    public TextField(InteractionState state, IForm form)
    {
        this(state, form, FieldType.ALFANUMERIC);
    }
	
    public void setTextSize(int chars) {
    	if(_fieldEdit!=null)
    		_fieldEdit.setTextSize(chars);
    }
    
	public void setEchomode(WLineEdit.EchoMode mode)
	{
		echoMode_ = mode;
        if(_fieldEdit!=null)
        {
            _fieldEdit.setEchoMode(mode);
        }
	}

	@Override
	public void setText(String text) {
    	if(echoMode_ == WLineEdit.EchoMode.Password && _fieldEdit==null) {
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
		_fieldEdit.setStyleClass("Wt-invalid");
	}

	public void flagValid()
	{
		_fieldEdit.setStyleClass("");
	}

    public String getFormText() 
    {
        return _fieldEdit.getText();
    }
    
    public void setFormText(String text) 
    {
        _fieldEdit.setText(text);
    }
    
    public WCssDecorationStyle decorationStyle()
    {
		if(_fieldEdit!=null)
		{
			return _fieldEdit.getDecorationStyle();
		}
		else
		{
			return getViewWidget().getDecorationStyle();
		}
    }
    
    public void addChangeListener(Signal.Listener listener)
    {
        if(_fieldEdit!=null)
        {
            _fieldEdit.changed().addListener(this, listener);
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
