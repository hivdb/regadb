package net.sf.regadb.ui.form.singlePatient.custom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.util.settings.EventItem;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.settings.TestItem;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WTableCell;
import eu.webtoolkit.jwt.WWidget;

public abstract class GridForm extends FormWidget{
	private TreeMenuNode lastItem;
	
	private List<Test> tests = new ArrayList<Test>();
	private List<Event> events = new ArrayList<Event>();
	
	private WTable table;
	
	private static class RemoveButton extends WPushButton{
		private WTable table;
		
		public RemoveButton(WTable table){
			super(tr("form.custom.grid.removeRow"));
			this.table = table;
			
			clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
                public void trigger(WMouseEvent a) {
                	removeRow();
                }
			});
                
		}
		
		private void removeRow(){
			int row = ((WTableCell)getParent()).getRow();
        	for(int i=0; i<table.getColumnCount(); ++i){
        		for(WWidget ww : table.getElementAt(row, i).getChildren()){
        			if(ww instanceof IFormField) {
	    				IForm form = ((IFormField)ww).getForm();
	    				if(form!=null)
	    					form.removeFormField((IFormField)ww);
	    			}
        		}
        	}
        	table.deleteRow(row);
		}
	}

	public GridForm(InteractionState interactionState, TreeMenuNode lastItem) {
		super(tr("form.custom.grid"), interactionState);
		
		if(RegaDBMain.getApp().isPatientInteractionAllowed(interactionState)){
			this.lastItem = lastItem;
			init();
		}
	}
	
	protected void init(){
        Transaction t = RegaDBMain.getApp().createTransaction();

        tests = new ArrayList<Test>();
        for(TestItem ti : RegaDBSettings.getInstance().getInstituteConfig().getContactFormConfig().getTests()){
        	Test test = t.getTestByGenome(ti.description, ti.organism);
        	if(test != null)
        		tests.add(test);
        }
        
        events = new ArrayList<Event>();
        for(EventItem ei : RegaDBSettings.getInstance().getInstituteConfig().getContactFormConfig().getEvents()){
        	Event event = t.getEvent(ei.name);
        	if(event != null)
        		events.add(event);
        }

        t.commit();
        
        table = new WTable(this);
        table.setStyleClass("datatable form-custom-grid");
        
        int i = 0;
        table.getElementAt(0, i++).addWidget(new WLabel(tr("form.custom.grid.date")));
        table.getElementAt(0, i++).addWidget(new WLabel(tr("form.custom.grid.sampleId")));
        
        for(Test test : tests)
        	table.getElementAt(0, i++).addWidget(new WLabel(test.getDescription()));
        for(Event event : events)
        	table.getElementAt(0, i++).addWidget(new WLabel(event.getName()));
        
        table.getElementAt(0, i).addWidget(new WLabel(""));
        
        for(int j=0; j<table.getColumnCount(); ++j)
        	table.getElementAt(0, j).setStyleClass("column-title");
        
        for(int j=0; j<RegaDBSettings.getInstance().getInstituteConfig().getContactFormConfig().getGridRowCount(); ++j){
        	addRow();
        }
        
        WPushButton add = new WPushButton(tr("form.custom.grid.addRow"));
        add.clicked().addListener(this,  new Signal1.Listener<WMouseEvent>() {
                public void trigger(WMouseEvent a) {
                	addRow();
                }
			});
        addWidget(add);
        
        addControlButtons();
	}
	
	@SuppressWarnings("unchecked")
	protected void addRow(){
		int i = table.getRowCount();
		int j = 0;
		
		table.getElementAt(i, j++).addWidget(new DateField(getInteractionState(), this));
		table.getElementAt(i, j++).addWidget(new TextField(getInteractionState(), this));
		
		for(Test test : tests){
			ValueTypes vt = ValueTypes.getValueType(test.getTestType().getValueType());
			FormField f;
			if(vt == ValueTypes.NOMINAL_VALUE){
				f = new ComboBox(getInteractionState(), this);
                for(TestNominalValue tnv : test.getTestType().getTestNominalValues()) {
                    ((ComboBox)f).addItem(new DataComboMessage<TestNominalValue>(tnv, tnv.getValue()));
                }
                ((ComboBox)f).sort();
                ((ComboBox)f).addNoSelectionItem();
			}
			else{
				f = getTextField(vt);
			}
			table.getElementAt(i, j++).addWidget(f);
		}
		
		for(Event event : events){
			ValueTypes vt = ValueTypes.getValueType(event.getValueType());
			FormField f;
			if(ValueTypes.getValueType(event.getValueType()) == ValueTypes.NOMINAL_VALUE) {
	            f = new ComboBox(getInteractionState(), this);
	            for(EventNominalValue env : event.getEventNominalValues()) {
	                ((ComboBox)f).addItem(new DataComboMessage<EventNominalValue>(env, env.getValue()));
	            }
	            ((ComboBox)f).sort();
	            ((ComboBox)f).addNoSelectionItem();
			}
			else{
				f = getTextField(vt);
			}
			table.getElementAt(i,j).addWidget(new DateField(getInteractionState(),this));
			table.getElementAt(i,j++).addWidget(f);
		}
		
		table.getElementAt(i,j).addWidget(new RemoveButton(table));
	}

	@Override
	public void cancel() {
		lastItem.prograSelectNode();
	}

	@Override
	public WString deleteObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void redirectAfterDelete() {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveData() {
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
		
		for(int i=1; i<table.getRowCount(); ++i){
			int j = 0;
			
			Date date = ((DateField)table.getElementAt(i,j++).getChildren().get(0)).getDate();
			if(date == null)
				continue;
			
			String sampleId = ((TextField)table.getElementAt(i,j++).getChildren().get(0)).getFormText().trim();
			if(sampleId.length() == 0)
				sampleId = null;
			
			for(Test test : tests){
				FormField f = ((FormField)table.getElementAt(i,j++).getChildren().get(0));
				ValueTypes vt = ValueTypes.getValueType(test.getTestType().getValueType());
				TestResult tr = null;
				
				if(vt == ValueTypes.NOMINAL_VALUE){
					DataComboMessage<TestNominalValue> currentItem = (DataComboMessage<TestNominalValue>)((ComboBox)f).currentItem();
					if(currentItem != null && currentItem.getValue()!=null) {
	                	tr = p.createTestResult(test);
	                	tr.setTestNominalValue(((DataComboMessage<TestNominalValue>)((ComboBox)f).currentItem()).getDataValue());
	                }
				}else{
					if(f.text()!=null && !f.text().trim().equals("")) {
	                	tr = p.createTestResult(test);
	                	if(f instanceof DateField)
	                		tr.setValue(((DateField)f).getDate().getTime() +"");
	                	else
	                		tr.setValue(f.text());
	                }
				}
				if(tr!=null) {
	            	tr.setTestDate(date);
	                tr.setSampleId(sampleId);
	                t.save(tr);
	            }
			}
			
			for(Event event : events){
				Date stopDate = ((DateField)table.getElementAt(i,j).getChildren().get(0)).getDate();
				FormField f = ((FormField)table.getElementAt(i,j++).getChildren().get(1));
				ValueTypes vt = ValueTypes.getValueType(event.getValueType());
				PatientEventValue pev = null;
				
				if(vt == ValueTypes.NOMINAL_VALUE){
					DataComboMessage<EventNominalValue> currentItem = ((DataComboMessage<EventNominalValue>)((ComboBox)f).currentItem());
					if(currentItem != null && currentItem.getValue()!=null) {
	                	pev = p.createPatientEventValue(event);
	                    pev.setEventNominalValue(((DataComboMessage<EventNominalValue>)((ComboBox)f).currentItem()).getDataValue());
	                }
				}
				else{
					if(f.text()!=null && !f.text().trim().equals("")) {
	                	pev = p.createPatientEventValue(event);
	                	
	                	if(f instanceof DateField)
	                		pev.setValue(((DateField)f).getDate().getTime() +"");
	                	else
	                		pev.setValue(f.text());
	                }
				}
				
				if(pev!=null) {
	                pev.setStartDate(date);
	                pev.setEndDate(stopDate);
	                t.save(pev);
	            }
			}
		}
		
		t.commit();
	}
}
