package net.sf.regadb.ui.form.query.custom;

import java.util.Date;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import eu.webtoolkit.jwt.WWidget;

public class DateParameter extends BasicParameter {
	private DateField df;

	public DateParameter(IForm form, String description, boolean mandatory) {
		super(description, mandatory);
		
		df = new DateField(InteractionState.Adding, form);
		df.setMandatory(mandatory);
	}

	@Override
	public WWidget getWidget() {
		return df;
	}
	
	public DateField getDateField(){
		return df;
	}
	
	public Date getDate(){
		return df.getDate();
	}

	@Override
	public boolean isValid() {
		return df.validate() && (!isMandatory() || df.getDate() != null);
	}
}
