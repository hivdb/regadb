package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class PatientSelectedItem extends GenericSelectedItem<Patient>
{
    public PatientSelectedItem(WTreeNode parent)
    {
        super(parent, "menu.singlePatient.patientSelectedItem", "{patientId}");
    }

    @Override
    public String getArgument(Patient type) 
    {
        String id="";
        if(type.getFirstName()!=null)
        {
            id += type.getFirstName() + " ";
        }
        if(type.getLastName()!=null)
        {
            id += type.getLastName();
        }
        id = id.trim();
        
        if(id.equals(""))
        {
            id = type.getPatientId();
        }

        return id;
    }
}
