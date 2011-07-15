package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.datatable.patient.SelectPatientForm;
import net.sf.regadb.ui.form.singlePatient.SinglePatientForm;
import net.sf.regadb.ui.form.singlePatient.chart.PatientChartForm;
import net.sf.regadb.ui.form.singlePatient.custom.Nadir;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.DefaultNavigationNode;
import net.sf.regadb.ui.tree.FormNavigationNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.ui.tree.items.custom.ContactItem;
import eu.webtoolkit.jwt.WString;

public class PatientTreeNode extends ObjectTreeNode<Patient>{
	private FormNavigationNode chart;
	private TestResultTreeNode testResult;
	private TherapyTreeNode therapy;
	private ViralIsolateTreeNode viralIsolate;
	private PatientEventTreeNode event;
	
	private DefaultNavigationNode custom;
	private ContactItem contact;
	private FormNavigationNode nadir;
	
	public PatientTreeNode(TreeMenuNode parent) {
		super("patient", parent);
	}
	
	protected void init(){
		super.init();
		
		chart = new FormNavigationNode(getMenuResource("chart"), getSelectedItemNavigationNode()){
			@Override
			public IForm createForm() {
				return new PatientChartForm(getSelectedItem());
			}
		};
		
		testResult = new TestResultTreeNode(getSelectedItemNavigationNode());
		therapy = new TherapyTreeNode(getSelectedItemNavigationNode());
		viralIsolate = new ViralIsolateTreeNode(getSelectedItemNavigationNode());
		event = new PatientEventTreeNode(getSelectedItemNavigationNode());
		
		custom = new DefaultNavigationNode(getMenuResource("custom"), getSelectedItemNavigationNode());
		contact = new ContactItem(custom);
		
		nadir = new FormNavigationNode(getMenuResource("custom.nadir"), custom){
			public IForm createForm() 
			{
				return new Nadir();
			}
		};
	}
	
	public FormNavigationNode getChartNode(){
		return chart;
	}
	public TestResultTreeNode getTestResultTreeNode(){
		return testResult;
	}
	public TherapyTreeNode getTherapyTreeNode(){
		return therapy;
	}
	public ViralIsolateTreeNode getViralIsolateTreeNode(){
		return viralIsolate;
	}
	public PatientEventTreeNode getEventTreeNode(){
		return event;
	}
	public DefaultNavigationNode getCustomNode(){
		return custom;
	}
	public ContactItem getContactItem(){
		return contact;
	}

	protected Patient getObjectById(Transaction t, String id){
		return t.getPatient(Integer.parseInt(id));
	}
	
	protected String getObjectId(Patient object){
		return object.getPatientIi() +"";
	}
	
    @Override
    public String getArgument(Patient type) 
    {
         return type.getPatientId();
    }

	@Override
	public boolean isDisabled()
	{
		return RegaDBMain.getApp().getLogin()==null;
	}
	
    @Override
    public void setSelectedItem(Patient item){
    	Patient prev = getSelectedItem();
    	if(item != prev){
	    	super.setSelectedItem(item);
	    	
	    	if(item != null){
		    	Privileges priv = RegaDBMain.getApp().getPrivilege(item.getSourceDataset());
		    	applyPrivileges(priv);
		    	testResult.applyPrivileges(priv);
		    	therapy.applyPrivileges(priv);
		    	viralIsolate.applyPrivileges(priv);
		    	event.applyPrivileges(priv);
		    	
		    	testResult.setSelectedItem(null);
		    	therapy.setSelectedItem(null);
		    	viralIsolate.setSelectedItem(null);
		    	event.setSelectedItem(null);
		    	
		    	boolean disabled = item.getViralIsolates().size()<2;
				viralIsolate.getEvolutionNode().setDisabled(disabled);
				viralIsolate.getCumulatedResistanceNode().setDisabled(disabled);
	    	}
    	}
    }
    
    @Override
    public void applyPrivileges(Privileges priv){
    	super.applyPrivileges(priv);
    	getAddNavigationNode().enable();
    	
    	boolean disabled = priv != Privileges.READWRITE;
    	getContactItem().addContact.setDisabled(disabled);
    }

	@Override
	protected ObjectForm<Patient> createForm(WString name, InteractionState interactionState, Patient selectedObject) {
		if(interactionState == InteractionState.Adding)
			selectedObject = new Patient();
			
		return new SinglePatientForm(name, interactionState, PatientTreeNode.this, selectedObject);
	}

	@Override
	protected IForm createSelectionForm() {
		return new SelectPatientForm(this);
	}
}
