package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class PatientEventSelectedItem extends GenericSelectedItem<PatientEventValue>
{
	public PatientEventSelectedItem(WTreeNode parent)
    {
        super(parent, "event.form", "{patientEventID}");
    }
	
	@Override
	public String getArgument(PatientEventValue pev) {
		return "" + pev.getPatientEventValueIi();
	}
}
