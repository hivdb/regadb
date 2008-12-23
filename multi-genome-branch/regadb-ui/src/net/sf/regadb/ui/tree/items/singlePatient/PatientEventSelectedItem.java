package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class PatientEventSelectedItem extends GenericSelectedItem<PatientEventValue>
{
	public PatientEventSelectedItem(WTreeNode parent)
    {
        super(parent, "menu.singlePatient.event.selected");
    }
	
	@Override
	public String getArgument(PatientEventValue pev) {
		return "" + pev.getPatientEventValueIi();
	}
}
