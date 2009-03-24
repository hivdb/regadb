package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WFormWidget;
import eu.webtoolkit.jwt.WWidget;

public interface IFormField
{
	public boolean isMandatory();
	public void setMandatory(boolean mandatory);
	public WFormWidget getFormWidget();
    public String getFormText();
    public void setFormText(String text);
	public WWidget getViewWidget();
	public boolean validate();
	public void flagErroneous();
	public void flagValid();
    public WWidget getWidget();
    public void setConfirmAction(Signal.Listener se);
    public IForm getForm();
}
