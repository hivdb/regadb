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
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.i8n.WMessage;

public class AttributeForm extends FormWidget
{
    private Attribute attribute_;
    
    //general group
    private WGroupBox generalGroup_;
    private FormTable generalGroupTable_;
    private Label nameL;
    private TextField nameTF;
    private Label valueTypeL;
    private ComboBox<ValueType> valueTypeCB;
    private Label groupL;
    private ComboBox<AttributeGroup> groupCB;
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
        generalGroupTable_ = new FormTable(generalGroup_);
        nameL = new Label(tr("form.attributeSettings.attribute.editView.name"));
        nameTF = new TextField(getInteractionState(), this);
        nameTF.setMandatory(true);
        generalGroupTable_.addLineToTable(nameL, nameTF);
        valueTypeL = new Label(tr("form.attributeSettings.attribute.editView.valueType"));
        valueTypeCB = new ComboBox<ValueType>(getInteractionState(), this);
        valueTypeCB.setMandatory(true);
        generalGroupTable_.addLineToTable(valueTypeL, valueTypeCB);
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
            valueTypeCB.selectItem(attribute_.getValueType().getDescription());
            groupCB.selectItem(attribute_.getAttributeGroup().getGroupName());
            
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
        WMessage duplicates = null;
        if(nominalValuesList_!=null) {
        duplicates = nominalValuesList_.removeDuplicates(0);
        }
        if(duplicates!=null)
        {
            MessageBox.showWarningMessage(duplicates);
        }
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        if(!(getInteractionState()==InteractionState.Adding))
        {
            t.attach(attribute_);
        }
        AttributeGroup ag = groupCB.currentValue();
        t.attach(ag);
        ValueType vt = valueTypeCB.currentValue();
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
        
        update(attribute_, t);
        t.commit();
        
        RegaDBMain.getApp().getTree().getTreeContent().attributesSelected.setSelectedItem(attribute_);
        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().attributesSelected, RegaDBMain.getApp().getTree().getTreeContent().attributesView);
    }
    
    @Override
    public void cancel()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().attributes, RegaDBMain.getApp().getTree().getTreeContent().attributesSelect);
        }
        else
        {
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().attributesSelected, RegaDBMain.getApp().getTree().getTreeContent().attributesView);
        } 
    }
    
    @Override
    public WMessage deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        try
        {
        	t.delete(attribute_);
        	
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

    @Override
    public void redirectAfterDelete() 
    {
        RegaDBMain.getApp().getTree().getTreeContent().attributesSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().attributesSelected.setSelectedItem(null);
    }
}
