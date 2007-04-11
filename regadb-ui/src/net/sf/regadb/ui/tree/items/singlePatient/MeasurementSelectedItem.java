package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class MeasurementSelectedItem extends GenericSelectedItem<TestResult>
{
	public MeasurementSelectedItem(WTreeNode parent)
	{
		super(parent, "menu.singlePatient.testResultSelectedItem", "{testResultId}");
	}

    @Override
    public String getArgument(TestResult type) 
    {
        return DateUtils.getEuropeanFormat(type.getTestDate());
    }
}
