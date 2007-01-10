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
	public TreeMenuNode setContent(RootItem rootItem)
	{
		PatientItem singlePatientMain = new PatientItem(rootItem);
		
			PatientSelectItem patientSelect = new PatientSelectItem(singlePatientMain);
			PatientAddItem patientAdd = new PatientAddItem(singlePatientMain);
			
			ActionItem chart = new ActionItem(rootItem.tr("menu.singlePatient.chart"), singlePatientMain);
			ActionItem measurements = new ActionItem(rootItem.tr("menu.singlePatient.measurements"), singlePatientMain);
				ActionSelectItem measurementsSelect = new ActionSelectItem(rootItem.tr("menu.singlePatient.measurements.select"), measurements);
				ActionAddItem measurementsAdd = new ActionAddItem(rootItem.tr("menu.singlePatient.measurements.add"), measurements);
			ActionItem therapies = new ActionItem(rootItem.tr("menu.singlePatient.therapies"), singlePatientMain);
				ActionSelectItem therapiesSelect = new ActionSelectItem(rootItem.tr("menu.singlePatient.therapies.select"), therapies);
				ActionAddItem therapiesAdd = new ActionAddItem(rootItem.tr("menu.singlePatient.therapies.add"), therapies);
		
		MyAccountItem myAccountMain = new MyAccountItem(rootItem);
			LoginItem myAccountLogin = new LoginItem(myAccountMain);
			
		if(singlePatientMain.isEnabled())
		{
			return singlePatientMain;
		}
		else
		{
			return myAccountLogin;
		}
	}
}
