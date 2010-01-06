package net.sf.regadb.ui.framework.forms.fields;

import java.util.Calendar;
import java.util.Date;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WDate;
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
		super(form);
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
			WDateValidator dv;
			int minYear = RegaDBSettings.getInstance().getInstituteConfig().getMinYear();
			int maxDays = RegaDBSettings.getInstance().getInstituteConfig().getMaxDaysFuture();
			
			if(minYear > -1 || maxDays > -1){
				if(minYear < 0)
					minYear = 1900;
				if(maxDays < 0)
					maxDays = 36500;
				
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, maxDays);
				
				dv = new WDateValidator(dateFormat,
						new WDate(minYear,1,1),
						new WDate(cal.getTime()));
			}
			else{
				dv = new WDateValidator(dateFormat);
			}
			_fieldEdit.setValidator(dv);
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
        return _fieldEdit.getText();
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
}
