package net.sf.regadb.ui.framework.forms;

import net.sf.regadb.ui.framework.forms.fields.IFormField;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WString;

public interface IForm
{
	public WContainerWidget getWContainer();
	public void addFormField(IFormField field);
	public void removeFormField(IFormField field);
    public WString leaveForm();
    
    public void setListener(FormListener listener);
}