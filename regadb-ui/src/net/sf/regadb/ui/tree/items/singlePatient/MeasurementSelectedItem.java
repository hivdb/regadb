package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.WTreeNode;

public class MeasurementSelectedItem extends GenericSelectedItem<TestResult>
{
	public MeasurementSelectedItem(WTreeNode parent)
	{
		super(parent, "menu.singlePatient.testResultSelectedItem");
	}

    @Override
    public String getArgument(TestResult type) 
    {
    	String result = type.getTest().getTestType().getDescription();
    	
    	if (type.getTestDate() != null)
    	{
    		result += " - " + DateUtils.format(type.getTestDate());
    	}
    	
        return result;
    }
}
