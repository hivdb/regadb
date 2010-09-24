package net.sf.regadb.ui.form.event;

import java.util.List;

import net.sf.regadb.db.Event;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import eu.webtoolkit.jwt.WString;

public class IEventDataTable implements IDataTable<Event> {
	private static WString [] _colNames = {
	    WString.tr("dataTable.event.column.name"),
	    WString.tr("dataTable.event.column.valuetype")};
	
	private static String[] filterVarNames_ = {"event.name", "event.valueType"};
	private static boolean [] sortable_ = {true, true};
	private static int[] colWidths = {50,50};
	
	private IFilter[] filters_ = new IFilter[2];
	
	public IEventDataTable() { }
	
	public void selectAction(Event selectedItem) {
		RegaDBMain.getApp().getTree().getTreeContent().eventSelected.setSelectedItem(selectedItem);
		RegaDBMain.getApp().getTree().getTreeContent().eventSelected.refresh();
	}
	
	public List<Event> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending) {
		return t.getEvents(startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
	}
	
	public String[] getRowData(Event evt) {
		String [] row = new String[2];
		
		row[0] = evt.getName();
		row[1] = evt.getValueType().getDescription();
		
		return row;
	}
	
	public long getDataSetSize(Transaction t) {
		return t.eventCount(HibernateStringUtils.filterConstraintsQuery(this));
	}
	
	public void init(Transaction t) {
		filters_[0] = new StringFilter();
		filters_[1] = new StringFilter();
	}
	
	public CharSequence[] getColNames() {
		return _colNames;
	}
	
	public String[] getFieldNames() {
		return filterVarNames_;
	}
	
	public IFilter[] getFilters() {
		return filters_;
	}
	
	public boolean[] sortableFields() {
		return sortable_;
	}

	public int[] getColumnWidths() {
		return colWidths;
	}

	public String[] getRowTooltips(Event type) {
		return null;
	}
}
