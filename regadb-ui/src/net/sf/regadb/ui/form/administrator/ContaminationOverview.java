package net.sf.regadb.ui.form.administrator;

import java.util.List;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

import eu.webtoolkit.jwt.Key;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WDoubleValidator;
import eu.webtoolkit.jwt.WKeyEvent;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WValidator;

public class ContaminationOverview extends FormWidget {
	
	private WLineEdit threshold;
	private WPushButton button;
	private WTable table;

	public ContaminationOverview() {
		super(WString.tr("form.contamination"), InteractionState.Viewing);
		
		if(RegaDBSettings.getInstance().getContaminationConfig().isConfigured()){
			addWidget(new WLabel(WString.tr("form.contamination.threshold")));
			
			threshold = new WLineEdit();
			//TODO lower and upper limit for threshold?
			threshold.setValidator(new WDoubleValidator());
			threshold.keyPressed().addListener(this, new Signal1.Listener<WKeyEvent>() {
				public void trigger(WKeyEvent ke) {
					if (ke.getCharCode() == Key.Key_Enter.getValue())
						update();
				}
			});
			
			threshold.setText(""+ RegaDBSettings.getInstance().getContaminationConfig().getThreshold());
			addWidget(threshold);
			
			button = new WPushButton(WString.tr("form.contamination.submit"));
			button.clicked().addListener(this, new Signal.Listener() {
	            public void trigger() {
	            	update();
	            }
	        });
			addWidget(button);
			
			table = new WTable(this);
			table.setStyleClass("datatable contamination");
			addWidget(table);
			
			fill(RegaDBSettings.getInstance().getContaminationConfig().getThreshold());
		}
		else{
			addWidget(new WLabel("form.contamination.noConfig"));
		}
	}
	
	private void update() {
    	if(threshold.validate() == WValidator.State.Valid)
    		fill(Double.parseDouble(threshold.getText()));
	}
	
	@SuppressWarnings("unchecked")
	private void fill(double threshold){
		Transaction t = RegaDBMain.getApp().createTransaction();
		Test test = t.getTest(StandardObjects.getContaminationClusterFactorTest().getDescription());
		
		if(test != null){
			int testIi = test.getTestIi();
			Query q = t.createQuery(
					"select p.patientId, v.sampleId, nt.label, tr.value, p.patientIi, v.viralIsolateIi" +
					" from TestResult tr join tr.ntSequence nt join nt.viralIsolate v join v.patient p" +
					" where tr.test.testIi = :testii" +
					" and cast(tr.value as double) >= :threshold" +
					" order by p.patientId, v.sampleId, nt.label");
			q.setInteger("testii", testIi);
			q.setDouble("threshold", threshold);
			
			List<Object[]> l = q.list();
			
			table.clear();
			table.getElementAt(0,0).addWidget(new WLabel(WString.tr("form.contamination.patientId")));
			table.getElementAt(0,1).addWidget(new WLabel(WString.tr("form.contamination.sampleId")));
			table.getElementAt(0,2).addWidget(new WLabel(WString.tr("form.contamination.label")));
			table.getElementAt(0,3).addWidget(new WLabel(WString.tr("form.contamination.clusterFactor")));
			
			table.getRowAt(0).setStyleClass("header");
			
			int n = 1;
			
			for(Object[] o : l){
				String patientId = (String)o[0];
				String sampleId = (String)o[1];
				String label = (String)o[2];
				String value = (String)o[3];
				final Integer patientIi = (Integer)o[4];
				final Integer viralIsolateIi = (Integer)o[5];
				
				WLabel lblPatient = new WLabel(patientId);
				lblPatient.clicked().addListener(this, new Signal.Listener() {
		            public void trigger() {
		            	gotoPatient(patientIi);
		            }
		        });
				
				Signal.Listener toViralIsolate = new Signal.Listener(){
					public void trigger(){
						gotoViralIsolate(patientIi, viralIsolateIi);
					}
				};
				
				WLabel lblSample = new WLabel(sampleId);
				lblSample.clicked().addListener(this, toViralIsolate);
				
				WLabel lblLabel = new WLabel(label);
				lblLabel.clicked().addListener(this, toViralIsolate);
				
				WLabel lblValue = new WLabel(value);
				lblValue.clicked().addListener(this, toViralIsolate);
				
				table.getElementAt(n,0).addWidget(lblPatient);
				table.getElementAt(n,1).addWidget(lblSample);
				table.getElementAt(n,2).addWidget(lblLabel);
				table.getElementAt(n,3).addWidget(lblValue);
				++n;
			}
		}
		
		t.commit();
	}
	
	public void gotoPatient(int patientIi){
		Transaction t = RegaDBMain.getApp().createTransaction();
		Patient p = t.getPatient(patientIi);
		t.commit();
		
		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.setSelectedItem(p);
		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViewActionItem().prograSelectNode();
	}
	
	public void gotoViralIsolate(int patientIi, int viralIsolateIi){
		Transaction t = RegaDBMain.getApp().createTransaction();
		Patient p = t.getPatient(patientIi);
		ViralIsolate v = t.getViralIsolate(viralIsolateIi);
		t.commit();
		
		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.setSelectedItem(p);
		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode().setSelectedItem(v);
		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode().getViewActionItem().prograSelectNode();
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
