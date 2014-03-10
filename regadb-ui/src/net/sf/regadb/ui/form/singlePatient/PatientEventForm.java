package net.sf.regadb.ui.form.singlePatient;

import java.util.List;

import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;

public class PatientEventForm extends ObjectForm<PatientEventValue>
{
	// FORM COMPONENTS \\
	private WGroupBox mainFrameGroup_;
	private FormTable mainFrameTable_;
	private Label lblEvent, lblValue, lblStartDate, lblEndDate;
	private WContainerWidget valueContainer;
	private FormField ffValue;
	private ComboBox<Event> cmbEvents;
	private DateField startDate, endDate;
	
	public PatientEventForm(WString formName, InteractionState state, ObjectTreeNode<PatientEventValue> node, PatientEventValue patientEvent) {
		super(formName, state, node, patientEvent);
		
		if(RegaDBMain.getApp().isPatientInteractionAllowed(state)){
			init();
			fillData();
		}
	}
	
	private void init()
	{
		mainFrameGroup_= new WGroupBox(tr("form.patientEvent.general"), this);
		mainFrameTable_ = new FormTable(mainFrameGroup_);
		
		lblStartDate = new Label(tr("form.patientEvent.startDate"));
		startDate = new DateField(getInteractionState(), this, RegaDBSettings.getInstance().getDateFormat());
		startDate.setMandatory(true);
		mainFrameTable_.addLineToTable(lblStartDate, startDate);
		
		lblEndDate = new Label(tr("form.patientEvent.endDate"));
		endDate = new DateField(getInteractionState(), this, RegaDBSettings.getInstance().getDateFormat());
		mainFrameTable_.addLineToTable(lblEndDate, endDate);
		
		lblEvent = new Label(tr("form.patientEvent.event"));
		cmbEvents = new ComboBox<Event>(getInteractionState(), this);
        cmbEvents.setMandatory(true);
        mainFrameTable_.addLineToTable(lblEvent, cmbEvents);
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		
        List<Event> eventList = t.getEvents();
        
        for(Event e : eventList)
        {
        	cmbEvents.addItem(new DataComboMessage<Event>(e, e.getName()));
        }
        
        cmbEvents.sort();
        
        cmbEvents.addComboChangeListener(new Signal.Listener()
		{
			public void trigger()
			{
				updateValue();
			}
        });
        
        t.commit();
		
        int row = mainFrameTable_.getRowCount();
		lblValue = new Label(tr("form.patientEvent.value"));
		lblValue.setLabelUIMandatory(this);
		valueContainer = new WContainerWidget();
		
        mainFrameTable_.putElementAt(row, 0, lblValue);
        mainFrameTable_.putElementAt(row, 1, valueContainer);
        
		addControlButtons();
	}
	
	private void fillData() {
		if( getInteractionState() == InteractionState.Adding )
		{
			setObject(new PatientEventValue());
		}
		else
		{
			cmbEvents.selectItem(getObject().getEvent().getName());
		}
		
		updateValue();
		
		if( getInteractionState() != InteractionState.Adding )
        {
            if( isNominalValue() )
            {
                ((ComboBox<PatientEventValue>)ffValue).selectItem(getObject().getEventNominalValue().getValue());
            }
            else
            {
            	ffValue.setText(getObject().getValue());
            }
        }
		
		startDate.setDate(getObject().getStartDate());
		endDate.setDate(getObject().getEndDate());
	}
	
	private void updateValue()
	{
		removeFormField(ffValue);
        
		if ( isNominalValue() )
		{
			ffValue = new ComboBox<PatientEventValue>(getInteractionState(), this);
			
	        for(EventNominalValue env : cmbEvents.currentValue().getEventNominalValues())
	        {
	        	((ComboBox)ffValue).addItem(new DataComboMessage<EventNominalValue>(env, env.getValue()));
	        }
	        
	        ((ComboBox<PatientEventValue>)ffValue).sort();
		}
		else
		{
			ffValue = getTextField(ValueTypes.getValueType(cmbEvents.currentValue().getValueType()));
		}
		
        valueContainer.clear();
        valueContainer.addWidget(ffValue);
	}
	
	@Override
	public void cancel() {
	}
	
	@Override
	public WString deleteObject()
	{
		Transaction t = RegaDBMain.getApp().createTransaction();
        
		RegaDBMain.getApp().getSelectedPatient().getPatientEventValues().remove(getObject());
        t.delete(getObject());
        
        t.commit();
        
        return null;
	}
	
	@Override
	public void saveData() {
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		Patient p = RegaDBMain.getApp().getSelectedPatient();
		t.attach(p);
		
    	if( getInteractionState() == InteractionState.Adding )
		{
    		setObject(p.addPatientEvent(cmbEvents.currentValue()));
		}
		
    	getObject().setStartDate(startDate.getDate());
    	getObject().setEndDate(endDate.getDate());
		
    	getObject().setEvent(cmbEvents.currentValue());
		
		if ( isNominalValue() )
		{
			getObject().setEventNominalValue( (EventNominalValue)((ComboBox)ffValue).currentValue() );
		}
		else
		{
			getObject().setValue( ffValue.getFormText() );
		}
			
		update(getObject(), t);
		t.commit();
	}
	
	public boolean isNominalValue()
	{
		return (ValueTypes.getValueType(cmbEvents.currentValue().getValueType()) == ValueTypes.NOMINAL_VALUE);
	}
	
	@Override
	public boolean validateForm(){
		if(super.validateForm()){
			if(endDate.getDate() != null && DateUtils.compareDates(startDate.getDate(), endDate.getDate()) > 0)
				UIUtils.showWarningMessageBox(this, tr("form.therapy.date.warning"));
			else
				return true;
		}
		return false;
	}
}
