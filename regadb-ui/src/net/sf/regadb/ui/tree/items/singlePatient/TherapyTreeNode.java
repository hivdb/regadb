package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.ui.datatable.therapy.SelectTherapyForm;
import net.sf.regadb.ui.form.singlePatient.TherapyForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.WTreeNode;
import eu.webtoolkit.jwt.WWidget;

public class TherapyTreeNode extends ObjectTreeNode<Therapy>{
	private ActionItem copyLast;

	public TherapyTreeNode(WTreeNode root) {
		super("patient.therapy", root);
	}
	
	@Override
	protected void init(){
		super.init();
		
      copyLast = new ActionItem(getResource("copylast"), this, new ITreeAction()
      {
          public void performAction(TreeMenuNode node)
          {
        	  copyLast();
          }
      });
	}
	
	protected void copyLast(){
        Patient p = RegaDBMain.getApp().getSelectedPatient();
        Therapy lastTherapy = null;
        for(Therapy therapy : p.getTherapies()){
            if(lastTherapy == null || lastTherapy.getStartDate().before(therapy.getStartDate()))
                lastTherapy = therapy;
        }
        RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Adding, WWidget.tr("form.therapy.add"), lastTherapy));
	}
	
	public ActionItem getCopyLastActionItem(){
		return copyLast;
	}

	@Override
	protected void doAdd() {
		setSelectedItem(null);
		RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Adding, WWidget.tr("form.therapy.add"), null));		
	}

	@Override
	protected void doDelete() {
		RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Deleting, WWidget.tr("form.therapy.delete"), getSelectedItem()));		
	}

	@Override
	protected void doEdit() {
		RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Editing, WWidget.tr("form.therapy.edit"), getSelectedItem()));		
	}

	@Override
	protected void doSelect() {
		RegaDBMain.getApp().getFormContainer().setForm(new SelectTherapyForm());		
	}

	@Override
	protected void doView() {
		RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Viewing, WWidget.tr("form.therapy.view"), getSelectedItem()));
	}

	@Override
	public String getArgument(Therapy type) {
		if(type != null)
			return DateUtils.format(type.getStartDate());
		else
			return "";
	}

}
