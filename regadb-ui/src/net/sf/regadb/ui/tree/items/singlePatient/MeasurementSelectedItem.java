package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.i8n.WArgMessage;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class MeasurementSelectedItem extends TreeMenuNode
{
	TestResult selectedTestResult_;
	
	public MeasurementSelectedItem(WTreeNode parent)
	{
		super(new WArgMessage("menu.singlePatient.testResultSelectedItem"), parent);
		((WArgMessage)label().text()).addArgument("{testResultId}", "");
	}

	public TestResult getSelectedTestResult()
	{
		return selectedTestResult_;
	}

	public void setSelectedTestResult(TestResult selectedTestResult)
	{
		selectedTestResult_ = selectedTestResult;
		
        ((WArgMessage)label().text()).changeArgument("{testResultId}", DateUtils.getEuropeanFormat(selectedTestResult.getTestDate()));
        
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
		return selectedTestResult_!=null;
	}
}
