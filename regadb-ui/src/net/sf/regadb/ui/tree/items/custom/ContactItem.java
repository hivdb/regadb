package net.sf.regadb.ui.tree.items.custom;

import net.sf.regadb.ui.form.singlePatient.custom.GridForm;
import net.sf.regadb.ui.form.singlePatient.custom.MultipleTestResultForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.DefaultNavigationNode;
import net.sf.regadb.ui.tree.FormNavigationNode;
import eu.webtoolkit.jwt.WString;

public class ContactItem extends DefaultNavigationNode {
    public FormNavigationNode lastContact;
    public FormNavigationNode addContact;
    public FormNavigationNode grid;
    
    public ContactItem(TreeMenuNode root) {
        super(WString.tr("menu.patient.custom.contact"), root);
        
        lastContact = new FormNavigationNode(WString.tr("menu.patient.custom.contact.last"), this) {
            public IForm createForm() {
            	return ContactItem.this.createMultipleTestResultForm(
            			WString.tr("form.multipleTestResults.contact.view"), InteractionState.Viewing);
            }   
        };
        
        addContact = new FormNavigationNode(WString.tr("menu.patient.custom.contact.add"), this) {
            public IForm createForm() {
                return ContactItem.this.createMultipleTestResultForm(
                		WString.tr("form.multipleTestResults.contact.add"), InteractionState.Adding);
            }
        };
        
        grid = new FormNavigationNode(WString.tr("menu.patient.custom.contact.grid"), this) {
            public IForm createForm() {
                return new GridForm(InteractionState.Adding, lastContact){

					@Override
					public void redirectAfterSave() {
						grid.selectNode();
					}

					@Override
					public void redirectAfterCancel() {
						grid.selectNode();
					}
                };
            }
        };
    }
    
    private IForm createMultipleTestResultForm(WString name, InteractionState state){
    	return new MultipleTestResultForm( WString.tr("form.multipleTestResults.contact.newest"),
                InteractionState.Viewing,
                lastContact){

			@Override
			protected void gotoViralIsolateForm() {
//				RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode
//					.getViralIsolateTreeNode().getAddNavigationNode().selectNode();
//				RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode
//					.getViralIsolateTreeNode().setSelectedItem(null);
//				RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(WString.tr("form.viralIsolate.add"), InteractionState.Adding, sampleIdTF_.text(), dateTF_.getDate()));
			}
			
			@Override
			public void redirectAfterSave() {
				lastContact.selectNode();
			}
			
			@Override
			public void redirectAfterCancel() {
				lastContact.selectNode();
			}
		};
    }
}
