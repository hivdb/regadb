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

public class IsolateTable extends WTable {

	public IsolateTable(WContainerWidget parent){
		super(parent);
		setStyleClass("datatable isolate-table");
	}
	
	public void addHeader(CharSequence ... extraFields){
		getElementAt(0,0).addWidget(new WLabel(WString.tr("form.isolate-table.patientId")));
		getElementAt(0,1).addWidget(new WLabel(WString.tr("form.isolate-table.sampleId")));
		getElementAt(0,2).addWidget(new WLabel(WString.tr("form.isolate-table.label")));
		
		for (int i = 0; i < extraFields.length; i++) 
			getElementAt(0, i + 3).addWidget(new WLabel(extraFields[i]));
		
		getRowAt(0).setStyleClass("header");
	}
	
	public void addRow(final int patientIi, String patientId, final int viralIsolateIi, String sampleId, String label, CharSequence ... extraFields){
		int n = getRowCount();

		WLabel lblPatient = new WLabel(patientId);
		lblPatient.setStyleClass("text-link");
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
		lblSample.setStyleClass("text-link");
		lblSample.clicked().addListener(this, toViralIsolate);
		
		WLabel lblLabel = new WLabel(label);
		lblLabel.setStyleClass("text-link");
		lblLabel.clicked().addListener(this, toViralIsolate);
		
		getElementAt(n,0).addWidget(lblPatient);
		getElementAt(n,1).addWidget(lblSample);
		getElementAt(n,2).addWidget(lblLabel);
		
		for (int i = 0; i < extraFields.length; i++) 
			getElementAt(n, i + 3).addWidget(new WLabel(extraFields[i]));
	}
	
	public void gotoPatient(int patientIi){
		Transaction t = RegaDBMain.getApp().createTransaction();
		Patient p = t.getPatient(patientIi);
		t.commit();
		
		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.setSelectedItem(p);
	}
	
	public void gotoViralIsolate(int patientIi, int viralIsolateIi){
		Transaction t = RegaDBMain.getApp().createTransaction();
		Patient p = t.getPatient(patientIi);
		ViralIsolate v = t.getViralIsolate(viralIsolateIi);
		t.commit();
		
		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.setSelectedItem(p);
		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode().setSelectedItem(v);
	}
}
