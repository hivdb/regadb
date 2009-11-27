package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.datatable.viralisolate.SelectViralIsolateForm;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateCumulatedResistance;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateForm;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateMutationEvolution;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateResistanceEvolutionForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.WTreeNode;
import eu.webtoolkit.jwt.WWidget;

public class ViralIsolateTreeNode extends ObjectTreeNode<ViralIsolate>{
	private ActionItem evolution;
	private ActionItem mutationEvolution;
	private ActionItem resistanceEvolution;
	private ActionItem cumulatedResistance;

	public ViralIsolateTreeNode(WTreeNode root) {
		super("patient.viralisolate", root);
	}
	
	@Override
	protected void init(){
		super.init();
		
		evolution = new ActionItem(getResource("evolution"), this);
        mutationEvolution = new ActionItem(getResource("evolution.mutation"), evolution, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateMutationEvolution(WWidget.tr("form.viralIsolate.evolution.mutation"), 
                        RegaDBMain.getApp().getSelectedPatient()));
            }
        });
        resistanceEvolution = new ActionItem(getResource("evolution.resistance"), evolution, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateResistanceEvolutionForm(WWidget.tr("form.viralIsolate.evolution.resistance"), 
                        RegaDBMain.getApp().getSelectedPatient()));
            }
        });
        cumulatedResistance = new ActionItem(getResource("cumulatedresistance"), this, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateCumulatedResistance(WWidget.tr("form.viralIsolate.cumulatedResistance"), 
                        RegaDBMain.getApp().getSelectedPatient()));
            }
        });
	}
	
	public ActionItem getEvolutionActionItem(){
		return evolution;
	}
	public ActionItem getMutationEvolutionActionItem(){
		return mutationEvolution;
	}
	public ActionItem getResistanceEvolutionActionItem(){
		return resistanceEvolution;
	}
	public ActionItem getCumulatedResistanceActionItem(){
		return cumulatedResistance;
	}

	@Override
	protected void doAdd() {
		setSelectedItem(null);
		RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Adding, WWidget.tr("form.viralIsolate.add"), null));
	}

	@Override
	protected void doDelete() {
		RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Deleting, WWidget.tr("form.viralIsolate.delete"), getSelectedItem()));		
	}

	@Override
	protected void doEdit() {
		RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Editing, WWidget.tr("form.viralIsolate.edit"), getSelectedItem()));		
	}

	@Override
	protected void doSelect() {
		RegaDBMain.getApp().getFormContainer().setForm(new SelectViralIsolateForm());		
	}

	@Override
	protected void doView() {
		RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Viewing, WWidget.tr("form.viralIsolate.view"), getSelectedItem()));		
	}

	@Override
	public String getArgument(ViralIsolate type) {
		if(type != null){
			return type.getSampleId();
		}
		else{
			return "";
		}
	}
	
	@Override
	public void refresh(){
		Patient p = RegaDBMain.getApp().getSelectedPatient();
		boolean disabled = p == null || p.getViralIsolates().size() < 2;
		evolution.setDisabled(disabled);
		cumulatedResistance.setDisabled(disabled);
		
		super.refresh();
	}
}
