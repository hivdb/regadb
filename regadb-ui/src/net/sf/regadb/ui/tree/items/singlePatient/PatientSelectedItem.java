package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.i8n.WArgMessage;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class PatientSelectedItem extends TreeMenuNode implements IGetSinglePatient
{
    private Patient selectedPatient_ = null;
    
    public PatientSelectedItem(WTreeNode parent)
    {
        super(new WArgMessage("menu.singlePatient.patientSelectedItem"), parent);
        ((WArgMessage)label().text()).addArgument("{patientId}", "");
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
        
        String id="";
        if(selectedPatient_.getFirstName()!=null)
        {
            id += selectedPatient_.getFirstName() + " ";
        }
        if(selectedPatient_.getLastName()!=null)
        {
            id += selectedPatient_.getLastName();
        }
        id = id.trim();
        
        if(id.equals(""))
        {
            id = selectedPatient_.getPatientId();
        }
                
        ((WArgMessage)label().text()).changeArgument("{patientId}", id);
        
        refresh();
    }
}
