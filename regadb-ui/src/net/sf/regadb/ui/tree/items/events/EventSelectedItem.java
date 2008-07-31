package net.sf.regadb.ui.tree.items.events;

import net.sf.regadb.db.Event;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class EventSelectedItem extends GenericSelectedItem<Event>
{
	public EventSelectedItem(WTreeNode parent)
	{
		super(parent, "event.form", "{eventName}");
	}
	
	@Override
	public String getArgument(Event type) {
		return RegaDBMain.getApp().getTree().getTreeContent().eventSelected.getSelectedItem().getEventIi().toString();
	}
}
