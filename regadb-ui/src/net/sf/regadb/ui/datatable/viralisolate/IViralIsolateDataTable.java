package net.sf.regadb.ui.datatable.viralisolate;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Genome;
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
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WString;

public class IViralIsolateDataTable implements IDataTable<ViralIsolate>
{
	private static WString [] _colNames = {
	    WString.tr("dataTable.viralIsolate.colName.sampleDate"),
	    WString.tr("dataTable.viralIsolate.colName.sampleId"), 
	    WString.tr("dataTable.viralIsolate.colName.protein"),
	    WString.tr("dataTable.viralIsolate.colName.genome")};
	
	private static String[] filterVarNames_ = { "viralIsolate.sampleDate", "viralIsolate.sampleId", null, null};
	
	private IFilter[] filters_ = new IFilter[4];
	
	private static boolean [] sortable_ = {true, true, false, false};
	private static int[] colWidths = {25,25,25,25};
	public CharSequence[] getColNames()
	{
		return _colNames;
	}

	public List<ViralIsolate> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending)
	{
		Patient pt = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
		return t.getViralIsolates(pt, startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
	}

	public long getDataSetSize(Transaction t)
	{
		Patient pt = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
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
		
		row[0] = DateUtils.format(type.getSampleDate());
		row[1] = type.getSampleId() == null ? "---":type.getSampleId();
		
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
		
		Genome genome = type.getGenome();
		row[3] = (genome == null ? "":genome.getOrganismName());
		
		return row;
	}

	public void init(Transaction t)
	{
		filters_[0] = new DateFilter(RegaDBSettings.getInstance().getDateFormat());
		filters_[1] = new StringFilter();
		filters_[2] = null;
		filters_[3] = null;
	}

	public void selectAction(ViralIsolate selectedItem)
	{
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode()
        	.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTherapyTreeNode()
        	.getSelectedActionItem().expand();
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTherapyTreeNode()
        	.getSelectedActionItem().refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTherapyTreeNode()
        	.getViewActionItem().selectNode();
	}

	public boolean[] sortableFields()
	{
		return sortable_;
	}

	public int[] getColumnWidths() {
		return colWidths;
	}

	public String[] getRowTooltips(ViralIsolate type) {
		return null;
	}
}
