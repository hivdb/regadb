package net.sf.regadb.ui.tree.items.testSettings;

import net.sf.regadb.db.TestType;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class TestTypeSelectedItem extends GenericSelectedItem<TestType>
{
	public TestTypeSelectedItem(WTreeNode parent) 
	{
		super(parent, "menu.testSettings.testTypeSelectedItem");
	}

    @Override
    public String getArgument(TestType type) 
    {
        return type.getDescription();
    }
}
