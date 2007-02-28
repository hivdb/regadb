package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.validation.WDoubleValidator;
import net.sf.witty.wt.validation.WIntValidator;
import net.sf.witty.wt.widgets.WFormWidget;
import net.sf.witty.wt.widgets.WLineEdit;
import net.sf.witty.wt.widgets.WLineEditEchoMode;

public class TextField extends FormField
{
	private WLineEdit _fieldEdit;
	
	public TextField(InteractionState state, IForm form, FieldType type)
	{
		super();
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
			_fieldEdit = new WLineEdit();
			addWidget(_fieldEdit);
			flagValid();
		}
		else
		{
		    initViewWidget();
		}
		
		form.addFormField(this);
        
        switch(type)
        {
            case DOUBLE:
                _fieldEdit.setValidator(new WDoubleValidator());
                break;
            case INTEGER:
                _fieldEdit.setValidator(new WIntValidator());
                break;
        }
	}
    
    public TextField(InteractionState state, IForm form)
    {
        this(state, form, FieldType.ALFANUMERIC);
    }
	
	public void setEchomode(WLineEditEchoMode mode)
	{
		_fieldEdit.setEchoMode(mode);
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
}
