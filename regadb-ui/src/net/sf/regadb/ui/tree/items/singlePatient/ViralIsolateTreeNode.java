package net.sf.regadb.ui.tree.items.singlePatient;

import eu.webtoolkit.jwt.WTreeNode;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class ViralIsolateTreeNode extends ObjectTreeNode<ViralIsolate>{

	public ViralIsolateTreeNode(WTreeNode root) {
		super("patient.viralisolate", root);
	}

	@Override
	protected void doAdd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doDelete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doEdit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doSelect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doView() {
		// TODO Auto-generated method stub
		
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
	public ITreeAction getFormAction() {
		// TODO Auto-generated method stub
		return null;
	}

}
