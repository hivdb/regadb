package net.sf.regadb.ui.form.query.custom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.TestTypeComboBox;
import net.sf.regadb.util.date.DateUtils;

import org.hibernate.Query;

import eu.webtoolkit.jwt.WString;

public class NadirQuery extends CustomQuery {
	private DatasetParameter pDataset;
	private DateParameter pDate;
	private TestTypeParameter pTestType;

	public NadirQuery() {
		super(WString.tr("form.query.custom.nadir.name"));
	}

	@Override
	protected void init() {
		setName(WString.tr("form.query.custom.nadir.name").getValue());
		setDescription(WString.tr("form.query.custom.nadir.description").getValue());
		
		pDataset = new DatasetParameter(this, "Dataset", true);
		pDate = new DateParameter(this, "Date", true);
		pTestType = new TestTypeParameter("Test type", true,
				new TestTypeComboBox(InteractionState.Adding, this){
			public void addItem(DataComboMessage<TestType> item){
				if(item.getDataValue() != null){
					ValueTypes vt = ValueTypes.getValueType(item.getDataValue().getValueType());
					if(!(vt == ValueTypes.LIMITED_NUMBER || vt == ValueTypes.NUMBER))
						return;
				}
				super.addItem(item);
			}
		});
		
		getParameters().add(pDataset);
		getParameters().add(pTestType);
		getParameters().add(pDate);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public File run() throws Exception {
		File result = File.createTempFile("nadir_", ".csv", getResultDir());
		PrintStream out = new PrintStream(new FileOutputStream(result));

		TestType tt = pTestType.getTestTypeComboBox().currentValue();
		ValueTypes vt = ValueTypes.getValueType(tt.getValueType());
		String hql = "select tr.patient.patientId, tr.testDate, tr.value from TestResult tr join tr.patient.patientDatasets patient_dataset" +
				" where patient_dataset.id.dataset.datasetIi = :var_dataset" +
				" and tr.testDate <= :var_date" +
				" and tr.test.testIi in (select testIi from Test t where t.testType.testTypeIi = :var_testtype)" +
				" order by tr.patient.patientId, ";
		
		if(vt == ValueTypes.LIMITED_NUMBER)
			hql += "cast(substring(tr.value, 2) as double)";
		else
			hql += "cast(tr.value as double)";
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		Query q = t.createQuery(hql);
		q.setParameter("var_dataset", pDataset.getDataset().getDatasetIi());
		q.setParameter("var_date", pDate.getDate());
		q.setParameter("var_testtype", tt.getTestTypeIi());
		
		out.println("patient_id,test_date,value");
		
		List r = q.list();
		String prevPatientId = null;
		for(Object o : r){
			Object[] oo = (Object[])o;
			String patientId = (String)oo[0];
			Date testDate = (Date)oo[1];
			String value = (String)oo[2];
			
			if(!patientId.equals(prevPatientId)){
				out.println('"'+ patientId +"\",\""+ DateUtils.format(testDate) +"\",\""+ value +'"');
				prevPatientId = patientId;
			}
		}
		
		t.commit();
		out.close();
		
		return result;
	}
}
