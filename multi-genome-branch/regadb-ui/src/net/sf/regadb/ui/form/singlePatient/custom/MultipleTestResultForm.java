package net.sf.regadb.ui.form.singlePatient.custom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TestComboBox;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.framework.widgets.messagebox.ConfirmMessageBox;
import net.sf.regadb.ui.tree.items.custom.ContactItem.TestItem;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;
import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.i8n.WMessage;

public class MultipleTestResultForm extends FormWidget {
    
    private WGroupBox generalGroupBox_;
    private FormTable generalGroupTable_;
    private Label dateL_;
    private DateField dateTF_;
    private Label sampleIdL_;
    private TextField sampleIdTF_;
    private List<FormField> formFields_ = new ArrayList<FormField>();
    
    private List<Test> tests_;
    private ActionItem lastItem_;

    public MultipleTestResultForm(WMessage name, InteractionState state, List<TestItem> tests, ActionItem lastItem) {
        super(name, state);
        
        tests_ = new ArrayList<Test>();
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        for(TestItem ti : tests){
        	Test test = t.getTestByGenome(ti.description, ti.organism);
        	if(test != null)
        		tests_.add(test);
        }
        t.commit();
        
        lastItem_ = lastItem;
        
        init();
    }
    
    private void init() {
        generalGroupBox_ = new WGroupBox(tr("form.multipleTestResults.general"), this);
        generalGroupTable_ = new FormTable(generalGroupBox_);
        dateL_ = new Label(tr("form.multipleTestResults.date"));
        dateTF_ = new DateField(getInteractionState(), this);
        dateTF_.setMandatory(true);
        generalGroupTable_.addLineToTable(dateL_, dateTF_);
        sampleIdL_ = new Label(tr("form.multipleTestResults.sampleId"));
        sampleIdTF_ = new TextField(getInteractionState(), this, FieldType.ALFANUMERIC);
        generalGroupTable_.addLineToTable(sampleIdL_, sampleIdTF_);
        
        for(Test t : tests_) {
            Label l = new Label(lt(TestComboBox.getLabel(t)));
            FormField testResultField;
            if(ValueTypes.getValueType(t.getTestType().getValueType()) == ValueTypes.NOMINAL_VALUE) {
                testResultField = new ComboBox(getInteractionState(), this);
                for(TestNominalValue tnv : t.getTestType().getTestNominalValues()) {
                    ((ComboBox)testResultField).addItem(new DataComboMessage<TestNominalValue>(tnv, tnv.getValue()));
                }
                ((ComboBox)testResultField).sort();
            } else {
                testResultField = getTextField(ValueTypes.getValueType(t.getTestType().getValueType()));
            }

            generalGroupTable_.addLineToTable(l, testResultField);
            formFields_.add(testResultField);
        }
        
        fillData();
        addControlButtons();
    }
    
    private void fillData() {
        if(!(getInteractionState()==InteractionState.Adding)) {
            Transaction t = RegaDBMain.getApp().createTransaction();
            
            Date newestDate = null;
            for(Test test : tests_) {
                TestResult tr = t.getNewestTestResult(test, RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem());
                if(tr!=null) {
                    if(newestDate==null) {
                        newestDate = tr.getTestDate();
                    } else if(newestDate.before(tr.getTestDate())){
                        newestDate = tr.getTestDate();
                    }
                }
            }
            
            if(newestDate!=null) {
	            dateTF_.setDate(newestDate);
	            
	            for(int i=0; i<tests_.size(); i++) {
	                FormField f = formFields_.get(i);
	                TestResult tr = t.getNewestTestResult(tests_.get(i), RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem());
	                
	                if(tr!=null && DateUtils.compareDates(tr.getTestDate(), (newestDate))==0) {
	                	sampleIdTF_.setText(tr.getSampleId());
	                    if(f instanceof ComboBox) {
	                        ((ComboBox)f).selectItem(tr.getTestNominalValue().getValue());
	                    } else {
	                        f.setText(tr.getValue());
	                    }
	                }
	            }
            }
        }
    }

    @Override
    public void cancel() {
        lastItem_.prograSelectNode();
    }

    @Override
    public WMessage deleteObject() {
        return null;
    }

    @Override
    public void redirectAfterDelete() {
        
    }

    @Override
    public void saveData() {
        Transaction t = RegaDBMain.getApp().createTransaction();
        boolean duplicateSampleId = false;
        if(!sampleIdTF_.getFormText().equals("")) {
        	for(TestResult tr : RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem().getTestResults()) {
        		if(sampleIdTF_.getFormText().equals(tr.getSampleId())) {
        			for(Test test : tests_) {
        				if(tr.getTest().getDescription().equals(test.getDescription())) {
        					duplicateSampleId = true;
        					break;
        				}
        			}
        		}
        		if(duplicateSampleId)
        			break;
        	}
        }
        
        if(duplicateSampleId) {
	        final ConfirmMessageBox cmb = new ConfirmMessageBox(tr("form.multipleTestResults.duplicateSampleIdWarning"));
	        cmb.yes.clicked.addListener(new SignalListener<WMouseEvent>()
	                {
	            public void notify(WMouseEvent a) 
	            {
	                cmb.hide();
	            }
	        });
	        cmb.no.clicked.addListener(new SignalListener<WMouseEvent>()
	                {
	            public void notify(WMouseEvent a) 
	            {
	                cmb.hide();
	                return;
	            }
	        });
        }
        
        for(int i = 0; i<tests_.size(); i++) {
            TestResult tr = null;
            FormField f = formFields_.get(i);
            if(f instanceof ComboBox) {
                if(((DataComboMessage<TestNominalValue>)((ComboBox)f).currentItem()).getValue()!=null) {
                    tr = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem().createTestResult(tests_.get(i));
                    tr.setTestNominalValue(((DataComboMessage<TestNominalValue>)((ComboBox)f).currentItem()).getValue());
                }
            } else {
                if(f.text()!=null && !f.text().trim().equals("")) {
                    tr = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem().createTestResult(tests_.get(i));
                    tr.setValue(f.text());
                }
            }
            if(tr!=null) {
                tr.setTestDate(dateTF_.getDate());
                if(!sampleIdTF_.getFormText().equals("")) {
                	tr.setSampleId(sampleIdTF_.getFormText());
                }
                t.save(tr);
            }
        }
        t.commit();
        lastItem_.prograSelectNode();
    }
}
