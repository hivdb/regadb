package net.sf.regadb.ui.datatable.patient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WString;

public class IPatientDataTable implements IDataTable<Object[]>
{
	private List<WString> colNames_ = new ArrayList<WString>();

	private List<IFilter> filters_ = new ArrayList<IFilter>();

	private List<String> filterVarNames_ = new ArrayList<String>();
	
	private List<Boolean> sortable_ = new ArrayList<Boolean>();

	private List<Integer> widths = new ArrayList<Integer>();
	
	
	private AttributeFilter attributeFilter_;
	
	private List<String> attributeNames = new ArrayList<String>();
	private List<Attribute> attributes = new ArrayList<Attribute>();
	
	public IPatientDataTable()
	{
	    attributeNames.add(Patient.FIRST_NAME);
	    attributeNames.add(Patient.LAST_NAME);
//	    attributeNames.add(Patient.BIRTH_DATE);
//	    attributeNames.add(Patient.DEATH_DATE);
	}

	public void init(Transaction t)
	{
	    setAttributeFilter(new AttributeFilter(t,getDefaultAttribute(t)));
	    
	    for(String attributeName : attributeNames){
	        attributes.addAll(t.getAttributes(attributeName));
	    }
	    
	    int width = 100 / (4 + attributes.size());
	    
	    addColumn(WString.tr("dataTable.patient.colName.dataSet"), new DatasetFilter(t), "dataset.description", true,width);
	    addColumn(WString.tr("dataTable.patient.colName.patientId"), new StringFilter(), "p.patientId", true,width);
	    
	    int i=0;
	    for(Attribute attribute : attributes){
	        IFilter filter = AttributeFilter.createFilter(attribute, t);
	        addColumn(WString.lt(attribute.getName()), filter, "av"+ (i++) +".value", true, width);
	    }
	    
	    addColumn(WString.tr("dataTable.patient.colName.attribute"), getAttributeFilter(), "av"+ (i) +".value", true, width);
	    addColumn(WString.tr("dataTable.patient.colName.sampleId"), new SampleIdFilter(), "vi.sampleId", false, width);
	}
	
	public void addColumn(WString colName, IFilter filter, String varName, boolean sortable, int width){
	    colNames_.add(colName);
	    filters_.add(filter);
	    filterVarNames_.add(varName);
	    sortable_.add(sortable);
	    widths.add(width);
	}

	public WString[] getColNames()
	{
		return colNames_.toArray(new WString[colNames_.size()]);
	}

