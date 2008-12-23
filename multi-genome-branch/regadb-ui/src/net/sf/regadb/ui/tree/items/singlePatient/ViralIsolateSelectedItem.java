package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class ViralIsolateSelectedItem extends GenericSelectedItem<ViralIsolate>
{
	public ViralIsolateSelectedItem(WTreeNode parent)
	{
		super(parent, "menu.singlePatient.viralIsolates.viralIsolatesSelectedItem");
	}

    @Override
    public String getArgument(ViralIsolate type) 
    {
        return type.getSampleId();
    }
}
