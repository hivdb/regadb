package net.sf.regadb.ui.form.administrator;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.RegaDBMain;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;

public class ContaminationTable extends WTable {

	public ContaminationTable(WContainerWidget parent){
		super(parent);
		setStyleClass("datatable contamination");
	}
	
	public void addHeader(){
		getElementAt(0,0).addWidget(new WLabel(WString.tr("form.contamination.patientId")));
		getElementAt(0,1).addWidget(new WLabel(WString.tr("form.contamination.sampleId")));
		getElementAt(0,2).addWidget(new WLabel(WString.tr("form.contamination.label")));
		getElementAt(0,3).addWidget(new WLabel(WString.tr("form.contamination.clusterFactor")));
		
		getRowAt(0).setStyleClass("header");
	}
	
	public void add(final int patientIi, String patientId, final int viralIsolateIi, String sampleId, String label, String value){
		int n = getRowCount();

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
		
		getElementAt(n,0).addWidget(lblPatient);
		getElementAt(n,1).addWidget(lblSample);
		getElementAt(n,2).addWidget(lblLabel);
		getElementAt(n,3).addWidget(lblValue);
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
}
