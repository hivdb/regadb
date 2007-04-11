package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Therapy;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class TherapySelectedItem extends GenericSelectedItem<Therapy>
{
	public TherapySelectedItem(WTreeNode parent)
	{
		super(parent, "menu.singlePatient.therapies.therapySelectedItem", "{therapyId}");
	}

    @Override
    public String getArgument(Therapy type) 
    {
        return DateUtils.getEuropeanFormat(type.getStartDate());
    }
}
