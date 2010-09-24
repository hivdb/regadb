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
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WString;

public class ITherapyDataTable implements IDataTable<Therapy>
{
	private static WString [] _colNames = {
	    WString.tr("dataTable.therapy.colName.startDate"),
	    WString.tr("dataTable.therapy.colName.endDate"), 
	    WString.tr("dataTable.therapy.colName.drugs"),
	    WString.tr("dataTable.therapy.colName.comment")};
	
	private static String[] filterVarNames_ = { "therapy.startDate", "therapy.stopDate", null, "therapy.comment"};
	
	private IFilter[] filters_ = new IFilter[4];
	
	private static boolean [] sortable_ = {true, true, false, true};
	private static int[] colWidths = {20,20,25,25};
	public CharSequence[] getColNames()
	{
		return _colNames;
	}

	public List<Therapy> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending)
	{
		Patient pt = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
		return t.getTherapies(pt, startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
	}

	public long getDataSetSize(Transaction t)
	{
		Patient pt = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
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
		
		row[0] = DateUtils.format(type.getStartDate());
		row[1] = DateUtils.format(type.getStopDate());
		
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
		filters_[0] = new DateFilter(RegaDBSettings.getInstance().getDateFormat());
		filters_[1] = new DateFilter(RegaDBSettings.getInstance().getDateFormat());
		filters_[2] = null;
		filters_[3] = new StringFilter();
	}

	public void selectAction(Therapy selectedItem)
	{
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTherapyTreeNode()
        	.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTherapyTreeNode()
        	.getSelectedActionItem().refresh();
	}

	public boolean[] sortableFields()
	{
		return sortable_;
	}

	public int[] getColumnWidths() {
		return colWidths;
	}

	public String[] getRowTooltips(Therapy type) {
		return null;
	}
}
