package net.sf.regadb.ui.tree.items.events;

import net.sf.regadb.db.Event;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class EventSelectedItem extends GenericSelectedItem<Event>
{
	public EventSelectedItem(WTreeNode parent)
	{
		super(parent, "menu.event.selected");
	}
	
	@Override
	public String getArgument(Event type) {
		return RegaDBMain.getApp().getTree().getTreeContent().eventSelected.getSelectedItem().getEventIi().toString();
	}
}
