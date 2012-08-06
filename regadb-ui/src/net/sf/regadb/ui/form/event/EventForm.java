package net.sf.regadb.ui.form.event;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
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
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;

public class EventForm extends ObjectForm<Event> {
	// Frame
	private WGroupBox mainFrameGroup_;
	private FormTable mainFrameTable_;
	private Label lblName, lblType;
	private TextField txtName;
	private ComboBox<ValueType> cmbValueType;
	
	// Nominal Values
	private WGroupBox nominalValuesGroup_;
	private EditableTable<EventNominalValue> nominalValuesList_;
	private IEventNominalValueDataList iNominalValuesList_;
	
	public EventForm(WString formName, InteractionState interactionState, ObjectTreeNode<Event> node, Event event) {
		super(formName, interactionState, node, event);
		init();
		fillData();
	}
	
	private void init() {
		mainFrameGroup_= new WGroupBox(tr("form.event.general"), this);
		mainFrameTable_ = new FormTable(mainFrameGroup_);
		
		lblName = new Label(tr("form.event.name"));
        txtName = new TextField(getInteractionState(), this);
        txtName.setMandatory(true);
        mainFrameTable_.addLineToTable(lblName, txtName);
		
        lblType = new Label(tr("form.event.type"));
        cmbValueType = new ComboBox<ValueType>(getInteractionState(), this);
        
		Transaction t = RegaDBMain.getApp().createTransaction();
		
        List<ValueType> valueTypeList = t.getValueTypes();
        
        for(ValueType vt : valueTypeList)
        {
            cmbValueType.addItem(new DataComboMessage<ValueType>(vt, vt.getDescription()));
        }
        
        cmbValueType.sort();
        
        t.commit();
        
        mainFrameTable_.addLineToTable(lblType, cmbValueType);
        
        nominalValuesGroup_= new WGroupBox(tr("form.event.values"), this);
		
		addControlButtons();
	}
	
	private void fillData() {
		if(getInteractionState() == InteractionState.Adding){
			setObject(new Event());
		}
		
		txtName.setText(getObject().getName());
		
		if(getObject().getValueType() != null)
        {
            cmbValueType.selectItem(getObject().getValueType().getDescription());
        }
		
		setNominalValuesGroup();
		cmbValueType.addComboChangeListener(new Signal.Listener()
				{
					public void trigger()
					{
						setNominalValuesGroup();
					}
                });
	}
	
	private void setNominalValuesGroup() {
		boolean visible = (ValueTypes.getValueType(cmbValueType.currentValue()) == ValueTypes.NOMINAL_VALUE);
        
		nominalValuesGroup_.setHidden(!visible);
		
        if(visible)
        {
        	if(nominalValuesList_!= null)
            {
        		nominalValuesGroup_.removeWidget(nominalValuesList_);
            }
        	
            ArrayList<EventNominalValue> list = new ArrayList<EventNominalValue>();
            
            if(getInteractionState()!=InteractionState.Adding)
            {
                Transaction t = RegaDBMain.getApp().createTransaction();
                t.attach(getObject());
                
                for(EventNominalValue anv : getObject().getEventNominalValues())
                {
                    list.add(anv);
                }
                t.commit();
            }
            
            iNominalValuesList_ = new IEventNominalValueDataList(this, getObject());
            nominalValuesList_ = new EditableTable<EventNominalValue>(nominalValuesGroup_, iNominalValuesList_, list){
            	public boolean canRemove(EventNominalValue toRemove){
            		Transaction t = RegaDBMain.getApp().createTransaction();
            		boolean isUsed = t.isUsed(toRemove);
            		t.commit();
            		return !isUsed;
            	}
            };
        }
	}
	
	@Override
	public void cancel() {
	}
	
	@Override
	public WString deleteObject() {
		Transaction t = RegaDBMain.getApp().createTransaction();
    	
    	try {
    		t.delete(getObject());
	        t.commit();
	        return null;
    	} catch(Exception e) {
    		t.clear();
    		t.rollback();
    		return tr("form.delete.restriction");
    	}
	}
	
	@Override
	public void saveData() {
		Transaction t = RegaDBMain.getApp().createTransaction();
		
        if(getInteractionState() != InteractionState.Adding) {
            t.attach(getObject());
        }
        
        ValueType vt = cmbValueType.currentValue();
        
        getObject().setName(txtName.text());
        getObject().setValueType(vt);
        
        if(!nominalValuesGroup_.isHidden())
        {
            iNominalValuesList_.setEvent(getObject());
            iNominalValuesList_.setTransaction(t);
            nominalValuesList_.saveData();
        }
        
        update(getObject(), t);
                
		t.commit();
	}
}
