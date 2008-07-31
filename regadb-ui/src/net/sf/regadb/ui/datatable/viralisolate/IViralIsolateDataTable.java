package net.sf.regadb.ui.datatable.viralisolate;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
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
	private static String [] _colNames = {"date.sample","viralIsolate.sampleid", 
		"viralIsolate.protein", "viralIsolate.ntseq.HIVType"};
	private static String[] filterVarNames_ = { "viralIsolate.sampleDate", "viralIsolate.sampleid", null, null};
	
	private IFilter[] filters_ = new IFilter[4];
	
	private static boolean [] sortable_ = {true, true, false, false};
	private static int[] colWidths = {25,25,25,25};
	public String[] getColNames()
	{
		return _colNames;
	}

	public List<ViralIsolate> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending)
	{
		Patient pt = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
		return t.getViralIsolates(pt, startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
	}

	public long getDataSetSize(Transaction t)
	{
		Patient pt = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
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
		
		SortedSet<String> proteinList = new TreeSet<String>();
		for(NtSequence ntseq : type.getNtSequences())
		{
			for(AaSequence aaseq : ntseq.getAaSequences())
			{
				proteinList.add(aaseq.getProtein().getAbbreviation());
			}
		}
		
		StringBuffer proteinBuffer = new StringBuffer(proteinList.size()*4);
		for(Iterator<String> i = proteinList.iterator(); i.hasNext();)
		{
			proteinBuffer.append(i.next());
			if(i.hasNext())
			proteinBuffer.append("+");
		}
		row[2] = proteinBuffer.toString();
		
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
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolateView.selectNode();
	}

	public boolean[] sortableFields()
	{
		return sortable_;
	}

	public int[] getColumnWidths() {
		return colWidths;
	}
}
