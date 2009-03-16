package net.sf.regadb.ui.form.singlePatient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.LimitedNumberField;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.expandtable.TableExpander;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.pair.Pair;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;

public class SinglePatientForm extends FormWidget
{
    //general group
    private WGroupBox generalGroup_;
    private FormTable generalGroupTable_;
    private Label sourceDatasetL;
    private ComboBox<Dataset> sourceDatasetCB;
    private Label idL;
    private TextField idTF;
    private Label firstNameL;
    private TextField firstNameTF;
    private Label lastNameL;
    private TextField lastNameTF;
    private Label birthDateL;
    private DateField birthDateTF;
    private Label deathDateL;
    private DateField deathDateTF;
    
    //attributes
    private Set<String> excludeList = new HashSet<String>();
    
    private WGroupBox attributesGroup_;
    private WTable attributesGroupTable_;
    private ArrayList<Pair<IFormField, PatientAttributeValue>> attributeList_ = new ArrayList<Pair<IFormField, PatientAttributeValue>>();
    
    private HashMap<Label, Attribute> attributePairs_ = new HashMap<Label, Attribute>();
    
    private Patient patient_;
    
    public SinglePatientForm(InteractionState state, WString formName, Patient patient)
	{
        super(formName, state);
        patient_ = patient;
        init();
	}
    
    public void init()
    {
        addExclude(Patient.FIRST_NAME);
        addExclude(Patient.LAST_NAME);
        addExclude(Patient.BIRTH_DATE);
        addExclude(Patient.DEATH_DATE);
        
        //general group
        generalGroup_ = new WGroupBox(tr("form.singlePatient.editView.general"), this);
        generalGroupTable_ = new FormTable(generalGroup_);
        sourceDatasetL = new Label(tr("form.singlePatient.editView.sourceDataset"));
        sourceDatasetCB = new ComboBox<Dataset>(getInteractionState()==InteractionState.Adding?InteractionState.Adding:InteractionState.Viewing, this);
        sourceDatasetCB.setMandatory(true);
        generalGroupTable_.addLineToTable(sourceDatasetL, sourceDatasetCB);
        idL = new Label(tr("form.singlePatient.editView.patientId"));
        idTF = new TextField(getInteractionState(), this){
                public boolean checkUniqueness(){
                    return checkPatientId(getFormText());
                }
            
            };
        idTF.setMandatory(true);
        idTF.setUnique(true);
        generalGroupTable_.addLineToTable(idL, idTF);
        firstNameL = new Label(tr("form.singlePatient.editView.firstName"));
        firstNameTF = new TextField(getInteractionState(), this);
        generalGroupTable_.addLineToTable(firstNameL, firstNameTF);
        lastNameL = new Label(tr("form.singlePatient.editView.lastName"));
        lastNameTF = new TextField(getInteractionState(), this);
        generalGroupTable_.addLineToTable(lastNameL, lastNameTF);
        birthDateL = new Label(tr("form.singlePatient.editView.birthDate"));
        birthDateTF = new DateField(getInteractionState(), this, RegaDBSettings.getInstance().getDateFormat());
        generalGroupTable_.addLineToTable(birthDateL, birthDateTF);
        deathDateL = new Label(tr("form.singlePatient.editView.deathDate"));
        deathDateTF = new DateField(getInteractionState(), this, RegaDBSettings.getInstance().getDateFormat());
        generalGroupTable_.addLineToTable(deathDateL, deathDateTF);
        /*WPushButton export = new WPushButton(lt("Export Patient"),generalGroupTable_.elementAt(generalGroupTable_.numRows(), 0));
        export.clicked.addListener(new SignalListener<WMouseEvent>()
        {
            public void notify(WMouseEvent a) 
            {
                final Patient pt = patient_;
                File tmpFile = RegaDBMain.getApp().createTempFile("patient-export", ".xml");
                exportXML(tmpFile.getAbsolutePath(), pt);
                final WTable parent = generalGroupTable_;
                WAnchor anchor = new WAnchor(new WFileResource("xml", tmpFile.getAbsolutePath()), WWidget.lt("patient xml file"), parent.elementAt(parent.numRows()-1, 1));
            }
        });*/
        generalGroupTable_.columnCount();
        
        fillData(patient_);
        
        addControlButtons();
    }
    
