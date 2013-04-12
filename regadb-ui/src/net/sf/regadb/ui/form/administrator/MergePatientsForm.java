package net.sf.regadb.ui.form.administrator;

import java.io.IOException;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientImplHelper;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.StandardButton;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WMessageBox;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;

public class MergePatientsForm extends FormWidget {

	private ComboBox<Dataset> dsPatientA;
	private ComboBox<Dataset> dsPatientB;
	private WLineEdit lePatientA;
	private WLineEdit lePatientB;
	private WPushButton pbDryRun;
	private WPushButton pbRun;
	private WText tLog;

	public MergePatientsForm() {
		super(tr("form.mergePatients"), InteractionState.Viewing);
		
		new WText(tr("form.mergePatients.help"), TextFormat.PlainText, this);
		
		WTable table = new WTable(this);

		new WLabel(tr("form.mergePatients.patientA"), table.getElementAt(0, 0));
		dsPatientA = new ComboBox<Dataset>(InteractionState.Editing, this);
		table.getElementAt(0, 1).addWidget(dsPatientA);
		lePatientA = new WLineEdit(table.getElementAt(0, 2));

		new WLabel(tr("form.mergePatients.patientB"), table.getElementAt(1, 0));
		dsPatientB = new ComboBox<Dataset>(InteractionState.Editing, this);
		table.getElementAt(1, 1).addWidget(dsPatientB);
		lePatientB = new WLineEdit(table.getElementAt(1, 2));
		
		pbDryRun = new WPushButton(tr("form.mergePatients.dryRun"), table.getElementAt(2, 0));
		pbRun = new WPushButton(tr("form.mergePatients.run"), table.getElementAt(2, 0));

		
		tLog = new WText(this);
		tLog.setTextFormat(TextFormat.PlainText);
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		for(Dataset dataset : t.getCurrentUsersDatasets(Privileges.READWRITE)){
			dsPatientA.addItem(new DataComboMessage<Dataset>(dataset, dataset.getDescription()));
			dsPatientB.addItem(new DataComboMessage<Dataset>(dataset, dataset.getDescription()));
		}
		t.commit();
		
		Signal.Listener dryRun = new Signal.Listener() {
			@Override
			public void trigger() {
				doDryRun();
			}
		};
		pbDryRun.clicked().addListener(this, dryRun);
		
		Signal.Listener run = new Signal.Listener() {
			@Override
			public void trigger() {
				doRun();
			}
		};
		pbRun.clicked().addListener(this, run);
	}
	
	private void doDryRun(){
		merge(true);
	}
	
	private void doRun(){
		tLog.setText("");
		
		final WMessageBox cmb = UIUtils.createYesNoMessageBox(this, tr("form.mergePatients.runWarning"));
        cmb.buttonClicked().addListener(this, new Signal1.Listener<StandardButton>(){
			public void trigger(StandardButton sb) {
				cmb.remove();
				if(sb==StandardButton.Yes) {
					merge(false);
				}
			}
        });
        cmb.show();
	}
	
	private void merge(boolean dryrun){
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		Patient patientA = t.getPatient(dsPatientA.currentValue(), lePatientA.getText());
		if(patientA == null)
			flagInvalid(lePatientA);
		else
			flagValid(lePatientA);
		
		Patient patientB = t.getPatient(dsPatientB.currentValue(), lePatientB.getText());
		if(patientB == null)
			flagInvalid(lePatientB);
		else
			flagValid(lePatientB);
		
		if(patientA != null
				&& patientB != null
				&& patientA != patientB){
			try {
				StringBuilder sb = new StringBuilder();
				sb.append(dryrun ? "Simulating merge.\n\n" : "Merging.\n\n");

				PatientImplHelper.mergePatients(patientA, patientB, sb, dryrun);

				tLog.setText(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			tLog.setText("");
		}
		
		if(dryrun)
			t.rollback();
		else
			t.commit();
	}
		

	@Override
	public void saveData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WString deleteObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void redirectAfterDelete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void redirectAfterSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void redirectAfterCancel() {
		// TODO Auto-generated method stub
		
	}

}
