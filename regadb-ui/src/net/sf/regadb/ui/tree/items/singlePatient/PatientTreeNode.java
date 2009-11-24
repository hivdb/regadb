package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.ui.datatable.patient.IPatientDataTable;
import net.sf.regadb.ui.datatable.patient.SelectPatientForm;
import net.sf.regadb.ui.form.singlePatient.SinglePatientForm;
import net.sf.regadb.ui.form.singlePatient.chart.PatientChartForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.ui.tree.items.custom.ContactItem;
import eu.webtoolkit.jwt.WTreeNode;
import eu.webtoolkit.jwt.WWidget;

public class PatientTreeNode extends ObjectTreeNode<Patient>{
	private ActionItem chart;
	private TestResultTreeNode testResult;
	private TherapyTreeNode therapy;
	private ViralIsolateTreeNode viralIsolate;
	private EventTreeNode event;
	
	private ActionItem custom;
	private ContactItem contact;
	
	public PatientTreeNode(WTreeNode root) {
		super("patient", root);
	}
	
	protected void init(){
		super.init();
		
		chart = new ActionItem(getResource("chart"), getSelectedActionItem(), new ITreeAction()
			{
				public void performAction(TreeMenuNode node) 
				{
					RegaDBMain.getApp().getFormContainer().setForm(new PatientChartForm(getSelectedItem()));
				}
			});
		
		testResult = new TestResultTreeNode(getSelectedActionItem());
		therapy = new TherapyTreeNode(getSelectedActionItem());
		viralIsolate = new ViralIsolateTreeNode(getSelectedActionItem());
		event = new EventTreeNode(getSelectedActionItem());
		
		custom = new ActionItem(getResource("custom"), getSelectedActionItem());
		contact = new ContactItem(custom);
	}
	
	public ActionItem getChartActionItem(){
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
	public EventTreeNode getEventTreeNode(){
		return event;
	}
	public ActionItem getCustomActionItem(){
		return custom;
	}
	public ContactItem getContactItem(){
		return contact;
	}

	@Override
	protected void doAdd() {
		setSelectedItem(null);
        RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Adding, WWidget.tr("form.singlePatient.add"), new Patient()));
	
        IPatientDataTable.clearItems();
	}

	@Override
	protected void doDelete() {
		RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Deleting, WWidget.tr("form.singlePatient.delete"), getSelectedItem()));
	}

	@Override
	protected void doEdit() {
		RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Editing, WWidget.tr("form.singlePatient.edit"), getSelectedItem()));
	}

	@Override
	protected void doSelect() {
		RegaDBMain.getApp().getFormContainer().setForm(new SelectPatientForm());
	}

	@Override
	protected void doView() {
		RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Viewing, WWidget.tr("form.singlePatient.view"), getSelectedItem()));
	}

    @Override
    public String getArgument(Patient type) 
    {
         return type.getPatientId();
    }

	@Override
	public boolean isEnabled()
	{
		return RegaDBMain.getApp().getLogin()!=null;
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
	    	}
    	}
    }
    
    @Override
    public void applyPrivileges(Privileges priv){
    	super.applyPrivileges(priv);
    	getAddActionItem().enable();
    	
    	boolean disabled = priv != Privileges.READWRITE;
    	getContactItem().addContact.setDisabled(disabled);
    }
}
