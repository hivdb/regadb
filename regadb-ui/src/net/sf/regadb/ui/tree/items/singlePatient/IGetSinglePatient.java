package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;

public interface IGetSinglePatient
{
	public Patient getSelectedPatient();
	public void setSelectedPatient(Patient selectedPatient);
}
