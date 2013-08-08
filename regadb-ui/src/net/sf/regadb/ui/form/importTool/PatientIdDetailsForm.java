package net.sf.regadb.ui.form.importTool;

import net.sf.regadb.ui.form.importTool.data.PatientIdDetails;
import net.sf.regadb.ui.form.importTool.data.Rule;
import net.sf.regadb.ui.framework.forms.fields.CheckBox;
import eu.webtoolkit.jwt.WString;

public class PatientIdDetailsForm extends DetailsForm{
	private PatientIdDetails details = new PatientIdDetails();
	
	private CheckBox updateExistingPatientsCB;

	public PatientIdDetailsForm(ImportRule rule, PatientIdDetails details) {
		super(WString.tr("form.importTool.details.patientId.title"));
		
		if (details != null) {
			this.details.setUpdateExistingPatients(details.isUpdateExistingPatients());
		}
		
		updateExistingPatientsCB = new CheckBox(rule.getForm().getInteractionState(), null, WString.tr("form.importTool.details.patientId.updateExistingPatients"));
		this.addWidget(updateExistingPatientsCB);
	}

	@Override
	public WString validate() {
		return null;
	}

	@Override
	public void save(Rule rule) {
		rule.setPatientIdDetails(details);
	}

	@Override
	public void save() {
		details.setUpdateExistingPatients(updateExistingPatientsCB.isChecked());
	}

	@Override
	public void init() {
		updateExistingPatientsCB.setChecked(details.isUpdateExistingPatients());
	}
}