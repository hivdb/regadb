package net.sf.regadb.ui.datatable.viralisolate;

import java.util.List;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.DateFilter;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import net.sf.regadb.util.date.DateUtils;

public class IViralIsolateDataTable implements IDataTable<ViralIsolate>
{
	private static String [] _colNames = {"dataTable.viralIsolate.colName.sampleDate","dataTable.viralIsolate.colName.sampleId", 
		"dataTable.viralIsolate.colName.protein", "dataTable.viralIsolate.colName.hivType"};
	private static String[] filterVarNames_ = { "viralIsolate.sampleDate", "viralIsolate.sampleId", null, null};
	
	private IFilter[] filters_ = new IFilter[4];
	
	private static boolean [] sortable_ = {true, true, false, false};
	
	public String[] getColNames()
	{
		return _colNames;
	}

	public List<ViralIsolate> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending)
	{
		Patient pt = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedPatient();
		return t.getViralIsolates(pt, startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
	}

	public long getDataSetSize(Transaction t)
	{
		Patient pt = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedPatient();
		return t.getViralIsolateCount(pt, HibernateStringUtils.filterConstraintsQuery(this));
	}

	public String[] getFieldNames()
	{
		return filterVarNames_;
	}

	public IFilter[] getFilters()
	{
		return filters_;
	}

	public String[] getRowData(ViralIsolate type)
	{
		String [] row = new String[4];
		
		row[0] = DateUtils.getEuropeanFormat(type.getSampleDate());
		row[1] = type.getSampleId();
		row[2] = "";
		row[3] = "";
		
		return row;
	}

	public void init(Transaction t)
	{
		filters_[0] = new DateFilter();
		filters_[1] = new StringFilter();
		filters_[2] = null;
		filters_[3] = null;
	}

	public void selectAction(ViralIsolate selectedItem)
	{
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected.setSelectedViralIsolate(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected.refreshAllChildren();
        //TODO select vi view node
	}

	public boolean[] sortableFields()
	{
		return sortable_;
	}

	public boolean stillExists(ViralIsolate selectedItem)
	{
        Transaction trans = RegaDBMain.getApp().createTransaction();
        boolean state = trans.viralIsolateStillExists(selectedItem);
        trans.commit();
        return state;
	}
}
