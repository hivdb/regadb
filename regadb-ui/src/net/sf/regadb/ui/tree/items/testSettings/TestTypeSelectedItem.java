package net.sf.regadb.ui.tree.items.testSettings;

import net.sf.regadb.db.TestType;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.i8n.WArgMessage;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class TestTypeSelectedItem extends TreeMenuNode 
{
	TestType selectedTestType_;

	public TestTypeSelectedItem(WTreeNode parent) 
	{
		super(new WArgMessage("menu.testType.testTypeSelectedItem"), parent);
		((WArgMessage) label().text()).addArgument("{testTypeSelectedItem}", "");
	}

	public TestType getSelectedTestType() 
	{
		return selectedTestType_;
	}

	public void setSelectedTestType(TestType selectedTestType) 
	{
		selectedTestType_ = selectedTestType;

		((WArgMessage) label().text()).changeArgument("{testTypeSelectedItem}", selectedTestType.getDescription());

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
		return selectedTestType_ != null;
	}
}
