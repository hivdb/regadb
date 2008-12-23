package net.sf.regadb.ui.tree.items.custom;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.form.singlePatient.custom.MultipleTestResultForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.jdom.Element;

public class ContactItem extends ActionItem {
    public ActionItem lastContact;
    public ActionItem addContact;
    
    public static class TestItem{
        public TestItem(){
        }
        public TestItem(String d){
            description = d;
        }
        public TestItem(String d, String o){
            description = d;
            organism = o;
        }
        public String description = null;
        public String organism = null;
    }
    
    private static List<TestItem> contactTests = new ArrayList<TestItem>();
    
    static {
    	Element root = RegaDBSettings.getInstance().getCustomSettings("form.multipleTestResults.contact");
    	if(root != null){
            Element tests = (Element)root.getChild("tests");
            for(Object o : tests.getChildren()){
            	contactTests.add(new TestItem(((Element)o).getAttributeValue("description"),((Element)o).getAttributeValue("organism")));
            }
    	}
    	else{
	        contactTests.add(new TestItem(StandardObjects.getGenericCD4Test().getDescription()));
	        contactTests.add(new TestItem(StandardObjects.getGenericCD8Test().getDescription()));
	        contactTests.add(new TestItem(StandardObjects.getGenericHiv1ViralLoadTest().getDescription(), StandardObjects.getHiv1Genome().getOrganismName()));
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
