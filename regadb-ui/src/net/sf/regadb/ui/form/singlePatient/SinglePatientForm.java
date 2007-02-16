package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.expandtable.TableExpander;
import net.sf.regadb.util.pair.Pair;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WTable;

public class SinglePatientForm extends WContainerWidget implements IForm
{
    private ArrayList<IFormField> formFields_ = new ArrayList<IFormField>();
    
    private WGroupBox formGroup_;
    
    //general group
    private WGroupBox generalGroup_;
    private WTable generalGroupTable_;
    private Label sourceDatasetL;
    private TextField sourceDatasetTF;
    private Label idL;
    private TextField idTF;
    private Label firstNameL;
    private TextField firstNameTF;
    private Label lastNameL;
    private TextField lastNameTF;
    private Label birthDateL;
    private TextField birthDateTF;
    private Label deathDateL;
    private TextField deathDateTF;
    
    //attributes
    private WGroupBox attributesGroup_;
    private WTable attributesGroupTable_;
    private ArrayList<Pair<IFormField, PatientAttributeValue>> attributeList_ = new ArrayList<Pair<IFormField, PatientAttributeValue>>(); 
    
    private boolean editable_;
    
    public SinglePatientForm(boolean editable, WMessage formName)
	{
        super(null);
        editable_ = editable;
        formGroup_ = new WGroupBox(formName, this);
        init();
	}
    
    public void init()
    {   
        //general group
        generalGroup_ = new WGroupBox(tr("form.singlePatient.editView.general"), formGroup_);
        generalGroupTable_ = new WTable(generalGroup_);
        sourceDatasetL = new Label(tr("form.singlePatient.editView.sourceDataset"));
        sourceDatasetTF = new TextField(editable_, this);
        addLineToTable(generalGroupTable_, sourceDatasetL, sourceDatasetTF);
        idL = new Label(tr("form.singlePatient.editView.patientId"));
        idTF = new TextField(editable_, this);
        addLineToTable(generalGroupTable_, idL, idTF);
        firstNameL = new Label(tr("form.singlePatient.editView.firstName"));
        firstNameTF = new TextField(editable_, this);
        addLineToTable(generalGroupTable_, firstNameL, firstNameTF);
        lastNameL = new Label(tr("form.singlePatient.editView.lastName"));
        lastNameTF = new TextField(editable_, this);
        addLineToTable(generalGroupTable_, lastNameL, lastNameTF);
        birthDateL = new Label(tr("form.singlePatient.editView.birthDate"));
        birthDateTF = new TextField(editable_, this);
        addLineToTable(generalGroupTable_, birthDateL, birthDateTF);
        deathDateL = new Label(tr("form.singlePatient.editView.deathDate"));
        deathDateTF = new TextField(editable_, this);
        addLineToTable(generalGroupTable_, deathDateL, deathDateTF);
        
        fillData(RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedPatient());
    }
    
    public void addLineToTable(WTable table, Label label, IFormField field)
    {
        int numRows = table.numRows();
        table.putElementAt(numRows, 0, label);
        table.putElementAt(numRows, 1, field.getWidget());
        label.setBuddy(field);
    }
    
