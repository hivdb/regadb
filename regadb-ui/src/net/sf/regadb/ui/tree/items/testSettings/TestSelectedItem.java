package net.sf.regadb.ui.tree.items.testSettings;

import net.sf.regadb.db.Test;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class TestSelectedItem extends GenericSelectedItem<Test> 
{
	public TestSelectedItem(WTreeNode parent) 
	{
		super(parent, "menu.testSettings.testSelectedItem");
	}

    @Override
    public String getArgument(Test type) 
    {
        return type.getDescription();
    }
}
