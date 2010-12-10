package net.sf.regadb.ui.form.administrator;

import java.util.List;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WDoubleValidator;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WValidator;

public class ContaminationOverview extends FormWidget {
	
	private WLineEdit threshold;
	private WPushButton button;
	private IsolateTable table;

	public ContaminationOverview() {
		super(WString.tr("form.contamination"), InteractionState.Viewing);
		
		if(RegaDBSettings.getInstance().getContaminationConfig().isConfigured()){
			addWidget(new WLabel(WString.tr("form.contamination.threshold")));
			
			Signal.Listener listener = new Signal.Listener() {
	            public void trigger() {
	            	if(threshold.validate() == WValidator.State.Valid)
	            		fill(Double.parseDouble(threshold.getText()));
	            }
	        };
	        
			threshold = new WLineEdit();
			//TODO lower and upper limit for threshold?
			threshold.setValidator(new WDoubleValidator());
			
			threshold.setText(""+ RegaDBSettings.getInstance().getContaminationConfig().getThreshold());
			addWidget(threshold);
			threshold.enterPressed().addListener(this, listener);
			
			button = new WPushButton(WString.tr("form.contamination.submit"));
			button.clicked().addListener(this, listener);

			addWidget(button);
			
			table = new IsolateTable(this);
			addWidget(table);
			
			fill(RegaDBSettings.getInstance().getContaminationConfig().getThreshold());
		}
		else{
			addWidget(new WLabel("form.contamination.noConfig"));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void fill(double threshold){
		table.clear();
		table.addHeader(tr("form.contamination.clusterFactor"));
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		Test test = t.getTest(StandardObjects.getContaminationClusterFactorTest().getDescription());
		
		if(test != null){
			int testIi = test.getTestIi();
			Query q = t.createQuery(
					"select p.patientId, v.sampleId, nt.label, tr.value, p.patientIi, v.viralIsolateIi" +
					" from TestResult tr join tr.ntSequence nt join nt.viralIsolate v join v.patient p" +
					" where tr.test.testIi = :testii" +
					" and cast(tr.value as double) <= :threshold" +
					" order by cast(tr.value as double) asc, p.patientId, v.sampleId, nt.label");
			q.setInteger("testii", testIi);
			q.setDouble("threshold", threshold);
			
			List<Object[]> l = q.list();
			for(Object[] o : l)
				table.addRow((Integer)o[4], (String)o[0], (Integer)o[5], (String)o[1], (String)o[2], (String)o[3]);
		}
		
		t.commit();
	}
	

	@Override
	public void saveData() {
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
}
