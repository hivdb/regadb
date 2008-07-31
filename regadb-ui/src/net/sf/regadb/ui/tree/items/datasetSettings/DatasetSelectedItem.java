package net.sf.regadb.ui.tree.items.datasetSettings;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class DatasetSelectedItem extends GenericSelectedItem <Dataset>
{

	public DatasetSelectedItem(WTreeNode parent) 
	{
		super(parent, "menu.datasetSettings.datasetSelectedItem", "{datasetSelectedItem}");
		
	}

	@Override
	public String getArgument(Dataset dataset) 
	{
		return dataset.getDescription();
	}
}
