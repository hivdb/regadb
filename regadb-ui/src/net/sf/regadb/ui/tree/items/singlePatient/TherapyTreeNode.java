package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.ui.datatable.therapy.SelectTherapyForm;
import net.sf.regadb.ui.form.singlePatient.TherapyForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.FormNavigationNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class TherapyTreeNode extends ObjectTreeNode<Therapy>{
	private FormNavigationNode copyLast;

	public TherapyTreeNode(TreeMenuNode parent) {
		super("therapy", parent);
	}
	
	@Override
	protected void init(){
		super.init();
		
      copyLast = new FormNavigationNode(getMenuResource("copylast"), this)
      {
          public ObjectForm<Therapy> createForm()
          {
        	  Patient p = RegaDBMain.getApp().getSelectedPatient();
              Therapy lastTherapy = null;
              for(Therapy therapy : p.getTherapies()){
                  if(lastTherapy == null || lastTherapy.getStartDate().before(therapy.getStartDate()))
                      lastTherapy = therapy;
              }
        	  return new TherapyForm(WWidget.tr("form.therapy.add"), InteractionState.Adding, TherapyTreeNode.this, lastTherapy);
          }
      };
	}
	
	protected void copyLast(){
        Patient p = RegaDBMain.getApp().getSelectedPatient();
        Therapy lastTherapy = null;
        for(Therapy therapy : p.getTherapies()){
            if(lastTherapy == null || lastTherapy.getStartDate().before(therapy.getStartDate()))
                lastTherapy = therapy;
        }
        RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(WWidget.tr("form.therapy.add"), InteractionState.Adding, TherapyTreeNode.this, lastTherapy));
	}
	
	public FormNavigationNode getCopyLastNode(){
		return copyLast;
	}

	@Override
	public String getArgument(Therapy type) {
		if(type != null)
			return DateUtils.format(type.getStartDate());
		else
			return "";
	}

	@Override
	public void applyPrivileges(Privileges priv){
		super.applyPrivileges(priv);
		copyLast.setDisabled(priv != Privileges.READWRITE);
	}

	@Override
	protected ObjectForm<Therapy> createForm(WString name, InteractionState interactionState, Therapy selectedObject) {
		return new TherapyForm(name, interactionState, TherapyTreeNode.this, selectedObject);
	}

	@Override
	protected IForm createSelectionForm() {
		return new SelectTherapyForm(this);
	}
}
