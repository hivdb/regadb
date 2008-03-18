package net.sf.regadb.ui.tree.items.custom;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Test;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.datatable.attributeSettings.SelectAttributeForm;
import net.sf.regadb.ui.form.singlePatient.custom.MultipleTestResultForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;

public class ContactItem extends ActionItem {
    public ActionItem lastContact;
    public ActionItem addContact;
    
    private static List<Test> contactTests = new ArrayList<Test>();
    
    static {
        contactTests.add(StandardObjects.getGenericCD4Test());
        contactTests.add(StandardObjects.getGenericCD8Test());
        contactTests.add(StandardObjects.getGenericViralLoadTest());
    }
    
    public ContactItem(ActionItem root) {
        super(tr("menu.custom.contacts"), root);
        
        lastContact = new ActionItem(tr("menu.custom.contacts.newest"), this, new ITreeAction() {
            public void performAction(TreeMenuNode node) {
                RegaDBMain.getApp().getFormContainer().setForm(
                        new MultipleTestResultForm( tr("form.multipleTestResults.contact.newest"),
                                                    InteractionState.Viewing,
                                                    contactTests,
                                                    lastContact)
                        );
            }   
        });
        
        addContact = new ActionItem(tr("menu.custom.contacts.add"), this, new ITreeAction() {
            public void performAction(TreeMenuNode node) {
                RegaDBMain.getApp().getFormContainer().setForm(
                        new MultipleTestResultForm( tr("form.multipleTestResults.contact.add"),
                                                    InteractionState.Adding,
                                                    contactTests,
                                                    lastContact)
                        );
            }   
        });
    }
}
