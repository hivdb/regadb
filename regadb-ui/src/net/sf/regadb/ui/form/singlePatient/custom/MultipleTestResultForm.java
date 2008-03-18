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
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class MultipleTestResultForm extends FormWidget {
    
    private WGroupBox generalGroupBox_;
    private WTable generalGroupTable_;
    private Label dateL_;
    private DateField dateTF_;
    private List<FormField> formFields_ = new ArrayList<FormField>();
    
    private List<Test> tests_;
    private ActionItem lastItem_;

    public MultipleTestResultForm(WMessage name, InteractionState state, List<Test> tests, ActionItem lastItem) {
        super(name, state);
        
        tests_ = tests;
        lastItem_ = lastItem;
        
        init();
    }
    
    private void init() {
        generalGroupBox_ = new WGroupBox(tr("form.multipleTestResults.general"), this);
        generalGroupTable_ = new WTable(generalGroupBox_);
        dateL_ = new Label(tr("form.multipleTestResults.date"));
        dateTF_ = new DateField(getInteractionState(), this);
        dateTF_.setMandatory(true);
        addLineToTable(generalGroupTable_, dateL_, dateTF_);
        for(Test t : tests_) {
            Label l = new Label(lt(t.getDescription()));
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

            addLineToTable(generalGroupTable_, l, testResultField);
            formFields_.add(testResultField);
        }
        
        fillData();
        addControlButtons();
    }
    
    private void fillData() {
        if(!(getInteractionState()==InteractionState.Adding)) {
            Transaction t = RegaDBMain.getApp().createTransaction();
            
            List<TestResult> newestTestResults = new ArrayList<TestResult>();
            Date newestDate = null;
            for(Test test : tests_) {
                TestResult tr = t.getNewestTestResult(test, RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem());
                newestTestResults.add(tr);
                if(tr!=null) {
                    if(newestDate==null) {
                        newestDate = tr.getTestDate();
                    } else if(newestDate.before(tr.getTestDate())){
                        newestDate = tr.getTestDate();
                    }
                }
            }
            
            dateTF_.setDate(newestDate);
            
            for(int i = 0; i<tests_.size(); i++) {
                FormField f = formFields_.get(i);
                TestResult tr = newestTestResults.get(i);
                if(tr!=null && tr.getTestDate().equals(newestDate)) {
                    if(f instanceof ComboBox) {
                        ((ComboBox)f).selectItem(tr.getTestNominalValue().getValue());
                    } else {
                        f.setText(tr.getValue());
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
        for(int i = 0; i<tests_.size(); i++) {
            TestResult tr = null;
            FormField f = formFields_.get(i);
            if(f instanceof ComboBox) {
                if(((DataComboMessage<TestNominalValue>)((ComboBox)f).currentItem()).getValue()!=null) {
                    tr = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem().createTestResult(tests_.get(i));
                    tr.setTestNominalValue(((DataComboMessage<TestNominalValue>)((ComboBox)f).currentItem()).getValue());
                }
            } else {
                if(f.text()!=null) {
                    tr = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem().createTestResult(tests_.get(i));
                    tr.setValue(f.text());
                }
            }
            if(tr!=null) {
                tr.setTestDate(dateTF_.getDate());
                t.save(tr);
            }
        }
        t.commit();
        lastItem_.prograSelectNode();
    }
}
