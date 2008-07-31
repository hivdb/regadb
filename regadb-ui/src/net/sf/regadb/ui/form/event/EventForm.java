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
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.i8n.WMessage;

public class EventForm extends FormWidget {
	private Event event_;
	
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
	
	public EventForm(InteractionState interactionState, WMessage formName, boolean literal, Event event) {
		super(formName, interactionState, literal);
		
		event_ = event;
		
		init();
		fillData();
	}
	
	private void init() {
		mainFrameGroup_= new WGroupBox(tr("general.group.general"), this);
		mainFrameTable_ = new FormTable(mainFrameGroup_);
		
		lblName = new Label(tr("general.name"));
        txtName = new TextField(getInteractionState(), this);
        txtName.setMandatory(true);
        mainFrameTable_.addLineToTable(lblName, txtName);
		
        lblType = new Label(tr("general.type"));
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
        
        nominalValuesGroup_= new WGroupBox(tr("general.nominalValues"), this);
		
		addControlButtons();
	}
	
	private void fillData() {
		if(getInteractionState() == InteractionState.Adding){
			event_ = new Event();
		}
		
		txtName.setText(event_.getName());
		
		if(event_.getValueType() != null)
        {
            cmbValueType.selectItem(event_.getValueType().getDescription());
        }
		
		setNominalValuesGroup();
		cmbValueType.addComboChangeListener(new SignalListener<WEmptyEvent>()
				{
					public void notify(WEmptyEvent a)
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
                t.attach(event_);
                
                for(EventNominalValue anv : event_.getEventNominalValues())
                {
                    list.add(anv);
                }
                t.commit();
            }
            
            iNominalValuesList_ = new IEventNominalValueDataList(this, event_);
            nominalValuesList_ = new EditableTable<EventNominalValue>(nominalValuesGroup_, iNominalValuesList_, list);
        }
	}
	
	@Override
	public void cancel() {
		if(getInteractionState()==InteractionState.Adding) {
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().event, RegaDBMain.getApp().getTree().getTreeContent().eventSelect);
        }
        else {
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().eventSelected, RegaDBMain.getApp().getTree().getTreeContent().eventSelectedView);
        }
	}
	
	@Override
	public WMessage deleteObject() {
		Transaction t = RegaDBMain.getApp().createTransaction();
    	
    	try {
    		t.delete(event_);
	        t.commit();
	        return null;
    	} catch(Exception e) {
    		t.clear();
    		t.rollback();
    		return tr("message.general.inuse");
    	}
	}
	
	@Override
	public void redirectAfterDelete() {
		RegaDBMain.getApp().getTree().getTreeContent().eventSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().eventSelected.setSelectedItem(null);
	}
	
	@Override
	public void saveData() {
		Transaction t = RegaDBMain.getApp().createTransaction();
		
        if(getInteractionState() != InteractionState.Adding) {
            t.attach(event_);
        }
        
        ValueType vt = cmbValueType.currentValue();
        
        event_.setName(txtName.text());
        event_.setValueType(vt);
        
        if(!nominalValuesGroup_.isHidden())
        {
            iNominalValuesList_.setEvent(event_);
            iNominalValuesList_.setTransaction(t);
            nominalValuesList_.saveData();
        }
        
        update(event_, t);
                
		t.commit();
        
        RegaDBMain.getApp().getTree().getTreeContent().eventSelected.setSelectedItem(event_);
        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().eventSelected, RegaDBMain.getApp().getTree().getTreeContent().eventSelectedView);
	}
}
