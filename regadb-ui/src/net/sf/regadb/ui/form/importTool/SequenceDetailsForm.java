package net.sf.regadb.ui.form.importTool;

import net.sf.regadb.ui.form.importTool.data.Rule;
import net.sf.regadb.ui.form.importTool.data.SequenceDetails;
import net.sf.regadb.ui.form.importTool.data.SequenceDetails.SequenceRetrievalOptions;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import eu.webtoolkit.jwt.WString;

public class SequenceDetailsForm extends DetailsForm {
	private ComboBox<SequenceRetrievalOptions> retrievalOptionsCombo;
	private SequenceDetails details = new SequenceDetails();

	public SequenceDetailsForm (ImportRule rule, SequenceDetails localDetails) {
		super(tr("form.importTool.details.sequences.title"));
		
		Label retrievalOptionsL = new Label(tr("form.importTool.details.sequences.retrievalOptions"));
		addWidget(retrievalOptionsL);
		retrievalOptionsCombo = new ComboBox<SequenceRetrievalOptions>(rule.getForm().getInteractionState(), null);
		addWidget(retrievalOptionsCombo);
		for (SequenceRetrievalOptions sro : SequenceRetrievalOptions.values()) {
			retrievalOptionsCombo.addItem(new DataComboMessage<SequenceRetrievalOptions>(sro, sro.getText()));
		}
		
		if (localDetails != null)
			details.setRetrievalOptions(localDetails.getRetrievalOptions());
		
		if (details.getRetrievalOptions() != null) 
			retrievalOptionsCombo.selectItem(details.getRetrievalOptions().getText());
	}
	
	public void init() {

	}

	public void save(Rule rule) {
		rule.setSequenceDetails(details);
	}

	public void save() {
		details.setRetrievalOptions(retrievalOptionsCombo.currentValue());
	}

	public WString validate() {
		return null;
	}
}
