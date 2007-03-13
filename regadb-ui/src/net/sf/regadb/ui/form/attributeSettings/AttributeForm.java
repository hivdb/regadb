package net.sf.regadb.ui.form.attributeSettings;

import java.util.List;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WTable;

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
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<ValueType> valueTypes = t.getValueTypes();
        for(ValueType vt : valueTypes)
        {
            valueTypeCB.addItem(new DataComboMessage<ValueType>(vt, vt.getDescription()));
        }
        
        List<AttributeGroup> attributeGroups = t.getAttributeGroups();
        for(AttributeGroup ag : attributeGroups)
        {
            groupCB.addItem(new DataComboMessage<AttributeGroup>(ag, ag.getGroupName()));
        }
        t.commit();
        
        addControlButtons();
    }
    
    private void fillData()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.attach(attribute_);
        
        nameTF.setText(attribute_.getName());
        valueTypeCB.selectItem(new DataComboMessage<ValueType>(attribute_.getValueType(), attribute_.getValueType().getDescription()));
        groupCB.selectItem(new DataComboMessage<AttributeGroup>(attribute_.getAttributeGroup(), attribute_.getAttributeGroup().getGroupName()));
        t.commit();
    }
    
    @Override
    public void saveData() 
    {
        
    }
}
