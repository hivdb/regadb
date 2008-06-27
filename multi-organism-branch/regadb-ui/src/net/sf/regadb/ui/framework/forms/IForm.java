package net.sf.regadb.ui.framework.forms;

import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.i8n.WMessage;

public interface IForm
{
	public WContainerWidget getWContainer();
	public void addFormField(IFormField field);
    public WMessage leaveForm();
}