    private void addExclude(String attribute){
        excludeList.add(attribute);
    }
    private boolean isExcluded(Attribute attribute){
        return excludeList.contains(attribute.getName());
    }
    
    private boolean checkPatientId(String id){
        boolean unique=true;
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = t.getPatient(sourceDatasetCB.currentValue(), id);
        if(p != null && !p.getPatientIi().equals(patient_.getPatientIi())){
            unique = false;
        }
        
        t.commit();
        return unique;
    }
    
    private void exportXML(String fileName, Patient pt) {
        ExportToXML l = new ExportToXML();
        Element root = new Element("patients");
        
        Element patient_el = new Element("patients-el");
        root.addContent(patient_el);
        l.writePatient(pt, patient_el);        
        
        Document n = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());

        java.io.FileWriter writer;
        try {
            writer = new java.io.FileWriter(fileName);
            outputter.output(n, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void fillData(Patient patient)
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        if(patient.getPatientIi()!=null)
        {
            t.attach(patient);
        }
        
        for(Dataset ds : t.getCurrentUsersDatasets(Privileges.READWRITE))
        {
            if(ds.getClosedDate()==null)
            {
                sourceDatasetCB.addItem(new DataComboMessage<Dataset>(ds, ds.getDescription()));
            }
        }
        sourceDatasetCB.sort();
        for(Dataset ds : patient.getDatasets())
        {
            if(ds.getClosedDate()==null)
            {
                sourceDatasetCB.selectItem(ds.getDescription());
            }
        }
        
        idTF.setText(patient.getPatientId());
        firstNameTF.setText(patient.getFirstName());
        lastNameTF.setText(patient.getLastName());
        birthDateTF.setDate(patient.getBirthDate());
        deathDateTF.setDate(patient.getDeathDate());
        
        List<Attribute> attributes;
        if(isEditable())
        {
            attributes = t.getAttributes();
        }
        else
        {
            attributes = new ArrayList<Attribute>();
            for(PatientAttributeValue attributeValue : patient.getPatientAttributeValues())
            {
                attributes.add(attributeValue.getAttribute());
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
            attributesGroup_ = new WGroupBox(tr("form.singlePatient.editView.attributes"), this);
            attributesGroupTable_ = new WTable(attributesGroup_);
            attributesGroupTable_.setStyleClass("datatable");
            int rowToPlace;
            TableExpander attributeGroup;
            WString groupMessage;
            Label attributeLabel;
            FormField attributeFieldTF = null;
            ComboBox<AttributeNominalValue> attributeFieldCB;
            AttributeNominalValue selectedNominalVal;
            for(Map.Entry<String, ArrayList<Pair<Attribute, PatientAttributeValue>>> entry : groups.entrySet())
            {
                if(entry.getKey().equals("form.singlePatient.editView.generalAttribute"))
                {
                    groupMessage = tr(entry.getKey());
                    rowToPlace = 0;
                }
                else
                {
                    groupMessage = lt(entry.getKey());
                    rowToPlace = attributesGroupTable_.rowCount();
                }
                addRowIfNotEmpty(rowToPlace);
                attributeGroup = new TableExpander(groupMessage, attributesGroupTable_, rowToPlace);
                attributesGroupTable_.elementAt(rowToPlace, 0).setColumnSpan(2);
                
                for(Pair<Attribute, PatientAttributeValue> attrEl : entry.getValue())
                {
                    if(isExcluded(attrEl.getKey()))
                        continue;
                    
                    rowToPlace++;
                    addRowIfNotEmpty(rowToPlace);
                    attributeLabel = new Label(lt(attrEl.getKey().getName()));
                    attributePairs_.put(attributeLabel, attrEl.getKey());
                    attributesGroupTable_.elementAt(rowToPlace, 0).addWidget(attributeLabel);
                    attributesGroupTable_.elementAt(rowToPlace, 0).setStyleClass("form-label-area");
                    if(attrEl.getKey().getValueType().getDescription().equals("nominal value"))
                    {
                        attributeFieldCB = new ComboBox<AttributeNominalValue>(getInteractionState(), this);
                        attributesGroupTable_.elementAt(rowToPlace, 1).addWidget(attributeFieldCB);
                        
                        for(AttributeNominalValue nominalVal : attrEl.getKey().getAttributeNominalValues())
                        {
                        	attributeFieldCB.addItem(new DataComboMessage<AttributeNominalValue>(nominalVal,nominalVal.getValue()));
                        }
                        
                        attributeFieldCB.sort();
                        	
                        //do this after the sort, should be on top
                        attributeFieldCB.addNoSelectionItem();
                        
                        if(attrEl.getValue()!=null)
                        {
                            selectedNominalVal = attrEl.getValue().getAttributeNominalValue();
                            if(selectedNominalVal!=null)
                            {
                                attributeFieldCB.selectItem(selectedNominalVal.getValue());
                            }
                        }
                    }
                    else
                    {
                        ValueTypes vt = ValueTypes.getValueType(attrEl.getKey().getValueType());
                    	attributeFieldTF = getTextField(vt);
                        if(attrEl.getValue()!=null && attrEl.getValue().getValue()!=null)
                        {
                            if(vt == ValueTypes.DATE){
                                attributeFieldTF.setText(DateUtils.getEuropeanFormat(attrEl.getValue().getValue()));
                            }
                            else
                                attributeFieldTF.setText(attrEl.getValue().getValue());
                        }
                        attributesGroupTable_.elementAt(rowToPlace, 1).addWidget(attributeFieldTF);
                    }
                    attributesGroupTable_.elementAt(rowToPlace, 1).setStyleClass("form-value-area");
                }
                attributeGroup.expand();
            }
        }
        
        t.commit();
    }
    
    public void addRowIfNotEmpty(int row)
    {
        if(attributesGroupTable_.elementAt(row, 0).children().size()!=0 || attributesGroupTable_.elementAt(row, 1).children().size()!=0)
        {
            attributesGroupTable_.insertRow(row);
        }
    }
    
    public HashMap<String, ArrayList<Pair<Attribute, PatientAttributeValue>>> groupAttributes(List<Attribute> attributes, List<PatientAttributeValue> attributeValueList, Transaction t)
    {
        HashMap<String, ArrayList<Pair<Attribute, PatientAttributeValue>>> groups = new HashMap<String, ArrayList<Pair<Attribute, PatientAttributeValue>>>();
        
        AttributeGroup groupName;
        String groupStr;
        ArrayList<Pair<Attribute, PatientAttributeValue>> listOfAttributesInOneGroup;
        PatientAttributeValue value;
        for(Attribute attribute : attributes)
        {
            groupName = attribute.getAttributeGroup();
            if(groupName==null)
            {
                groupStr = "form.singlePatient.editView.generalAttribute";
            }
            else
            {
                groupStr = groupName.getGroupName();
            }
            listOfAttributesInOneGroup = groups.get(groupStr);
            if(listOfAttributesInOneGroup==null)
            {
                listOfAttributesInOneGroup = new ArrayList<Pair<Attribute, PatientAttributeValue>>();
                groups.put(groupStr, listOfAttributesInOneGroup);
            }
            value = null;
            for(PatientAttributeValue attributeValue : attributeValueList)
            {
                if(attributeValue.getAttribute().getAttributeIi().equals(attribute.getAttributeIi()))
                {
                    value = attributeValue;
                    break;
                }
            }
            listOfAttributesInOneGroup.add(new Pair<Attribute, PatientAttributeValue>(attribute,value));
        }

        return groups;
    }
    
    public void saveData()
    {
    	if(getNulled(idTF.text()) == null)
    		return;
    	
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        if(patient_.getPatientIi()!=null)
        {
            t.attach(patient_);
        }
        
        if(getInteractionState() == InteractionState.Adding)
        {
            patient_.setSourceDataset(sourceDatasetCB.currentValue(), t);
        }
        
        patient_.setPatientId(getNulled(idTF.text()));
        patient_.setFirstName(t, getNulled(firstNameTF.text()));
        patient_.setLastName(t, getNulled(lastNameTF.text()));
     
        patient_.setBirthDate(t, birthDateTF.getDate());
        patient_.setDeathDate(t, deathDateTF.getDate());
        
        Object label;
        Object tf;
        String text;
        DataComboMessage message;
        Attribute attribute;
        if(attributesGroupTable_!=null)
        {
        for(int row = 0; row < attributesGroupTable_.rowCount(); row++)
        {
                label = attributesGroupTable_.elementAt(row, 0).children().get(0);
                if(label instanceof Label)
                {
                    attribute = attributePairs_.get(label);
                    tf = attributesGroupTable_.elementAt(row, 1).children().get(0);
                    PatientAttributeValue attributeValue = patient_.getAttributeValue(attribute);
    
                    if(tf instanceof TextField)
                    {
                        text = ((TextField)tf).text();
                        storeAttributeTF(text, attributeValue, attribute, patient_, t);
                    }
                    else if(tf instanceof LimitedNumberField)
                    {
                        text = ((LimitedNumberField)tf).text();
                        storeAttributeTF(text, attributeValue, attribute, patient_, t);
                    }
                    else if(tf instanceof DateField)
                    {
                        text = ((DateField)tf).text();
                        storeAttributeTF(text, attributeValue, attribute, patient_, t);
                    }
                    else if(tf instanceof ComboBox)
                    {
                        message = ((ComboBox)tf).currentItem();
                        
                        if(message!=null)
                        {
                            if(attributeValue==null)
                            {
                            attributeValue = patient_.createPatientAttributeValue(attribute);
                            }
                            attributeValue.setAttributeNominalValue(((DataComboMessage<AttributeNominalValue>)message).getValue());
                        }
                        else if(attributeValue!=null)
                        {
                        	patient_.getPatientAttributeValues().remove(attributeValue);
                            t.delete(attributeValue);
                        }
                    }
            }
        }
        }

        update(patient_, t);
        t.commit();
        
        RegaDBMain.getApp().getTree().getTreeContent().patientSelected.setSelectedItem(patient_);
        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().patientSelected, RegaDBMain.getApp().getTree().getTreeContent().viewPatient);
    }
    
    @Override
    public void cancel()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().singlePatientMain, RegaDBMain.getApp().getTree().getTreeContent().patientSelect);
        }
        else
        {
            RegaDBMain.getApp().getTree().getTreeContent().patientSelected.setSelectedItem(patient_);
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().patientSelected, RegaDBMain.getApp().getTree().getTreeContent().viewPatient);
        } 
    }
    
    @Override
    public WString deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        t.delete(patient_);
        
        t.commit();
        
        return null;
    }

    @Override
    public void redirectAfterDelete() 
    {
        RegaDBMain.getApp().getTree().getTreeContent().patientSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().patientSelected.setSelectedItem(null);
    }
    
    private void storeAttributeTF(String text, PatientAttributeValue attributeValue, Attribute attribute, Patient p, Transaction t)
    {
        if(!"".equals(text) && text!=null)
        {
            if(attributeValue==null)
            {
            attributeValue = p.createPatientAttributeValue(attribute);
            }
            
            if(ValueTypes.getValueType(attribute.getValueType()) == ValueTypes.DATE)
                attributeValue.setValue(DateUtils.parse(text).getTime()+"");
            else
                attributeValue.setValue(text);
        }
        else if(attributeValue!=null)
        {
            p.getPatientAttributeValues().remove(attributeValue);
            t.delete(attributeValue);
        }
    }
}
