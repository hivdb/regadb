package net.sf.regadb.ui.framework;

import net.sf.regadb.ui.framework.form.IForm;
import net.sf.witty.wt.widgets.WContainerWidget;

public class FormContainer extends WContainerWidget
{
	private IForm form_;
	
	public FormContainer(WContainerWidget root)
	{
		super(root);
	}
	
	public void setForm(IForm form)
	{
		if(form_!=null)
		{
			removeWidget(form_.getWContainer());
		}
		form_ = form;
		addWidget(form.getWContainer());
	}
	
	public IForm getForm()
	{
		return form_;
	}
}
