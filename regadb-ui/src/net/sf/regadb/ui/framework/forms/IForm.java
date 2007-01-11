package net.sf.regadb.ui.framework.forms;

import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.witty.wt.widgets.WContainerWidget;

public interface IForm
{
	public WContainerWidget getWContainer();
	public void addFormField(IFormField field);
}