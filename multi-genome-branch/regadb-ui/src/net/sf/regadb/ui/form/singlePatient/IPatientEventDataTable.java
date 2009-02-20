package net.sf.regadb.ui.form.singlePatient;

import java.util.List;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.DateFilter;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;

public class IPatientEventDataTable  implements IDataTable<PatientEventValue> {
	private static String [] _colNames = {
		"dataTable.singlePatient.patientEvent.column.startDate",
		"dataTable.singlePatient.patientEvent.column.endDate",
		"dataTable.singlePatient.patientEvent.column.eventName",
		"dataTable.singlePatient.patientEvent.column.value"};
	
	private static String[] filterVarNames_ = {"patient_event_value.startDate", "patient_event_value.endDate", "event.name", null};
	private static boolean [] sortable_ = {true, true, true, false};
	
	private static int[] colWidths = {20,20,25,35};
	
	private IFilter[] filters_ = new IFilter[4];
	
	public List<PatientEventValue> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending) {
		Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
		return t.getPatientEvents(p, startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
	}
	
	public long getDataSetSize(Transaction t) {
		Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
		return t.patientEventCount(p, HibernateStringUtils.filterConstraintsQuery(this));
	}
	
	public void selectAction(PatientEventValue selectedItem) {
		RegaDBMain.getApp().getTree().getTreeContent().patientEventSelected.setSelectedItem(selectedItem);
		RegaDBMain.getApp().getTree().getTreeContent().patientEventSelected.expand();
		RegaDBMain.getApp().getTree().getTreeContent().patientEventSelected.refreshAllChildren();
		RegaDBMain.getApp().getTree().getTreeContent().patientEventView.selectNode();
	}
	
	public String[] getRowData(PatientEventValue pev) {
		String [] row = new String[4];
		
		row[0] = DateUtils.getEuropeanFormat(pev.getStartDate());
		row[1] = DateUtils.getEuropeanFormat(pev.getEndDate());
		
		row[2] = pev.getEvent().getName();
		
		if ( pev.getEvent().getValueType().getDescription().equals("nominal value") )
		{
			row[3] = pev.getEventNominalValue().getValue();
		}
		else
		{
			row[3] = pev.getValue();
		}
		
		return row;
	}
	
	public void init(Transaction t) {
		filters_[0] = new DateFilter(RegaDBSettings.getInstance().getDateFormat());
		filters_[1] = new DateFilter(RegaDBSettings.getInstance().getDateFormat());
		filters_[2] = new StringFilter();
		filters_[3] = null;
	}
	
	public String[] getFieldNames() {
		return filterVarNames_;
	}
	
	public boolean[] sortableFields() {
		return sortable_;
	}
	
	public String[] getColNames() {
		return _colNames;
	}
	
	public IFilter[] getFilters() {
		return filters_;
	}

	public int[] getColumnWidths() {
		return colWidths;
	}

	public String[] getRowTooltips(PatientEventValue type) {
		return null;
	}
}
