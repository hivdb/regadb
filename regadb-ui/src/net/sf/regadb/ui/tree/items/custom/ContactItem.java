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
                return new MultipleTestResultForm( WString.tr("form.multipleTestResults.contact.newest"),
                                                    InteractionState.Viewing,
                                                    lastContact);
            }   
        };
        
        addContact = new FormNavigationNode(WString.tr("menu.patient.custom.contact.add"), this) {
            public IForm createForm() {
                return new MultipleTestResultForm( WString.tr("form.multipleTestResults.contact.add"),
                                                    InteractionState.Adding,
                                                    lastContact);
            }
        };
        
        grid = new FormNavigationNode(WString.tr("menu.patient.custom.contact.grid"), this) {
            public IForm createForm() {
                return new GridForm(InteractionState.Adding, lastContact);
            }
        };
    }
}
