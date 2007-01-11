package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.witty.wt.validation.WValidator;
import net.sf.witty.wt.validation.WValidatorState;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WFormWidget;
import net.sf.witty.wt.widgets.WInteractWidget;
import net.sf.witty.wt.widgets.WLineEdit;
import net.sf.witty.wt.widgets.WLineEditEchoMode;
import net.sf.witty.wt.widgets.WText;

public class TextField extends WContainerWidget implements IFormField
{
	private WLineEdit _fieldEdit;
	private WText _fieldView;
	
	public TextField(boolean edit, IForm form)
	{
		super();
		if(edit)
		{
			_fieldEdit = new WLineEdit();
			addWidget(_fieldEdit);
			flagValid();
		}
		else
		{
			_fieldView = new WText();
			addWidget(_fieldView);
		}
		
		form.addFormField(this);
	}
	
	public void setEchomode(WLineEditEchoMode mode)
	{
		_fieldEdit.setEchoMode(mode);
	}

	public WFormWidget getFormWidget()
	{
		return _fieldEdit;
	}
	
	public WInteractWidget getViewWidget()
	{
		return _fieldView;
	}

	public boolean isMandatory()
	{
		if(_fieldEdit==null)
		{
			return false;
		}
		else
		{
			WValidator validator = _fieldEdit.validator();
			if(validator==null)
				return false;
			else
				return validator.isMandatory();
		}
	}

	public void flagErroneous()
	{
		_fieldEdit.setStyleClass("form-field-textfield-edit-invalid");
	}

	public void flagValid()
	{
		_fieldEdit.setStyleClass("form-field-textfield-edit-valid");
	}

	public boolean validate()
	{
		if(_fieldEdit.validator()!=null)
		{
			return _fieldEdit.validator().validate(_fieldEdit.text(), null) == WValidatorState.Valid;
		}
		return true;
	}
	
	public void setValidator(WValidator validator)
	{
		_fieldEdit.setValidator(validator);
	}
	
	public void setMandatory(boolean mandatory)
	{
		if(_fieldEdit.validator()==null)
		{
			_fieldEdit.setValidator(new WValidator());
		}
		_fieldEdit.validator().setMandatory(mandatory);
	}
	
	public String text()
	{
		return _fieldEdit!=null?_fieldEdit.text():_fieldView.text().value();
	}
}
