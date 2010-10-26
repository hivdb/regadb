package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.datatable.viralisolate.SelectViralIsolateForm;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateCumulatedResistance;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateForm;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateMutationEvolution;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateResistanceEvolutionForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.DefaultNavigationNode;
import net.sf.regadb.ui.tree.FormNavigationNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.WString;

public class ViralIsolateTreeNode extends ObjectTreeNode<ViralIsolate>{
	private DefaultNavigationNode evolution;
	private FormNavigationNode mutationEvolution;
	private FormNavigationNode resistanceEvolution;
	private FormNavigationNode cumulatedResistance;

	public ViralIsolateTreeNode(TreeMenuNode parent) {
		super("patient.viralisolate", parent);
	}
	
	@Override
	protected void init(){
		super.init();
		
		evolution = new DefaultNavigationNode(getMenuResource("evolution"), this);
        mutationEvolution = new FormNavigationNode(getMenuResource("evolution.mutation"), evolution)
        {
            public IForm createForm()
            {
                return new ViralIsolateMutationEvolution(getFormResource("evolution.mutation"),RegaDBMain.getApp().getSelectedPatient());
            }
        };
        resistanceEvolution = new FormNavigationNode(getMenuResource("evolution.resistance"), evolution)
        {
            public IForm createForm()
            {
                return new ViralIsolateResistanceEvolutionForm(getFormResource("evolution.resistance"),RegaDBMain.getApp().getSelectedPatient());
            }
        };
        cumulatedResistance = new FormNavigationNode(getMenuResource("cumulatedresistance"), this)
        {
            public IForm createForm()
            {
                return new ViralIsolateCumulatedResistance(getFormResource("cumulatedResistance"),RegaDBMain.getApp().getSelectedPatient());
            }
        };
	}
	
	public DefaultNavigationNode getEvolutionNode(){
		return evolution;
	}
	public FormNavigationNode getMutationEvolutionNode(){
		return mutationEvolution;
	}
	public FormNavigationNode getResistanceEvolutionNode(){
		return resistanceEvolution;
	}
	public FormNavigationNode getCumulatedResistanceNode(){
		return cumulatedResistance;
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

	@Override
	protected ObjectForm<ViralIsolate> createForm(WString name, InteractionState interactionState, ViralIsolate selectedObject) {
		return new ViralIsolateForm(name, interactionState, ViralIsolateTreeNode.this, selectedObject);
	}

	@Override
	protected IForm createSelectionForm() {
		return new SelectViralIsolateForm(this);
	}
}
