package net.sf.regadb.ui.tree.items.datasetSettings;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class DatasetSelectedItem extends GenericSelectedItem <Dataset>
{

	public DatasetSelectedItem(WTreeNode parent) 
	{
		super(parent, "menu.datasetSettings.datasetSelectedItem");
		
	}

	@Override
	public String getArgument(Dataset dataset) 
	{
		return dataset.getDescription();
	}
}
