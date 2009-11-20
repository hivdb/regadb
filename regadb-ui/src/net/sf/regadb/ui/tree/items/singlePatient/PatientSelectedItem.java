package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.ui.form.singlePatient.SinglePatientForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WResource;
import eu.webtoolkit.jwt.WTreeNode;
import eu.webtoolkit.jwt.WWidget;

public class PatientSelectedItem extends GenericSelectedItem<Patient>
{
    private ActionItem editPatient;
    private ActionItem deletePatient;
    private ActionItem viewPatient;
    
    public PatientSelectedItem(WTreeNode parent)
    {
        super(parent, "menu.singlePatient.patientSelectedItem");

        viewPatient = new ActionItem(WResource.tr("menu.singlePatient.view"), this, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Viewing, WWidget.tr("form.singlePatient.view"), getSelectedItem()));
            }
        });
        editPatient = new ActionItem(WResource.tr("menu.singlePatient.edit"), this, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Editing, WWidget.tr("form.singlePatient.edit"), getSelectedItem()));
            }
        });
        deletePatient = new ActionItem(WResource.tr("menu.singlePatient.delete"), this, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Deleting, WWidget.tr("form.singlePatient.delete"), getSelectedItem()));
            }
        });
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
    
    @Override
    public void setSelectedItem(Patient item){
    	super.setSelectedItem(item);
    	
    	Privileges priv = RegaDBMain.getApp().getPrivilege(item.getSourceDataset());
    	if(priv == Privileges.READWRITE){
    		editPatient.enable();
    		deletePatient.enable();
    	}
    	else{
    		editPatient.disable();
    		deletePatient.disable();
    	}
    }
}
