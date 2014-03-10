package net.sf.regadb.ui.form.singlePatient;

import java.util.List;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.datatable.utils.TextUtils;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DateFilter;
import net.sf.regadb.ui.framework.widgets.datatable.DefaultDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WString;

public class IPatientEventDataTable extends DefaultDataTable<PatientEventValue> {
	public IPatientEventDataTable(SelectForm<PatientEventValue> form) {
		super(form);
	}

	private static WString [] _colNames = {
		WString.tr("dataTable.patientEvent.column.startDate"),
		WString.tr("dataTable.patientEvent.column.endDate"),
		WString.tr("dataTable.patientEvent.column.eventName"),
		WString.tr("dataTable.patientEvent.column.value")};
	
	private static String[] filterVarNames_ = {"patient_event_value.startDate", "patient_event_value.endDate", "event.name", null};
	private static boolean [] sortable_ = {true, true, true, false};
	
	private static int[] colWidths = {20,20,25,35};
	
	private IFilter[] filters_ = new IFilter[4];
	
	public List<PatientEventValue> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending) {
		Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
		return t.getPatientEvents(p, startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
	}
	
	public long getDataSetSize(Transaction t) {
		Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
		return t.patientEventCount(p, HibernateStringUtils.filterConstraintsQuery(this));
	}
	
	public String[] getRowData(PatientEventValue pev) {
		String [] row = new String[4];
		
		row[0] = DateUtils.format(pev.getStartDate());
		row[1] = DateUtils.format(pev.getEndDate());
		
		row[2] = pev.getEvent().getName();
		
		if ( pev.getEvent().getValueType().getDescription().equals("nominal value") )
		{
			row[3] = pev.getEventNominalValue().getValue();
		} 
		else if (ValueTypes.getValueType(pev.getEvent().getValueType()) == ValueTypes.TEXT) 
		{
			row[3] = TextUtils.summarize(pev.getValue());
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
	
	public CharSequence[] getColNames() {
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
