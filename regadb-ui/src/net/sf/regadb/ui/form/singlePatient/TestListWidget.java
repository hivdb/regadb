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
import net.sf.regadb.util.settings.TestItem;
import net.sf.regadb.util.settings.UITestItem;

public abstract class TestListWidget {
	private List<UITestItem> testItems;
	
	private List<FormField> testFormFields = new ArrayList<FormField>();
	
	public TestListWidget(InteractionState is, List<UITestItem> testItems, Set<TestResult> results) {
		this.testItems = new ArrayList<UITestItem>(testItems);
		
		Transaction tr = RegaDBMain.getApp().createTransaction();
		Iterator<UITestItem> i = this.testItems.iterator();
		while (i.hasNext()) {
			UITestItem ti = i.next();
			Test t = getTest(tr, ti);
			if (!showTest(is, t, results))
				i.remove();
		}
	}
	
	public void init(InteractionState interactionState, IForm form, FormTable table) {
		Transaction tr = RegaDBMain.getApp().createTransaction();
		
		for(UITestItem ti : testItems) {
        	Test t = getTest(tr, ti);
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
			TestItem ti = testItems.get(i);
			Test test = getTest(tra, ti);
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
                	TestItem ti = testItems.get(i);
                	if (tr == null)
                		tr = createTestResult(getTest(tra, ti));
                    tr.setTestNominalValue(((DataComboMessage<TestNominalValue>)((ComboBox)f).currentItem()).getDataValue());
                } else if (tr != null) {
                	removeTestResult(tr);
                }
            } else {
                if(f.text()!=null && !f.text().trim().equals("")) {
                	TestItem ti = testItems.get(i);
                	if (tr == null)
                		tr = createTestResult(getTest(tra, ti));
                    tr.setValue(f.text());
                }
                else if(tr != null){
                	removeTestResult(tr);
                }
            }
        }
	}
	
	public Test getTest(Transaction tr, TestItem ti) {
		return tr.getTest(ti.description, ti.type, ti.organism);
	}

	public abstract void removeTestResult(TestResult tr);

	public abstract TestResult createTestResult(Test test);
}
