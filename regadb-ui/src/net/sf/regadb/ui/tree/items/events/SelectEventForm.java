package net.sf.regadb.ui.tree.items.events;

import net.sf.regadb.db.Event;
import net.sf.regadb.ui.form.event.IEventDataTable;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectEventForm extends SelectForm {
	private DataTable<Event> datatable_;
	private IEventDataTable datatableI_;
	
	public SelectEventForm() {
		super(tr("event.form"));
        init();
	}
	
	public void init() 
    {
		datatableI_ = new IEventDataTable();
        datatable_ = new DataTable<Event>(datatableI_, 10);
        addWidget(datatable_);
    }
}
