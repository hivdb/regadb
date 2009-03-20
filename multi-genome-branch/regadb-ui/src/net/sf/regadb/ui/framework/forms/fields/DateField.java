package net.sf.regadb.ui.framework.forms.fields;

import java.util.Date;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WDatePicker;
import eu.webtoolkit.jwt.WDateValidator;
import eu.webtoolkit.jwt.WFormWidget;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WLineEdit;

public class DateField extends FormField
{
	private WLineEdit _fieldEdit;
	private WImage calendarIcon_ = new WImage("pics/calendar.png");
	
	public DateField(InteractionState state, IForm form, String dateFormat)
	{
		super();
		setStyleClass("datefield");
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
			_fieldEdit = new WLineEdit();
            ConfirmUtils.addConfirmAction(form, _fieldEdit);
			addWidget(_fieldEdit);
			WDatePicker dp = new WDatePicker(calendarIcon_, _fieldEdit, false, this);
			dp.setFormat(RegaDBSettings.getInstance().getDateFormat());
			flagValid();
		}
		else
		{
		    initViewWidget();
		}
		
		form.addFormField(this);
        
		if(_fieldEdit!=null)
		{
			_fieldEdit.setValidator(new WDateValidator(dateFormat));
		}
	}
	
	public void setEchomode(WLineEdit.EchoMode mode)
	{
		_fieldEdit.setEchoMode(mode);
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
    		setText(DateUtils.format(date));
    	}
        else
        {
            setText("");
        }
    }
    
    public Date getDate()
    {
    	return DateUtils.parse(text());
    }
    
    public void addChangeListener(Signal.Listener listener)
    {
        if(_fieldEdit!=null)
        {
            _fieldEdit.changed().addListener(this, listener);
        }
    }
    
    public void setEnabled(boolean enabled){
    	if(_fieldEdit != null)
    		_fieldEdit.setEnabled(enabled);
    }
    public boolean isEnabled(){
    	return _fieldEdit != null && _fieldEdit.isEnabled();
    }
}
