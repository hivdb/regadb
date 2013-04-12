package net.sf.regadb.ui.tree.items.events;

import net.sf.regadb.db.Event;
import net.sf.regadb.ui.form.event.IEventDataTable;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectEventForm extends SelectForm<Event> {
	
	public SelectEventForm(ObjectTreeNode<Event> node) {
		super(tr("form.event.selectForm"),node);
	}
	
	public DataTable<Event> createDataTable()
    {
		return new DataTable<Event>(new IEventDataTable(this), 10);
    }
}
