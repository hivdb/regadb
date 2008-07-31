package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class ViralIsolateSelectedItem extends GenericSelectedItem<ViralIsolate>
{
	public ViralIsolateSelectedItem(WTreeNode parent)
	{
		super(parent, "viralIsolate.form", "{viralIsolatesId}");
	}

    @Override
    public String getArgument(ViralIsolate type) 
    {
        return type.getSampleId();
    }
}
