package net.sf.regadb.ui.framework.forms.fields;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFormWidget;
import net.sf.witty.wt.WWidget;

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
    public void setConfirmAction(SignalListener<WEmptyEvent> se);
}
