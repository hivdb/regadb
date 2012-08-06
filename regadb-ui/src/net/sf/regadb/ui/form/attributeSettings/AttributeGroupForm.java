package net.sf.regadb.ui.form.attributeSettings;

import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;

public class AttributeGroupForm extends ObjectForm<AttributeGroup>
{
    //general group
    private WGroupBox generalGroup_;
    private FormTable generalGroupTable_;
    private Label nameL;
    private TextField nameTF;
    
    public AttributeGroupForm(WString formName, InteractionState interactionState, ObjectTreeNode<AttributeGroup> node, AttributeGroup attributeGroup)
    {
        super(formName, interactionState, node, attributeGroup);
        init();
        fillData();
    }
    
    private void init() 
    {
        generalGroup_ = new WGroupBox(tr("form.attributeSettings.attributeGroup.editView.general"), this);
        generalGroupTable_= new FormTable(generalGroup_);
        nameL = new Label(tr("form.attributeSettings.attributeGroup.editView.groupName"));
        nameTF = new TextField(getInteractionState(), this);
        nameTF.setMandatory(true);
        generalGroupTable_.addLineToTable(nameL, nameTF);
        
        addControlButtons();
    }
    
    private void fillData() 
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            setObject(new AttributeGroup());
        }
        
        if(getInteractionState()!=InteractionState.Adding)
        {
            Transaction t = RegaDBMain.getApp().createTransaction();
            
            t.attach(getObject());
            
            nameTF.setText(getObject().getGroupName());
            
            t.commit();
        }
    }

    @Override
    public void saveData() 
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        if(getInteractionState()!=InteractionState.Adding)
        {
            t.attach(getObject());
        }
        
        getObject().setGroupName(nameTF.text());
        
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
