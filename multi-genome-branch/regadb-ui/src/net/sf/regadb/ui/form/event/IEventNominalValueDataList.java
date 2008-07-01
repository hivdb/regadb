package net.sf.regadb.ui.form.event;

import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.IEditableTable;
import net.sf.witty.wt.WWidget;

public class IEventNominalValueDataList implements IEditableTable <EventNominalValue>{
	private FormWidget form_;
    private static final String [] headers_ = {"editableTable.eventNominalValue.name"};
    
    private Event event_;
    private Transaction transaction_;
    
	public IEventNominalValueDataList(FormWidget form, Event event) {
		form_ = form;
		event_ = event;
	}
	
	public void addData(WWidget[] widgets) {
		EventNominalValue env = new EventNominalValue(event_, ((TextField)widgets[0]).text());
        event_.getEventNominalValues().add(env);
	}
	
	public WWidget[] addRow() {
		TextField tf = new TextField(form_.getInteractionState(), form_);
        
        WWidget[] widgets = new WWidget[1];
        widgets[0] = tf;
        
        return widgets;
	}
	
	public void changeData(EventNominalValue env, WWidget[] widgets) {
		for(EventNominalValue env_ : event_.getEventNominalValues())
        {
            if( env.getNominalValueIi().equals( env_.getNominalValueIi() ) )
            {
            	env_.setValue( ( (TextField)widgets[0] ).text() );
                break;
            }
        }
	}
	
	public void deleteData(EventNominalValue env) {
		event_.getEventNominalValues().remove(env); 
        transaction_.delete(env);
	}
	
	public WWidget[] fixAddRow(WWidget[] widgets) {
		TextField tf = new TextField(form_.getInteractionState(), form_);
        
        WWidget[] widgetsToReturn = new WWidget[1];
        widgetsToReturn[0] = tf;
        
        tf.setText(((TextField)widgets[0]).text());
        
        return widgetsToReturn;
	}
	
	public void flush() {
		transaction_.flush();
	}
	
	public InteractionState getInteractionState() {
		return form_.getInteractionState();
	}
	
	public Event getEvent() 
    {
        return event_;
    }
    
    public void setEvent(Event event) 
    {
        this.event_ = event;
    }
    
    public void setTransaction(Transaction transaction) 
    {
        this.transaction_ = transaction;
    }
    
	public String[] getTableHeaders() {
		return headers_;
	}
	
	public WWidget[] getWidgets(EventNominalValue env) {
		TextField tf = new TextField(form_.getInteractionState(), form_);
        
        WWidget[] widgets = new WWidget[1];
        widgets[0] = tf;
        
        tf.setText(env.getValue());
        
        return widgets;
	}
}