    public void fillData(Patient patient)
    {
        sourceDatasetTF.setText("IMPLEMENT");
        idTF.setText(patient.getPatientId());
        firstNameTF.setText(patient.getFirstName());
        lastNameTF.setText(patient.getLastName());
        if(patient.getBirthDate()!=null)
        birthDateTF.setText(patient.getBirthDate().toString());
        if(patient.getDeathDate()!=null)
        deathDateTF.setText(patient.getDeathDate().toString());
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.update(patient);
        List<Attribute> attributes;
        if(editable_)
        {
            attributes = t.getAttributes();
        }
        else
        {
            attributes = new ArrayList<Attribute>();
            for(PatientAttributeValue attributeValue : patient.getPatientAttributeValues())
            {
                attributes.add(attributeValue.getId().getAttribute());
            }
        }
        
        List<PatientAttributeValue> attributeValueMap = new ArrayList<PatientAttributeValue>();
        for(PatientAttributeValue attributeValue : patient.getPatientAttributeValues())
        {
            attributeValueMap.add(attributeValue);
        }
                
        HashMap<String, ArrayList<Pair<Attribute, PatientAttributeValue>>> groups = groupAttributes(attributes, attributeValueMap, t);
        
        if(groups.entrySet().size()>0)
        {
            attributesGroup_ = new WGroupBox(tr("form.singlePatient.editView.attributes"), formGroup_);
            attributesGroupTable_ = new WTable(attributesGroup_);
            
            int numRows;
            TableExpander attributeGroup;
            WMessage groupMessage;
            Label attributeLabel;
            TextField attributeFieldTF;
            ComboBox attributeFieldCB;
            AttributeNominalValue selectedNominalVal;
            for(Map.Entry<String, ArrayList<Pair<Attribute, PatientAttributeValue>>> entry : groups.entrySet())
            {
                numRows = attributesGroupTable_.numRows();
                groupMessage = (entry.getKey().equals("form.singlePatient.editView.generalAttribute")?tr(entry.getKey()):lt(entry.getKey()));
                attributeGroup = new TableExpander(groupMessage, attributesGroupTable_, numRows);
                for(Pair<Attribute, PatientAttributeValue> attrEl : entry.getValue())
                {
                    numRows = attributesGroupTable_.numRows();
                    attributeLabel = new Label(lt(attrEl.getKey().getName()));
                    attributesGroupTable_.putElementAt(numRows, 1, attributeLabel);
                    if(attrEl.getKey().getValueType().getDescription().equals("nominal value"))
                    {
                        attributeFieldCB = new ComboBox(editable_, this);
                        attributesGroupTable_.putElementAt(numRows, 2, attributeFieldCB);
                        attributeFieldCB.addNoSelectionItem();
                        for(AttributeNominalValue nominalVal : attrEl.getKey().getAttributeNominalValues())
                        {
                            attributeFieldCB.addItem(lt(nominalVal.getValue()));
                        }
                        if(attrEl.getValue()!=null)
                        {
                            selectedNominalVal = attrEl.getValue().getAttributeNominalValue();
                            if(selectedNominalVal!=null)
                            {
                                attributeFieldCB.selectItem(lt(selectedNominalVal.getValue()));
                            }
                        }
                    }
                    else
                    {
                        attributeFieldTF = new TextField(editable_, this);
                        attributesGroupTable_.putElementAt(numRows, 2, attributeFieldTF);
                    }
                }
            }
        }
        
        t.commit();
    }
    
    public HashMap<String, ArrayList<Pair<Attribute, PatientAttributeValue>>> groupAttributes(List<Attribute> attributes, List<PatientAttributeValue> attributeValueList, Transaction t)
    {
        HashMap<String, ArrayList<Pair<Attribute, PatientAttributeValue>>> groups = new HashMap<String, ArrayList<Pair<Attribute, PatientAttributeValue>>>();
        
        String groupName;
        ArrayList<Pair<Attribute, PatientAttributeValue>> listOfAttributesInOneGroup;
        PatientAttributeValue value;
        for(Attribute attribute : attributes)
        {
            groupName = attribute.getAttributeGroup();
            if(groupName==null)
            {
                groupName = "form.singlePatient.editView.generalAttribute";
            }
            listOfAttributesInOneGroup = groups.get(groupName);
            if(listOfAttributesInOneGroup==null)
            {
                listOfAttributesInOneGroup = new ArrayList<Pair<Attribute, PatientAttributeValue>>();
                groups.put(groupName, listOfAttributesInOneGroup);
            }
            value = null;
            for(PatientAttributeValue attributeValue : attributeValueList)
            {
                if(attributeValue.getId().getAttribute().getAttributeIi().equals(attribute.getAttributeIi()))
                {
                    value = attributeValue;
                    break;
                }
            }
            listOfAttributesInOneGroup.add(new Pair<Attribute, PatientAttributeValue>(attribute,value));
        }

        return groups;
    }
	
	public WContainerWidget getWContainer()
	{
		return this;
	}

	public void addFormField(IFormField field)
	{
        formFields_.add(field);
	}

    public void performCollapse() 
    {
        
    }

    public void performExpand() 
    {
        
    }
}
