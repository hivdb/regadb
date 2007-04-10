package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.i8n.WArgMessage;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class ViralIsolateSelectedItem extends TreeMenuNode
{
	private ViralIsolate selectedViralIsolate_;
	
	public ViralIsolateSelectedItem(WTreeNode parent)
	{
		super(new WArgMessage("menu.singlePatient.viralIsolates.viralIsolatesSelectedItem"), parent);
		((WArgMessage)label().text()).addArgument("{viralIsolatesId}", "");
	}

	public ViralIsolate getSelectedViralIsolate()
	{
		return selectedViralIsolate_;
	}

	public void setSelectedViralIsolate(ViralIsolate selectedViralIsolate)
	{
		selectedViralIsolate_ = selectedViralIsolate;
		
        ((WArgMessage)label().text()).changeArgument("{viralIsolatesId}", selectedViralIsolate.getSampleId());
        
        refresh();
	}

	@Override
	public ITreeAction getFormAction()
	{
		return null;
	}

	@Override
	public boolean isEnabled()
	{
		return selectedViralIsolate_!=null;
	}
}
