package net.sf.regadb.ui.tree.items.custom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.form.singlePatient.custom.MultipleTestResultForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ContactItem extends ActionItem {
    public ActionItem lastContact;
    public ActionItem addContact;
    
    private static List<String> contactTests = new ArrayList<String>();
    
    static {
    	Element root = RegaDBSettings.getInstance().getCustomSettings("form.multipleTestResults.contact");
    	if(root != null){
            Element tests = (Element)root.getChild("tests");
            for(Object o : tests.getChildren())
            	contactTests.add(((Element)o).getAttributeValue("description"));
    	}
    	else{
	        contactTests.add(StandardObjects.getGenericCD4Test().getDescription());
	        contactTests.add(StandardObjects.getGenericCD8Test().getDescription());
	        contactTests.add(StandardObjects.getGenericHiv1ViralLoadTest().getDescription());
    	}
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
