package net.sf.regadb.ui.framework.forms.fields;

import java.util.Date;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.validation.WEuropeanDateValidator;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WFormWidget;
import net.sf.witty.wt.widgets.WImage;
import net.sf.witty.wt.widgets.WLineEdit;
import net.sf.witty.wt.widgets.WLineEditEchoMode;
import net.sf.witty.wt.widgets.event.WEmptyEvent;

public class DateField extends FormField
{
	private WLineEdit _fieldEdit;
	private WImage calendarIcon_ = new WImage("pics/calendar.png");
	
	public DateField(InteractionState state, IForm form)
	{
		super();
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
			_fieldEdit = new WLineEdit();
			addWidget(_fieldEdit);
			addWidget(calendarIcon_);
			flagValid();
		}
		else
		{
		    initViewWidget();
		}
		
		form.addFormField(this);
        
		if(_fieldEdit!=null)
		{
			_fieldEdit.setValidator(new WEuropeanDateValidator());
		}
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
    
    public void setDate(Date date)
    {
    	if(date!=null)
    	{
    		setText(DateUtils.getEuropeanFormat(date));
    	}
        else
        {
            setText("");
        }
    }
    
    public Date getDate()
    {
    	return DateUtils.parserEuropeanDate(text());
    }
    
    public void addChangeListener(SignalListener<WEmptyEvent> listener)
    {
        if(_fieldEdit!=null)
        {
            _fieldEdit.changed.addListener(listener);
        }
    }
}
