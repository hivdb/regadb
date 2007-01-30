package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.widgets.WTreeNode;

public class PatientSelectedItem extends TreeMenuNode implements IGetSinglePatient
{
    private Patient selectedPatient_ = null;
    
    public PatientSelectedItem(WTreeNode parent)
    {
        super(tr("menu.singlePatient.patientSelectedItem"), parent);
    }

    @Override
    public ITreeAction getFormAction()
    {
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return selectedPatient_!=null;
    }

    public Patient getSelectedPatient()
    {
        return selectedPatient_;
    }

    public void setSelectedPatient(Patient selectedPatient)
    {
        this.selectedPatient_ = selectedPatient;
    }
}
