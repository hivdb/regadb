package net.sf.regadb.ui.tree.items.testSettings;

import net.sf.regadb.db.Test;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.i8n.WArgMessage;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class TestSelectedItem extends TreeMenuNode {
	Test selectedTest_;

	public TestSelectedItem(WTreeNode parent) 
	{
		super(new WArgMessage("menu.testSettings.testSelectedItem"), parent);
		((WArgMessage) label().text()).addArgument("{testSelectedItem}", "");
	}

	public Test getSelectedTest() 
	{
		return selectedTest_;
	}

	public void setSelectedTest(Test selectedTest) 
	{
		selectedTest_ = selectedTest;

		((WArgMessage) label().text()).changeArgument("{testSelectedItem}", selectedTest.getDescription());

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
		return selectedTest_ != null;
	}
}
