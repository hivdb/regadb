package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.i8n.WArgMessage;
import net.sf.witty.wt.widgets.WTreeNode;

public class TherapySelectedItem extends TreeMenuNode
{
	private Therapy selectedTherapy_;
	
	public TherapySelectedItem(WTreeNode parent)
	{
		super(new WArgMessage("menu.singlePatient.therapies.therapySelectedItem"), parent);
		((WArgMessage)label().text()).addArgument("{therapyId}", "");
	}

	public Therapy getSelectedTherapy()
	{
		return selectedTherapy_;
	}

	public void setSelectedTherapy(Therapy selectedTherapy)
	{
		selectedTherapy_ = selectedTherapy;
		
        ((WArgMessage)label().text()).changeArgument("{therapyId}", DateUtils.getEuropeanFormat(selectedTherapy_.getStartDate()));
        
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
		return selectedTherapy_!=null;
	}
}
