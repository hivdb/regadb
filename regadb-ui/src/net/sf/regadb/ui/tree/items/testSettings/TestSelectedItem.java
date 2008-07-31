package net.sf.regadb.ui.tree.items.testSettings;

import net.sf.regadb.db.Test;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class TestSelectedItem extends GenericSelectedItem<Test> 
{
	public TestSelectedItem(WTreeNode parent) 
	{
		super(parent, "test.form", "{testSelectedItem}");
	}

    @Override
    public String getArgument(Test type) 
    {
        return type.getDescription();
    }
}
