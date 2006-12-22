package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.form.IForm;
import net.sf.regadb.ui.tree.items.singlePatient.IGetSinglePatient;
import net.sf.witty.event.SignalListener;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WPushButton;
import net.sf.witty.wt.widgets.WText;
import net.sf.witty.wt.widgets.event.WMouseEvent;

public class PatientSelect extends WContainerWidget implements IForm
{
	public PatientSelect()
	{
		super(null);
		WText selectPt = new WText("select a patient", this);
		WPushButton bt = new WPushButton("select", this);
		bt.clicked.addListener(new SignalListener<WMouseEvent>()
		{
			public void notify(WMouseEvent me)
			{
				IGetSinglePatient singlePatient = (IGetSinglePatient)RegaDBMain.getTree().getRootTreeNode().findDeepChild("menu.singlePatient.mainItem");
				singlePatient.setSelectedPatient(new Patient());
				
				RegaDBMain.getTree().getRootTreeNode().refresh();
			}
		});
	}
	
	public WContainerWidget getWContainer()
	{
		return this;
	}
}
