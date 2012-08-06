package net.sf.regadb.ui.form.attributeSettings;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;

public class AttributeForm extends ObjectForm<Attribute>
{
    //general group
    private WGroupBox generalGroup_;
    private FormTable generalGroupTable_;
    private Label nameL;
    private TextField nameTF;
    private Label valueTypeL;
    private ComboBox<ValueType> valueTypeCB;
    private Label validationStringL;
    private TextField validationStringTF;
    private Label groupL;
    private ComboBox<AttributeGroup> groupCB;
    private Label usageL;
    private TextField usageTF;
    
    //nominal values group
    private WGroupBox nominalValuesGroup_;
    private EditableTable<AttributeNominalValue> nominalValuesList_;
    private IAttributeNominalValueDataList iNominalValuesList_;
    
    public AttributeForm(WString formName, InteractionState interactionState, ObjectTreeNode<Attribute> node, Attribute attribute)
    {
        super(formName, interactionState, node, attribute);
        init();
        fillData();
    }
    
    private void init()
    {
        generalGroup_ = new WGroupBox(tr("form.attributeSettings.attribute.editView.general"), this);
        generalGroupTable_ = new FormTable(generalGroup_);

        nameL = new Label(tr("form.attributeSettings.attribute.editView.name"));
        nameTF = new TextField(getInteractionState(), this);
        nameTF.setMandatory(true);
        generalGroupTable_.addLineToTable(nameL, nameTF);

        valueTypeL = new Label(tr("form.attributeSettings.attribute.editView.valueType"));
        valueTypeCB = new ComboBox<ValueType>(getInteractionState(), this);
        valueTypeCB.setMandatory(true);
        generalGroupTable_.addLineToTable(valueTypeL, valueTypeCB);
        
        validationStringL = new Label(tr("form.attributeSettings.attribute.editView.validationString"));
        validationStringTF = new TextField(getInteractionState(), this);
        validationStringTF.setMandatory(false);
        generalGroupTable_.addLineToTable(validationStringL, validationStringTF);

        groupL = new Label(tr("form.attributeSettings.attribute.editView.group"));
        groupCB = new ComboBox<AttributeGroup>(getInteractionState(), this);
        groupCB.setMandatory(true);
        generalGroupTable_.addLineToTable(groupL, groupCB);

        if(getInteractionState()!=InteractionState.Adding)
        {
            usageL = new Label(tr("form.attributeSettings.attribute.editView.usage"));
            usageTF = new TextField(InteractionState.Viewing, null);
            generalGroupTable_.addLineToTable(usageL, usageTF);
        }
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<ValueType> valueTypes = t.getValueTypes();
        for(ValueType vt : valueTypes)
        {
            valueTypeCB.addItem(new DataComboMessage<ValueType>(vt, vt.getDescription()));
        }
        valueTypeCB.sort();
        valueTypeCB.selectIndex(0);
        
        List<AttributeGroup> attributeGroups = t.getAttributeGroups();
        for(AttributeGroup ag : attributeGroups)
        {
            groupCB.addItem(new DataComboMessage<AttributeGroup>(ag, ag.getGroupName()));
        }
        groupCB.sort();
        groupCB.selectIndex(0);

        t.commit();
        
        nominalValuesGroup_ = new WGroupBox(tr("form.attributeSettings.attribute.editView.nominalValues"), this);
                
        addControlButtons();
    }
    
    private void setValidationString(){
        boolean hide = (ValueTypes.getValueType(valueTypeCB.currentValue()) != ValueTypes.STRING);
        validationStringL.setHidden(hide);
        validationStringTF.setHidden(hide);
    }
    
    private void setNominalValuesGroup()
    {
        boolean visible = (ValueTypes.getValueType(valueTypeCB.currentValue()) == ValueTypes.NOMINAL_VALUE);
        
        if(!visible)
        {
            nominalValuesGroup_.setHidden(true);
        }
        else
        {
            nominalValuesGroup_.setHidden(false);
            if(nominalValuesList_!=null)
            {
                nominalValuesGroup_.removeWidget(nominalValuesList_);
            }
            ArrayList<AttributeNominalValue> list = new ArrayList<AttributeNominalValue>();
            if(getInteractionState()!=InteractionState.Adding)
            {
                Transaction t = RegaDBMain.getApp().createTransaction();
                t.attach(getObject());
                
                for(AttributeNominalValue anv : getObject().getAttributeNominalValues())
                {
                    list.add(anv);
                }
                t.commit();
            }
            iNominalValuesList_ = new IAttributeNominalValueDataList(this);
            nominalValuesList_ = new EditableTable<AttributeNominalValue>(nominalValuesGroup_, iNominalValuesList_, list){
            	public boolean canRemove(AttributeNominalValue toRemove){
            		Transaction t = RegaDBMain.getApp().createTransaction();
            		boolean isUsed = t.isUsed(toRemove);
            		t.commit();
            		return !isUsed;
            	}
            };
        }
    }
    
    private void fillData()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
        	setObject(new Attribute());
        }
        
        if(getInteractionState()!=InteractionState.Adding)
        {
            Transaction t = RegaDBMain.getApp().createTransaction();
            
            t.attach(getObject());
            
            nameTF.setText(getObject().getName());
            valueTypeCB.selectItem(getObject().getValueType().getDescription());
            groupCB.selectItem(getObject().getAttributeGroup().getGroupName());
            validationStringTF.setText(getObject().getValidationString());
            
            usageTF.setText(t.getAttributeUsage(getObject())+"");
            
            t.commit();
        }
        
        setValidationString();
        setNominalValuesGroup();
        
        valueTypeCB.addComboChangeListener(new Signal.Listener()
                {
                    public void trigger()
                    {
                    	setValidationString();
                        setNominalValuesGroup();
                    }
                });
    }
    
    @Override
    public void saveData() 
    {
    	WString duplicates = null;
        if(nominalValuesList_!=null) {
        duplicates = nominalValuesList_.warnDuplicatesAndBlanks(0);
        }
        if(duplicates!=null)
        {
        	UIUtils.showWarningMessageBox(this, duplicates);
        	return;
        }
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        if(!(getInteractionState()==InteractionState.Adding))
        {
            t.attach(getObject());
        }
        AttributeGroup ag = groupCB.currentValue();
        t.attach(ag);
        ValueType vt = valueTypeCB.currentValue();
        t.attach(vt);
        getObject().setName(nameTF.text());
        getObject().setValueType(vt);
        getObject().setAttributeGroup(ag);
        getObject().setValidationString(validationStringTF.text());
        
        if(!nominalValuesGroup_.isHidden())
        {
            iNominalValuesList_.setAttribute(getObject());
            iNominalValuesList_.setTransaction(t);
            nominalValuesList_.saveData();
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
        
        try
        {
        	t.delete(getObject());
        	
        	t.commit();
        	
        	return null;
        }
        catch(Exception e)
        {
        	t.clear();
        	t.rollback();
        	
        	return tr("form.delete.restriction");
        }
    }
}
