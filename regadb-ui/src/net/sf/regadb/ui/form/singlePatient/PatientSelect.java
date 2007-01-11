package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.tree.items.singlePatient.IGetSinglePatient;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WPushButton;
import net.sf.witty.wt.widgets.event.WMouseEvent;

public class PatientSelect extends WContainerWidget implements IForm
{
	public PatientSelect()
	{
		super(null);
		//WText selectPt = new WText("select a patient", this);
		WPushButton bt = new WPushButton(lt("select"), this);
		bt.clicked.addListener(new SignalListener<WMouseEvent>()
		{
			public void notify(WMouseEvent me)
			{
				IGetSinglePatient singlePatient = (IGetSinglePatient)RegaDBMain.getApp().getTree().getRootTreeNode().findDeepChild("menu.singlePatient.mainItem");
				singlePatient.setSelectedPatient(new Patient(null,1));
				
				RegaDBMain.getApp().getTree().getRootTreeNode().refresh();
			}
		});
	}
	
	public WContainerWidget getWContainer()
	{
		return this;
	}

	public void addFormField(IFormField field)
	{
		// TODO Auto-generated method stub
		
	}
}
