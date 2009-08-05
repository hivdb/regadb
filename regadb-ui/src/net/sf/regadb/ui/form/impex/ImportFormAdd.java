package net.sf.regadb.ui.form.impex;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;

public class ImportFormAdd extends FormWidget {
	private FormTable addFileTable;
	private ComboBox<Dataset> datasetCB;
	private FileUpload fileU;
	
	public ImportFormAdd(WString formName, InteractionState interactionState) {
		super(formName, interactionState);
		init();
	}
	
	private void init() {
		WGroupBox add = new WGroupBox(tr("form.impex.import.addupload"), this);

		addFileTable = new FormTable(add);

		Label fileL = new Label(tr("form.impex.import.select"));
		fileU = new FileUpload(getInteractionState(), this);
		fileU.setMandatory(true);
		addFileTable.addLineToTable(fileL, fileU);
		
		Label datasetL = new Label(tr("form.impex.import.dataset"));
		datasetCB = new ComboBox<Dataset>(getInteractionState(), this);
		datasetCB.setMandatory(true);
		addFileTable.addLineToTable(datasetL, datasetCB);
		
		fillComboBox();

		addControlButtons();
	}
	
	private void fillComboBox() {
		Transaction t = RegaDBMain.getApp().createTransaction();

		datasetCB.clearItems();

		for (Dataset ds : t.getDatasets()) {
			datasetCB.addItem(new DataComboMessage<Dataset>(ds, ds
					.getDescription()));
		}

		datasetCB.sort();
		datasetCB.addNoSelectionItem();
	}
	
	@Override
	public void cancel() {
		redirectToView(
				RegaDBMain.getApp().getTree().getTreeContent().importXMLrun,
				RegaDBMain.getApp().getTree().getTreeContent().importXMLrun);
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
		ImportFormRunning.add( new ProcessXMLImport(RegaDBMain.getApp().getLogin(), fileU.getFileUpload(), datasetCB.currentValue()));
		redirectToView(RegaDBMain.getApp().getTree().getTreeContent().importXMLrun, RegaDBMain.getApp().getTree().getTreeContent().importXMLrun);
	}
}
