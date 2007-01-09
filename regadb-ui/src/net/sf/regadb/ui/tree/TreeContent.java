package net.sf.regadb.ui.tree;

import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.items.myAccount.LoginItem;
import net.sf.regadb.ui.tree.items.myAccount.MyAccountItem;
import net.sf.regadb.ui.tree.items.singlePatient.ActionAddItem;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;
import net.sf.regadb.ui.tree.items.singlePatient.ActionSelectItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientAddItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientSelectItem;
import net.sf.witty.wt.widgets.WContainerWidget;

public class TreeContent
{
	public TreeMenuNode setContent(WContainerWidget rootCont)
	{
		RootItem rootItem = new RootItem(rootCont);
		
		PatientItem singlePatientMain = new PatientItem(rootItem);
		rootCont.addWidget(rootItem);
			PatientSelectItem patientSelect = new PatientSelectItem(singlePatientMain);
			PatientAddItem patientAdd = new PatientAddItem(singlePatientMain);
			
			ActionItem chart = new ActionItem(rootCont.tr("menu.singlePatient.chart"), singlePatientMain);
			ActionItem measurements = new ActionItem(rootCont.tr("menu.singlePatient.measurements"), singlePatientMain);
				ActionSelectItem measurementsSelect = new ActionSelectItem(rootCont.tr("menu.singlePatient.measurements.select"), measurements);
				ActionAddItem measurementsAdd = new ActionAddItem(rootCont.tr("menu.singlePatient.measurements.add"), measurements);
			ActionItem therapies = new ActionItem(rootCont.tr("menu.singlePatient.therapies"), singlePatientMain);
				ActionSelectItem therapiesSelect = new ActionSelectItem(rootCont.tr("menu.singlePatient.therapies.select"), therapies);
				ActionAddItem therapiesAdd = new ActionAddItem(rootCont.tr("menu.singlePatient.therapies.add"), therapies);
		
		MyAccountItem myAccountMain = new MyAccountItem(rootItem);
			LoginItem myAccountLogin = new LoginItem(myAccountMain);
			
		return rootItem;
	}
}
