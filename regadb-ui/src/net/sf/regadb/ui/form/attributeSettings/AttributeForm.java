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
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class AttributeForm extends FormWidget
{
    private Attribute attribute_;
    
    //general group
    private WGroupBox generalGroup_;
    private WTable generalGroupTable_;
    private Label nameL;
    private TextField nameTF;
    private Label valueTypeL;
    private ComboBox valueTypeCB;
    private Label groupL;
    private ComboBox groupCB;
    private Label usageL;
    private TextField usageTF;
    
    //nominal values group
    private WGroupBox nominalValuesGroup_;
    private EditableTable<AttributeNominalValue> nominalValuesList_;
    private IAttributeNominalValueDataList iNominalValuesList_;
    
    public AttributeForm(InteractionState interactionState, WMessage formName, Attribute attribute)
    {
        super(formName, interactionState);
        attribute_ = attribute;
        
        init();
        
        fillData();
    }
    
    private void init()
    {
        generalGroup_ = new WGroupBox(tr("form.attributeSettings.attribute.editView.general"), this);
        generalGroupTable_ = new WTable(generalGroup_);
        nameL = new Label(tr("form.attributeSettings.attribute.editView.name"));
        nameTF = new TextField(getInteractionState(), this);
        nameTF.setMandatory(true);
        addLineToTable(generalGroupTable_, nameL, nameTF);
        valueTypeL = new Label(tr("form.attributeSettings.attribute.editView.valueType"));
        valueTypeCB = new ComboBox(getInteractionState(), this);
        valueTypeCB.setMandatory(true);
        addLineToTable(generalGroupTable_, valueTypeL, valueTypeCB);
        groupL = new Label(tr("form.attributeSettings.attribute.editView.group"));
        groupCB = new ComboBox(getInteractionState(), this);
        groupCB.setMandatory(true);
        addLineToTable(generalGroupTable_, groupL, groupCB);
        if(getInteractionState()!=InteractionState.Adding)
        {
            usageL = new Label(tr("form.attributeSettings.attribute.editView.usage"));
            usageTF = new TextField(InteractionState.Viewing, null);
            addLineToTable(generalGroupTable_, usageL, usageTF);
        }
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<ValueType> valueTypes = t.getValueTypes();
        boolean first = true;
        WMessage msg;
        WMessage toSelect = null;
        for(ValueType vt : valueTypes)
        {
            msg = new DataComboMessage<ValueType>(vt, vt.getDescription());
            if(first)
            {
                toSelect = msg;
                first = false;
            }
            valueTypeCB.addItem(msg);
        }
        valueTypeCB.selectItem(toSelect);
        
        List<AttributeGroup> attributeGroups = t.getAttributeGroups();
        first = true;
        for(AttributeGroup ag : attributeGroups)
        {
            msg = new DataComboMessage<AttributeGroup>(ag, ag.getGroupName());
            if(first)
            {
                toSelect = msg;
                first = false;
            }
            groupCB.addItem(msg);
        }
        groupCB.selectItem(toSelect);

        t.commit();
        
        nominalValuesGroup_ = new WGroupBox(tr("form.attributeSettings.attribute.editView.nominalValues"), this);
                
        addControlButtons();
    }
    
    private void setNominalValuesGroup()
    {
        boolean visible = (ValueTypes.getValueType(((DataComboMessage<ValueType>)valueTypeCB.currentText()).getValue()) == ValueTypes.NOMINAL_VALUE);
        
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
                t.attach(attribute_);
                
                for(AttributeNominalValue anv : attribute_.getAttributeNominalValues())
                {
                    list.add(anv);
                }
                t.commit();
            }
            iNominalValuesList_ = new IAttributeNominalValueDataList(this);
            nominalValuesList_ = new EditableTable<AttributeNominalValue>(nominalValuesGroup_, iNominalValuesList_, list);
        }
    }
    
    private void fillData()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            attribute_ = new Attribute();
        }
        
        if(getInteractionState()!=InteractionState.Adding)
        {
            Transaction t = RegaDBMain.getApp().createTransaction();
            
            t.attach(attribute_);
            
            nameTF.setText(attribute_.getName());
            valueTypeCB.selectItem(new DataComboMessage<ValueType>(attribute_.getValueType(), attribute_.getValueType().getDescription()));
            groupCB.selectItem(new DataComboMessage<AttributeGroup>(attribute_.getAttributeGroup(), attribute_.getAttributeGroup().getGroupName()));
            
            usageTF.setText(t.getAttributeUsage(attribute_)+"");
            
            t.commit();
        }
        
        setNominalValuesGroup();
        
        valueTypeCB.addComboChangeListener(new SignalListener<WEmptyEvent>()
                {
                    public void notify(WEmptyEvent a)
                    {
                        setNominalValuesGroup();
                    }
                });
    }
    
    @Override
    public void saveData() 
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        if(!(getInteractionState()==InteractionState.Adding))
        {
            t.attach(attribute_);
        }
        AttributeGroup ag = ((DataComboMessage<AttributeGroup>)groupCB.currentText()).getValue();
        t.update(ag);
        ValueType vt = ((DataComboMessage<ValueType>)valueTypeCB.currentText()).getValue();
        t.attach(vt);
        attribute_.setName(nameTF.text());
        attribute_.setValueType(vt);
        attribute_.setAttributeGroup(ag);
        
        if(!nominalValuesGroup_.isHidden())
        {
            iNominalValuesList_.setAttribute(attribute_);
            iNominalValuesList_.setTransaction(t);
            nominalValuesList_.saveData();
        }
        
        t.save(attribute_);
        t.commit();
        
        RegaDBMain.getApp().getTree().getTreeContent().attributesSelected.setSelectedAttribute(attribute_);
        RegaDBMain.getApp().getTree().getTreeContent().attributesSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().attributesSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().attributesView.selectNode();
    }
}
