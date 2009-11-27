package net.sf.regadb.ui.form.singlePatient;

import java.util.List;

import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;

public class PatientEventForm extends FormWidget
{
	private PatientEventValue patientEvent_;
	
	// FORM COMPONENTS \\
	private WGroupBox mainFrameGroup_;
	private FormTable mainFrameTable_;
	private Label lblEvent, lblValue, lblStartDate, lblEndDate;
	private WContainerWidget valueContainer;
	private FormField ffValue;
	private ComboBox<Event> cmbEvents;
	private DateField startDate, endDate;
	
	public PatientEventForm(InteractionState state, WString formName, PatientEventValue patientEvent) {
		super(formName, state);
		
		patientEvent_ = patientEvent;
		
		init();
		fillData();
	}
	
	private void init()
	{
		mainFrameGroup_= new WGroupBox(tr("event.form.frame.general"), this);
		mainFrameTable_ = new FormTable(mainFrameGroup_);
		
		lblStartDate = new Label(tr("form.singlePatient.patientEvent.label.startDate"));
		startDate = new DateField(getInteractionState(), this, RegaDBSettings.getInstance().getDateFormat());
		startDate.setMandatory(true);
		mainFrameTable_.addLineToTable(lblStartDate, startDate);
		
		lblEndDate = new Label(tr("form.singlePatient.patientEvent.label.endDate"));
		endDate = new DateField(getInteractionState(), this, RegaDBSettings.getInstance().getDateFormat());
		mainFrameTable_.addLineToTable(lblEndDate, endDate);
		
		lblEvent = new Label(tr("form.singlePatient.patientEvent.label.event"));
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
		lblValue = new Label(tr("form.singlePatient.patientEvent.label.value"));
		lblValue.setLabelUIMandatory(this);
		valueContainer = new WContainerWidget();
		
        mainFrameTable_.putElementAt(row, 0, lblValue);
        mainFrameTable_.putElementAt(row, 1, valueContainer);
        
		addControlButtons();
	}
	
	private void fillData() {
		if( getInteractionState() == InteractionState.Adding )
		{
			patientEvent_ = new PatientEventValue();
		}
		else
		{
			cmbEvents.selectItem(patientEvent_.getEvent().getName());
		}
		
		updateValue();
		
		if( getInteractionState() != InteractionState.Adding )
        {
            if( isNominalValue() )
            {
                ((ComboBox<PatientEventValue>)ffValue).selectItem(patientEvent_.getEventNominalValue().getValue());
            }
            else
            {
            	ffValue.setText(patientEvent_.getValue());
            }
        }
		
		startDate.setDate(patientEvent_.getStartDate());
		endDate.setDate(patientEvent_.getEndDate());
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
		if( getInteractionState() == InteractionState.Adding )
        {
            redirectToSelect(
            		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getEventTreeNode(),
            		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getEventTreeNode().getSelectActionItem());
        }
        else
        {
            redirectToView(
            		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getEventTreeNode().getSelectedActionItem(),
            		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getEventTreeNode().getViewActionItem());
        }
	}
	
	@Override
	public WString deleteObject()
	{
		Transaction t = RegaDBMain.getApp().createTransaction();
        
		RegaDBMain.getApp().getSelectedPatient().getPatientEventValues().remove(patientEvent_);
        t.delete(patientEvent_);
        
        t.commit();
        
        return null;
	}
	
	@Override
	public void redirectAfterDelete() {
		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getEventTreeNode()
			.getSelectActionItem().selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getEventTreeNode()
        	.setSelectedItem(null);
	}
	
	@Override
	public void saveData() {
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		Patient p = RegaDBMain.getApp().getSelectedPatient();
		t.attach(p);
		
		if( endDate.getDate() != null && DateUtils.compareDates(startDate.getDate(), endDate.getDate()) > 0)
        {
			UIUtils.showWarningMessageBox(this, tr("form.therapy.date.warning"));
        }
        else
        {
        	if( getInteractionState() == InteractionState.Adding )
			{
				patientEvent_ = p.addPatientEvent(cmbEvents.currentValue());
			}
			
			patientEvent_.setStartDate(startDate.getDate());
			patientEvent_.setEndDate(endDate.getDate());
			
			patientEvent_.setEvent(cmbEvents.currentValue());
			
			if ( isNominalValue() )
			{
				patientEvent_.setEventNominalValue( (EventNominalValue)((ComboBox)ffValue).currentValue() );
			}
			else
			{
				patientEvent_.setValue( ((TextField)ffValue).getFormText() );
			}
			
			update(patientEvent_, t);
			t.commit();
			
	        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getEventTreeNode().setSelectedItem(patientEvent_);
	        redirectToView(
	        		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getEventTreeNode().getSelectedActionItem(),
	        		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getEventTreeNode().getViewActionItem());
		}
	}
	
	public boolean isNominalValue()
	{
		return (ValueTypes.getValueType(cmbEvents.currentValue().getValueType()) == ValueTypes.NOMINAL_VALUE);
	}
}
