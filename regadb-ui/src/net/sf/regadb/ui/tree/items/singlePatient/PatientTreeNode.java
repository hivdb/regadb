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
import eu.webtoolkit.jwt.WTreeNode;
import eu.webtoolkit.jwt.WWidget;

public class PatientTreeNode extends ObjectTreeNode<Patient>{
	private ActionItem chart;
	private TestResultTreeNode testResult;
	private TherapyTreeNode therapy;
	private ViralIsolateTreeNode viralIsolate;
	private EventTreeNode event;
	
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
	public ITreeAction getFormAction()
	{
		return new ITreeAction()
		{
			public void performAction(TreeMenuNode node)
			{
			    getChildren().get(0).prograSelectNode();
			}
		};
	}

	@Override
	public boolean isEnabled()
	{
		return RegaDBMain.getApp().getLogin()!=null;
	}
	
    @Override
    public void setSelectedItem(Patient item){
    	super.setSelectedItem(item);
    	
    	if(item != null){
	    	Privileges priv = RegaDBMain.getApp().getPrivilege(item.getSourceDataset());
	    	if(priv == Privileges.READWRITE){
	    		getEditActionItem().enable();
	    		getDeleteActionItem().enable();
	    	}
	    	else{
	    		getEditActionItem().disable();
	    		getDeleteActionItem().disable();
	    	}
    	}
    }
}
