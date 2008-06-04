package net.sf.regadb.ui.datatable.patient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.regadb.util.pair.Pair;
import net.sf.regadb.util.settings.RegaDBSettings;

public class IPatientDataTable implements IDataTable<Pair<Patient,PatientAttributeValue>>
{
	private List<String> colNames_ = new ArrayList<String>(5);

	private List<IFilter> filters_ = new ArrayList<IFilter>(5);

	private List<String> filterVarNames_ = new ArrayList<String>(5);
	
	private List<Boolean> sortable_ = new ArrayList<Boolean>(5);

	private AttributeFilter attributeFilter_;
	
	public IPatientDataTable()
	{

	}

	public void init(Transaction t)
	{
	    setAttributeFilter(new AttributeFilter(t,getDefaultAttribute(t)));
	    
	    addColumn("dataTable.patient.colName.dataSet", new DatasetFilter(t), "dataset.description", true);
	    addColumn("dataTable.patient.colName.patientId", new StringFilter(), "patient.patientId", true);
	    addColumn("dataTable.patient.colName.name", new StringFilter(), "patient.lastName", true);
	    addColumn("dataTable.patient.colName.surName", new StringFilter(), "patient.firstName", true);
	    addColumn("dataTable.patient.colName.attribute", getAttributeFilter(), "attributeValue.value", true);
	}
	
	public void addColumn(String colName, IFilter filter, String varName, boolean sortable){
	    colNames_.add(colName);
	    filters_.add(filter);
	    filterVarNames_.add(varName);
	    sortable_.add(sortable);
	}

	public String[] getColNames()
	{
		return colNames_.toArray(new String[colNames_.size()]);
	}

	public List<Pair<Patient,PatientAttributeValue>> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortColIndex, boolean ascending)
	{
	    if(!nullAttribute()){
    	    HibernateFilterConstraint hfc = HibernateStringUtils.filterConstraintsQuery(this);
    	    hfc.addArgument("attributeIi", getAttributeFilter().getAttribute().getAttributeIi());
    	    
    	    if(ValueTypes.getValueType(getAttributeFilter().getAttribute().getValueType()) == ValueTypes.NOMINAL_VALUE){
    	    	return t.getPatientPatientAttributeNominalValues(startIndex, amountOfRows, filterVarNames_.get(sortColIndex), ascending, hfc);
    	    }
    	    else{
    	        return t.getPatientPatientAttributeValues(startIndex, amountOfRows, filterVarNames_.get(sortColIndex), ascending, hfc);
    	    }
	    }
	    else{
	        List<Patient> l = t.getPatients(startIndex, amountOfRows, filterVarNames_.get(sortColIndex), ascending, HibernateStringUtils.filterConstraintsQuery(this));
	        List<Pair<Patient,PatientAttributeValue>> ret = new ArrayList<Pair<Patient,PatientAttributeValue>>();
	        
	        for(Patient p : l){
	            ret.add(new Pair<Patient,PatientAttributeValue>(p,null));
	        }
	        return ret;
	    }
	}

	public IFilter[] getFilters()
	{
		return filters_.toArray(new IFilter[filters_.size()]);
	}

	public String[] getRowData(Pair<Patient, PatientAttributeValue> type)
	{
		String[] toReturn = new String[colNames_.size()];

		Set<Dataset> dataSets = type.getKey().getDatasets();
		for (Dataset set : dataSets)
		{
			if (set.getClosedDate() == null)
			{
				toReturn[0] = set.getDescription();
			}
		}
		toReturn[1] = type.getKey().getPatientId();
		toReturn[2] = type.getKey().getFirstName();
		toReturn[3] = type.getKey().getLastName();
		
		if(!nullAttribute() && type.getValue() != null){
		    ValueTypes vt = ValueTypes.getValueType(type.getValue().getAttribute().getValueType());
    		if(vt == ValueTypes.NOMINAL_VALUE)
    		    toReturn[4] = type.getValue().getAttributeNominalValue().getValue();
    		else if(vt == ValueTypes.DATE)
    		    toReturn[4] = DateUtils.getEuropeanFormat(type.getValue().getValue());
    		else
    		    toReturn[4] = type.getValue().getValue();
		}
		else{
		    toReturn[4] = "";
		}

		return toReturn;
	}

    public String[] getFieldNames() 
    {
        return filterVarNames_.toArray(new String[filterVarNames_.size()]);
    }
    
    public long getDataSetSize(Transaction t)
    {
        if(!nullAttribute()){
            HibernateFilterConstraint hfc = HibernateStringUtils.filterConstraintsQuery(this);
            hfc.addArgument("attributeIi", getAttributeFilter().getAttribute().getAttributeIi());
            
            if(ValueTypes.getValueType(getAttributeFilter().getAttribute().getValueType()) == ValueTypes.NOMINAL_VALUE)
                return t.getPatientPatientAttributeNominalValuesCount(hfc);
            else
                return t.getPatientPatientAttributeValuesCount(hfc);
        }
        else{
            return t.getPatientCount(HibernateStringUtils.filterConstraintsQuery(this));
        }
    }

    public void selectAction(Pair<Patient,PatientAttributeValue> selectedItem) 
    {
        RegaDBMain.getApp().getTree().getTreeContent().patientSelected.setSelectedItem(selectedItem.getKey());
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
        List<Attribute> l;
        
        String dftAttr = RegaDBSettings.getInstance().getDefaultValue("datatable.patient.attribute");
        if(dftAttr != null){
            l = t.getAttributes(dftAttr);
        }
        else{
            l = t.getAttributes();
        }
        
        if(l.size() > 0)
            a = l.get(0);
        
        return a;
    }
}
