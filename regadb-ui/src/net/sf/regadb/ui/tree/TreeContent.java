package net.sf.regadb.ui.tree;

import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.items.singlePatient.ActionAddItem;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;
import net.sf.regadb.ui.tree.items.singlePatient.ActionSelectItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientAddItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientSelectItem;
import net.sf.witty.wt.widgets.WContainerWidget;

public class TreeContent
{
	public TreeMenuNode setContent(WContainerWidget root)
	{
		PatientItem singlePatientMain = new PatientItem();
		root.addWidget(singlePatientMain);
			PatientSelectItem patientSelect = new PatientSelectItem(singlePatientMain);
			PatientAddItem patientAdd = new PatientAddItem(singlePatientMain);
			
			ActionItem chart = new ActionItem(root.tr("menu.singlePatient.chart"), singlePatientMain);
			ActionItem measurements = new ActionItem(root.tr("menu.singlePatient.measurements"), singlePatientMain);
				ActionSelectItem measurementsSelect = new ActionSelectItem(root.tr("menu.singlePatient.measurements.select"), measurements);
				ActionAddItem measurementsAdd = new ActionAddItem(root.tr("menu.singlePatient.measurements.add"), measurements);
			ActionItem therapies = new ActionItem(root.tr("menu.singlePatient.therapies"), singlePatientMain);
				ActionSelectItem therapiesSelect = new ActionSelectItem(root.tr("menu.singlePatient.therapies.select"), therapies);
				ActionAddItem therapiesAdd = new ActionAddItem(root.tr("menu.singlePatient.therapies.add"), therapies);
				
		return singlePatientMain;
	}
}
