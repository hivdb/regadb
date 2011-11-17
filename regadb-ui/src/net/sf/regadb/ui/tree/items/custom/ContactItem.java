package net.sf.regadb.ui.tree.items.custom;

import java.util.Date;

import net.sf.regadb.ui.form.singlePatient.ViralIsolateForm;
import net.sf.regadb.ui.form.singlePatient.custom.GridForm;
import net.sf.regadb.ui.form.singlePatient.custom.MultipleTestResultForm;
import net.sf.regadb.ui.framework.RegaDBMain;
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
        
        lastContact = new FormNavigationNode(WString.tr("menu.patient.custom.contact.last"), this, true) {
            public IForm createForm() {
            	return ContactItem.this.createMultipleTestResultForm(
            			WString.tr("form.multipleTestResults.contact.view"), InteractionState.Viewing);
            }   
        };
        
        addContact = new FormNavigationNode(WString.tr("menu.patient.custom.contact.add"), this, true) {
            public IForm createForm() {
                return ContactItem.this.createMultipleTestResultForm(
                		WString.tr("form.multipleTestResults.contact.add"), InteractionState.Adding);
            }
        };
        
        grid = new FormNavigationNode(WString.tr("menu.patient.custom.contact.grid"), this, true) {
            public IForm createForm() {
                return new GridForm(InteractionState.Adding, lastContact){

					@Override
					public void redirectAfterSave() {
						grid.selectNode();
					}

					@Override
					public void redirectAfterCancel() {
						lastContact.selectNode();
					}
                };
            }
        };
    }
    
    private IForm createMultipleTestResultForm(WString name, InteractionState state){
    	return new MultipleTestResultForm( WString.tr("form.multipleTestResults.contact.newest"),
                state,
                lastContact){

			@Override
			protected void gotoViralIsolateForm(String sampleId, Date sampleDate) {
				RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode
					.getViralIsolateTreeNode().getAddNavigationNode().selectNode();
				RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode
					.getViralIsolateTreeNode().setSelectedItem(null);
				RegaDBMain.getApp().getFormContainer().setForm(
						new ViralIsolateForm(
								WString.tr("form.viralIsolate.add"),
								InteractionState.Adding,
								RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode(),
								sampleId, sampleDate));
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