	public List<Object[]> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortColIndex, boolean ascending)
	{
        HibernateFilterConstraint hfc = HibernateStringUtils.filterConstraintsQuery(this);
        
        List<Attribute> as;
        if(!nullAttribute()){
            as = new ArrayList<Attribute>();
            as.addAll(attributes);
            as.add(getAttributeFilter().getAttribute());
        }
        else
            as = attributes;
        
        String sort;
        IFilter f = filters_.get(sortColIndex); 
        if(f != null && f instanceof AttributeFilter && nullAttribute())
            sort = "p.patientId";
        else
            sort = filterVarNames_.get(sortColIndex);
        
        return t.getPatientWithAttributeValues(startIndex, amountOfRows, sort, ascending, hfc, as);
	}

	public IFilter[] getFilters()
	{
		return filters_.toArray(new IFilter[filters_.size()]);
	}

	public String[] getRowData(Object[] type)
	{
	    int rowIndex = 0;
	    int objIndex = 0;
	    
		String[] toReturn = new String[colNames_.size()];

		Patient p = (Patient)type[objIndex++];
		Set<Dataset> dataSets = p.getDatasets();
		for (Dataset set : dataSets)
		{
			if (set.getClosedDate() == null)
			{
				toReturn[rowIndex] = set.getDescription();
			}
		}
		
		toReturn[++rowIndex] = p.getPatientId();
		
		for(Attribute attribute : attributes){
		    String value = (String)type[objIndex++];
		    toReturn[++rowIndex] = AttributeFilter.formatValue(attribute, value);
		}
		
		++rowIndex;
		if(!nullAttribute()){
		    String value = (String)type[objIndex++];
		    toReturn[rowIndex] = AttributeFilter.formatValue(getAttributeFilter().getAttribute(), value);
		}
		else
		    toReturn[rowIndex] = "";

		++rowIndex;
		if(p.getViralIsolates().size()>0) {
			ViralIsolate vi = (ViralIsolate)p.getViralIsolates().toArray()[0];
			toReturn[rowIndex] = vi.getSampleId();
			if(p.getViralIsolates().size()>1) {
				toReturn[rowIndex] += ", ...";
			}
		} else {
			toReturn[rowIndex] = "";
		}

		return toReturn;
	}

    public String[] getFieldNames() 
    {
        return filterVarNames_.toArray(new String[filterVarNames_.size()]);
    }
    
    public long getDataSetSize(Transaction t)
    {
        HibernateFilterConstraint hfc = HibernateStringUtils.filterConstraintsQuery(this);
        List<Attribute> as;
        if(!nullAttribute()){
            as = new ArrayList<Attribute>();
            as.addAll(attributes);
            as.add(getAttributeFilter().getAttribute());
        }
        else
            as = attributes;
        
        return t.getPatientWithAttributeValuesCount(hfc,as);
    }

    public void selectAction(Object[] selectedItem) 
    {
        RegaDBMain.getApp().getTree().getTreeContent().patientSelected.setSelectedItem((Patient)selectedItem[0]);
        RegaDBMain.getApp().getTree().getTreeContent().patientSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().patientSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().viewPatient.selectNode();
        
        ArrayList<TreeMenuNode> patientAttributes = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getChildren();
        
        for(TreeMenuNode tn : patientAttributes)
        {
        	ArrayList<TreeMenuNode> children = tn.getChildren();
        	
        	for(TreeMenuNode tmn : children)
        	{
	        	if(tmn instanceof GenericSelectedItem)
	        	{
					((GenericSelectedItem)tmn).setSelectedItem(null);
				}
        	}
        }
    }

	public boolean[] sortableFields()
	{
	    boolean ret[] = new boolean[sortable_.size()];
	    for(int i=0;i<sortable_.size();++i){
	        ret[i] = sortable_.get(i);
	    }
		return ret;
	}

    private void setAttributeFilter(AttributeFilter attributeFilter) {
        this.attributeFilter_ = attributeFilter;
    }

    private AttributeFilter getAttributeFilter() {
        return attributeFilter_;
    }
    
    protected boolean nullAttribute(){
        return getAttributeFilter().getAttribute() == null;
    }
    
    protected Attribute getDefaultAttribute(Transaction t){
        Attribute a = null;
        
        String dftAttr = RegaDBSettings.getInstance().getInstituteConfig().getSelectPatientFormConfig().getAttributeFilter();
        if(dftAttr != null){
            List<Attribute> l = t.getAttributes(dftAttr);
            if(l.size() > 0)
                a = l.get(0);
        }
        
        return a;
    }

	public int[] getColumnWidths() {
	    int ret[] = new int[widths.size()];
	    for(int i=0;i<widths.size();++i){
	        ret[i] = widths.get(i);
	    }
	    return ret;
	}

	public String[] getRowTooltips(Object[] type) {
		String[] toReturn = new String[colNames_.size()];

		for(int i = 0; i < toReturn.length; ++i)
		    toReturn[i] = "";

		Patient p = (Patient)type[0];
		int rowIndex = colNames_.size()-1;
		
		if(p.getViralIsolates().size()>0) {
			int count = 0;
			for(ViralIsolate vi : p.getViralIsolates()) {
				toReturn[rowIndex] += vi.getSampleId();
				++count;
				
				if(count<p.getViralIsolates().size()) {
					toReturn[rowIndex] += ", ";
				}
			}
		}
		return toReturn;
	}
}
