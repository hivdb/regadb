package net.sf.regadb.ui.form.singlePatient.custom;

import java.util.Collection;
import java.util.List;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.TestTypeComboBox;
import net.sf.regadb.util.date.DateUtils;

import org.hibernate.Query;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;

public class Nadir extends FormWidget{
	private TestTypeComboBox testtypes;
	private WTable results;
	
	public Nadir() {
		super(tr("form.custom.nadir"), InteractionState.Viewing);
		init();
	}
	
	private void init(){
		testtypes = new TestTypeComboBox(InteractionState.Adding, this);
		Transaction t = RegaDBMain.getApp().createTransaction();
		Query q = t.createQuery("select distinct tt from TestResult tr join tr.test.testType tt" +
				" where tr.patient.id ='"+ RegaDBMain.getApp().getSelectedPatient().getPatientIi() +'\''+
				" and tt.testObject.description = '"+ StandardObjects.getPatientTestObject().getDescription() +"'"+
				" and (tt.valueType.description = '"+ StandardObjects.getLimitedNumberValueType().getDescription() +"'"+
				" or tt.valueType.description = '"+ StandardObjects.getNumberValueType().getDescription() +"')");
				
		for(Object o : q.list()){
			TestType tt = (TestType)o;
			testtypes.addItem(new DataComboMessage<TestType>(tt, TestTypeComboBox.getLabel(tt)));
		}
		testtypes.sort();
		t.commit();
		
		testtypes.addComboChangeListener(new Signal.Listener()
        {
			public void trigger()
			{
                show(testtypes.currentValue());
			}
        });
		addWidget(testtypes);
		
		if(testtypes.size() > 0)
			show(testtypes.currentValue());
	}
	
	@SuppressWarnings("unchecked")
	private void show(TestType tt){
		if(tt == null)
			return;
		
		ValueTypes vt = ValueTypes.getValueType(tt.getValueType());
		if(vt == ValueTypes.LIMITED_NUMBER || vt == ValueTypes.NUMBER){
			Transaction t = RegaDBMain.getApp().createTransaction();
			Query q = t.createQuery("select tr from TestResult tr where"
					+" tr.test.testType.id='"+ tt.getTestTypeIi() +"'"
					+" and tr.patient.id='"+ RegaDBMain.getApp().getSelectedPatient().getPatientIi() +"'"
					+" order by tr.testDate asc");
			fill(vt, (List<TestResult>)q.list());
			t.commit();
		}
		else{
			
		}
		
	}
	
	private void fill(ValueTypes vt, Collection<TestResult> trs){
		Double min = null;
		
		if(results != null)
			removeWidget(results);
		results = new WTable(this);
		results.getElementAt(0, 0).addWidget(new WLabel(tr("form.custom.nadir.date")));
		results.getElementAt(0, 1).addWidget(new WLabel(tr("form.custom.nadir.value")));
		results.getElementAt(0, 2).addWidget(new WLabel(tr("form.custom.nadir.test")));
		results.getElementAt(0, 3).addWidget(new WLabel(tr("form.custom.nadir.sampleid")));

		int i=0;
		for(TestResult tr :  trs){
			double cur = Double.parseDouble(vt == ValueTypes.LIMITED_NUMBER ? tr.getValue().substring(1) : tr.getValue());
			if(min == null || cur < min){
				results.insertRow(++i);
				results.getElementAt(i, 0).addWidget(new WText(DateUtils.format(tr.getTestDate())));
				results.getElementAt(i, 1).addWidget(new WText(tr.getValue(),TextFormat.PlainText));
				results.getElementAt(i, 2).addWidget(new WText(tr.getTest().getDescription(),TextFormat.PlainText));
				if(tr.getSampleId() != null)
					results.getElementAt(i, 3).addWidget(new WText(tr.getSampleId(),TextFormat.PlainText));
				
				min = cur;
			}
		}
		
		addWidget(results);
	}

	@Override
	public void cancel() {
	}

	@Override
	public WString deleteObject() {
		return null;
	}

	@Override
	public void redirectAfterDelete() {
	}

	@Override
	public void saveData() {
	}

	@Override
	public void redirectAfterSave() {
	}

	@Override
	public void redirectAfterCancel() {
	}
}
