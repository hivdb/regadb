package net.sf.regadb.ui.form.singlePatient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.sf.regadb.ui.framework.IntegratedRegaDBApplication;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.LimitedNumberField;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.expandtable.TableExpander;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.pair.Pair;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;

public class SinglePatientForm extends ObjectForm<Patient>
{
    //general group
    private WGroupBox generalGroup_;
    private FormTable generalGroupTable_;
    private Label sourceDatasetL;
    private ComboBox<Dataset> sourceDatasetCB;
    private Label idL;
    private TextField idTF;
    
    private WGroupBox attributesGroup_;
    private WTable attributesGroupTable_;
    private ArrayList<Pair<IFormField, PatientAttributeValue>> attributeList_ = new ArrayList<Pair<IFormField, PatientAttributeValue>>();
    
    private HashMap<Label, Attribute> attributePairs_ = new HashMap<Label, Attribute>();
    
    public SinglePatientForm(WString formName, InteractionState interactionState, ObjectTreeNode<Patient> node, Patient object)
	{
        super(formName, interactionState, node, object);
        init();
	}
    
    public void init()
    {   
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
        generalGroupTable_.getColumnCount();
        
        fillData(getObject());
        
        addControlButtons();
    }
    
    private boolean checkPatientId(String id){
        boolean unique=true;
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = t.getPatient(sourceDatasetCB.currentValue(), id);
        if(p != null && !p.getPatientIi().equals(getObject().getPatientIi())){
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
        else if(RegaDBMain.getApp() instanceof IntegratedRegaDBApplication){
            patient.setPatientId(((IntegratedRegaDBApplication)RegaDBMain.getApp()).getPatientId());
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
            CharSequence groupMessage;
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
                    groupMessage = entry.getKey();
                    rowToPlace = attributesGroupTable_.getRowCount();
                }
                addRowIfNotEmpty(rowToPlace);
                attributeGroup = new TableExpander(groupMessage, attributesGroupTable_, rowToPlace);
                attributesGroupTable_.getElementAt(rowToPlace, 0).setColumnSpan(2);
                
                for(Pair<Attribute, PatientAttributeValue> attrEl : entry.getValue())
                {
                    rowToPlace++;
                    addRowIfNotEmpty(rowToPlace);
                    attributeLabel = new Label(attrEl.getKey().getName());
                    attributePairs_.put(attributeLabel, attrEl.getKey());
                    attributesGroupTable_.getElementAt(rowToPlace, 0).addWidget(attributeLabel);
                    attributesGroupTable_.getElementAt(rowToPlace, 0).setStyleClass("form-label-area");
                    if(attrEl.getKey().getValueType().getDescription().equals("nominal value"))
                    {
                        attributeFieldCB = new ComboBox<AttributeNominalValue>(getInteractionState(), this);
                        attributesGroupTable_.getElementAt(rowToPlace, 1).addWidget(attributeFieldCB);
                        
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
                                attributeFieldTF.setText(DateUtils.format(attrEl.getValue().getValue()));
                            }
                            else
                                attributeFieldTF.setText(attrEl.getValue().getValue());
                        }
                        attributesGroupTable_.getElementAt(rowToPlace, 1).addWidget(attributeFieldTF);
                    }
                    attributesGroupTable_.getElementAt(rowToPlace, 1).setStyleClass("form-value-area");
                }
                attributeGroup.expand(true);
            }
        }
        
        t.commit();
    }
    
    public void addRowIfNotEmpty(int row)
    {
        if(attributesGroupTable_.getElementAt(row, 0).getChildren().size()!=0 || attributesGroupTable_.getElementAt(row, 1).getChildren().size()!=0)
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
        
        if(getObject().getPatientIi()!=null)
        {
            t.attach(getObject());
        }
        
        if(getInteractionState() == InteractionState.Adding)
        {
            getObject().setSourceDataset(sourceDatasetCB.currentValue(), t);
        }
        
        getObject().setPatientId(getNulled(idTF.text()));
        
        Object label;
        Object tf;
        String text;
        DataComboMessage message;
        Attribute attribute;
        if(attributesGroupTable_!=null)
        {
        for(int row = 0; row < attributesGroupTable_.getRowCount(); row++)
        {
                label = attributesGroupTable_.getElementAt(row, 0).getChildren().get(0);
                if(label instanceof Label)
                {
                    attribute = attributePairs_.get(label);
                    tf = attributesGroupTable_.getElementAt(row, 1).getChildren().get(0);
                    PatientAttributeValue attributeValue = getObject().getAttributeValue(attribute);
    
                    if(tf instanceof TextField)
                    {
                        text = ((TextField)tf).text();
                        storeAttributeTF(text, attributeValue, attribute, getObject(), t);
                    }
                    else if(tf instanceof LimitedNumberField)
                    {
                        text = ((LimitedNumberField)tf).text();
                        storeAttributeTF(text, attributeValue, attribute, getObject(), t);
                    }
                    else if(tf instanceof DateField)
                    {
                        text = ((DateField)tf).text();
                        storeAttributeTF(text, attributeValue, attribute, getObject(), t);
                    }
                    else if(tf instanceof ComboBox)
                    {
                        message = ((ComboBox)tf).currentItem();
                        
                        if(message!=null)
                        {
                            if(attributeValue==null)
                            {
                            attributeValue = getObject().createPatientAttributeValue(attribute);
                            }
                            attributeValue.setAttributeNominalValue(((DataComboMessage<AttributeNominalValue>)message).getDataValue());
                        }
                        else if(attributeValue!=null)
                        {
                        	getObject().getPatientAttributeValues().remove(attributeValue);
                            t.delete(attributeValue);
                        }
                    }
            }
        }
        }

        update(getObject(), t);
        t.commit();
    }
    
    @Override
    public void cancel()
    {
    }
    
    @Override
    public WString deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        t.delete(getObject());
        
        t.commit();
        
        return null;
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
