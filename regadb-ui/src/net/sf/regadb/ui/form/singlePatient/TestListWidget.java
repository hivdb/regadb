package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TestComboBox;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.settings.ViralIsolateFormConfig.TestItem;

public abstract class TestListWidget {
	private List<TestItem> testItems;
	
	private List<FormField> testFormFields = new ArrayList<FormField>();
	
	public TestListWidget(InteractionState is, List<TestItem> testItems, Set<TestResult> results) {
		this.testItems = new ArrayList<TestItem>(testItems);
		
		Transaction tr = RegaDBMain.getApp().createTransaction();
		Iterator<TestItem> i = this.testItems.iterator();
		while (i.hasNext()) {
			TestItem ti = i.next();
			Test t = tr.getTest(ti.description);
			if (!showTest(is, t, results))
				i.remove();
		}
	}
	
	public void init(InteractionState interactionState, IForm form, FormTable table) {
		Transaction tr = RegaDBMain.getApp().createTransaction();
		
		for(TestItem ti : testItems) {
        	Test t = tr.getTest(ti.description);
        	if(t != null){
	            Label l = new Label(TestComboBox.getLabel(t));
	            FormField testResultField;
	            if(ValueTypes.getValueType(t.getTestType().getValueType()) == ValueTypes.NOMINAL_VALUE) {
	                testResultField = new ComboBox(interactionState, form);
	                for(TestNominalValue tnv : t.getTestType().getTestNominalValues()) {
	                    ((ComboBox)testResultField).addItem(new DataComboMessage<TestNominalValue>(tnv, tnv.getValue()));
	                }
	                ((ComboBox)testResultField).sort();

	                if (ti.noValueSelected)
	                	((ComboBox)testResultField).addNoSelectionItem();
	                
	                ((ComboBox)testResultField).selectIndex(0);
	                
	                if (ti.defaultValue != null && interactionState.isEditable())
	                	((ComboBox)testResultField).selectItem(ti.defaultValue);
	            } else {
	                testResultField = FormField.getTextField(ValueTypes.getValueType(t.getTestType().getValueType()), interactionState, form);
	            }
	
	            table.addLineToTable(l, testResultField);
	            testFormFields.add(testResultField);
        	}
        	else{
        		System.err.println("ViralIsolateForm: test does not exist: '"+ ti.description +'\'');
        	}
        }
	}
	
	private boolean showTest(InteractionState is, Test t, Set<TestResult> results) {
		if (is.isEditable()) {
			return true;
		} else {
			if (results != null) {
				for (TestResult tr : results) {
					if (tr.getTest().getTestIi() == t.getTestIi())
						return true;
				}
			}
			return false;			
		}
	}
	
	public void fillData(Set<TestResult> testResults) {
		Transaction tra = RegaDBMain.getApp().createTransaction();
		
		for(int i = 0; i < testItems.size(); i++) {
			Test test = tra.getTest(testItems.get(i).description);
			if(test == null)
				continue;
			
			TestResult theTr = null;
			for (TestResult tr : testResults) {
				if (tr.getTest().getDescription().equals(test.getDescription())){
					theTr = tr;
					break;
				}
			}

			FormField f = testFormFields.get(i);
			if (theTr != null) {
				if (f instanceof ComboBox) {
					((ComboBox) f).selectItem(theTr.getTestNominalValue().getValue());
				} else {
					if (theTr.getValue() != null)
						f.setText(theTr.getValue());
					else 
						f.setText(new String(theTr.getData()));
				}
			}
		}
	}
	
	public void saveData(Set<TestResult> testResults) {
		Transaction tra = RegaDBMain.getApp().createTransaction();
		
		for(int i = 0; i < testItems.size(); i++) {
            TestResult tr = null;
            for (TestResult vi_tr : testResults) {
            	if (vi_tr.getTest().getDescription().equals(testItems.get(i).description)){
            		tr = vi_tr;
            		break;
            	}
            }
            FormField f = testFormFields.get(i);
            if(f instanceof ComboBox) {
            	DataComboMessage<TestNominalValue> dcm = (DataComboMessage<TestNominalValue>)((ComboBox)f).currentItem();
                if(dcm != null && dcm.getValue()!=null) {
                	if (tr == null)
                		tr = createTestResult(tra.getTest(testItems.get(i).description));
                    tr.setTestNominalValue(((DataComboMessage<TestNominalValue>)((ComboBox)f).currentItem()).getDataValue());
                } else if (tr != null) {
                	removeTestResult(tr);
                }
            } else {
                if(f.text()!=null && !f.text().trim().equals("")) {
                	if (tr == null)
                		tr = createTestResult(tra.getTest(testItems.get(i).description));
                    tr.setValue(f.text());
                }
                else if(tr != null){
                	removeTestResult(tr);
                }
            }
        }
	}

	public abstract void removeTestResult(TestResult tr);

	public abstract TestResult createTestResult(Test test);
}
