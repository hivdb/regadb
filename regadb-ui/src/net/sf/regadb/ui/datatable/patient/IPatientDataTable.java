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
import net.sf.regadb.ui.framework.widgets.datatable.TimestampFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.regadb.util.settings.AttributeConfig;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WString;

public class IPatientDataTable implements IDataTable<Object[]>
{
	private List<CharSequence> colNames_ = new ArrayList<CharSequence>();

	private List<IFilter> filters_ = new ArrayList<IFilter>();

	private List<String> filterVarNames_ = new ArrayList<String>();
	
	private List<Boolean> sortable_ = new ArrayList<Boolean>();

	private List<Integer> widths = new ArrayList<Integer>();
	
	
	private AttributeFilter attributeFilter_;
	
	private List<Attribute> attributes = new ArrayList<Attribute>();
	
	public IPatientDataTable()
	{
	}

	public void init(Transaction t)
	{
	    setAttributeFilter(new AttributeFilter(t,getDefaultAttribute(t)));
	    
	    for(AttributeConfig ai : RegaDBSettings.getInstance().getInstituteConfig().getSelectPatientFormConfig().getAttributes()){
	    	Attribute a = t.getAttribute(ai.getName(), ai.getGroup());
	    	if(a != null)
	    		attributes.add(a);
	    }
	    
	    int width = 100 / (4 + attributes.size());
	    
	    addColumn(WString.tr("dataTable.patient.colName.dataSet"), new DatasetFilter(t), "dataset.description", true,width);
	    addColumn(WString.tr("dataTable.patient.colName.patientId"), new StringFilter(), "p.patientId", true,width);
	    
	    int i=0;
	    for(Attribute attribute : attributes){
	        IFilter filter = AttributeFilter.createFilter(attribute, t);
	        addColumn(attribute.getName(), filter, "av"+ (i++) +".value", true, width);
	    }
	    
	    addColumn(WString.tr("dataTable.patient.colName.attribute"), getAttributeFilter(), "av"+ (i) +".value", true, width);
	    
	    if(RegaDBSettings.getInstance().getInstituteConfig().getSelectPatientFormConfig().getShowSampleIds())
	        addColumn(WString.tr("dataTable.patient.colName.sampleId"), new SampleIdFilter(), "vi.sampleId", false, width);
	}
	
	public void addColumn(CharSequence colName, IFilter filter, String varName, boolean sortable, int width){
	    colNames_.add(colName);
	    filters_.add(filter);
	    filterVarNames_.add(varName);
	    sortable_.add(sortable);
	    widths.add(width);
	}

	public CharSequence[] getColNames()
	{
		return colNames_.toArray(new CharSequence[colNames_.size()]);
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
        
        String sort = filterVarNames_.get(sortColIndex);
        IFilter f = filters_.get(sortColIndex); 
        if(f != null){
        	if( f instanceof AttributeFilter ){
        		if (nullAttribute())
        			sort = "p.patientId";
        		else if(((AttributeFilter)f).getFilter() instanceof TimestampFilter)
        			sort = "cast("+ sort +",long)";
        	}
        	else if(f instanceof TimestampFilter)
        		sort = "cast("+ sort +",long)";
        }
        
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

		if(RegaDBSettings.getInstance().getInstituteConfig().getSelectPatientFormConfig().getShowSampleIds()){
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
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.setSelectedItem((Patient)selectedItem[0]);
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.expand();
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViewActionItem().selectNode();
    
        clearItems();
    }
    
    public static void clearItems() {
        ArrayList<TreeMenuNode> patientAttributes = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getChildren();
        
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
    	AttributeConfig ac = RegaDBSettings.getInstance().getInstituteConfig().getSelectPatientFormConfig().getAttributeFilter();
    	if(ac != null)
    		return t.getAttribute(ac.getName(), ac.getGroup());
    	return null;
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
		
		if(RegaDBSettings.getInstance().getInstituteConfig().getSelectPatientFormConfig().getShowSampleIds()){
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
		}
		return toReturn;
	}
}
