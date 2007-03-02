package net.sf.regadb.ui.datatable.therapy;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.DateFilter;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import net.sf.regadb.util.date.DateUtils;

public class ITherapyDataTable implements IDataTable<Therapy>
{
	private static String [] _colNames = {"dataTable.therapy.colName.startDate","dataTable.therapy.colName.endDate", 
		"dataTable.therapy.colName.drugs", "dataTable.therapy.colName.comment"};
	private static String[] filterVarNames_ = { "therapy.startDate", "therapy.stopDate", null, "therapy.comment"};
	
	private IFilter[] filters_ = new IFilter[4];
	
	private static boolean [] sortable_ = {true, true, false, true};
	
	public String[] getColNames()
	{
		return _colNames;
	}

	public List<Therapy> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending)
	{
		Patient pt = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedPatient();
		return t.getTherapies(pt, startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
	}

	public long getDataSetSize(Transaction t)
	{
		Patient pt = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedPatient();
		return t.getTherapiesCount(pt, HibernateStringUtils.filterConstraintsQuery(this));
	}

	public String[] getFieldNames()
	{
		return filterVarNames_;
	}

	public IFilter[] getFilters()
	{
		return filters_;
	}

	public String[] getRowData(Therapy type)
	{
		String [] row = new String[4];
		
		row[0] = DateUtils.getEuropeanFormat(type.getStartDate());
		row[1] = DateUtils.getEuropeanFormat(type.getStopDate());
		
		SortedSet<String> drugList = new TreeSet<String>();
		for(TherapyGeneric tg : type.getTherapyGenerics())
		{
			drugList.add(tg.getId().getDrugGeneric().getGenericId());
		}
		for(TherapyCommercial tc : type.getTherapyCommercials())
		{
			for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics())
			{
				drugList.add(dg.getGenericId());
			}
		}
		
		StringBuffer genericDrugList = new StringBuffer(drugList.size()*4);
		for(Iterator<String> i = drugList.iterator(); i.hasNext();)
		{
			genericDrugList.append(i.next());
			genericDrugList.append("+");
		}
		//remove last character, which is an extra plus
		if(genericDrugList.length()>0)
		{
			genericDrugList.deleteCharAt(genericDrugList.length()-1);
		}
		row[2] = genericDrugList.toString();
		
		row[3] = type.getComment();
		
		return row;
	}

	public void init(Transaction t)
	{
		filters_[0] = new DateFilter();
		filters_[1] = new DateFilter();
		filters_[2] = null;
		filters_[3] = new StringFilter();
	}

	public void selectAction(Therapy selectedItem)
	{
        RegaDBMain.getApp().getTree().getTreeContent().therapiesSelected.setSelectedTherapy(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().therapiesSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().therapiesSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().therapiesView.selectNode();
	}

	public boolean[] sortableFields()
	{
		return sortable_;
	}

	public boolean stillExists(Therapy selectedItem)
	{
        Transaction trans = RegaDBMain.getApp().createTransaction();
        boolean state = trans.therapyStillExists(selectedItem);
        trans.commit();
        return state;
	}
}
