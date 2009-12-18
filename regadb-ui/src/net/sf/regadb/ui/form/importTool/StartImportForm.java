package net.sf.regadb.ui.form.importTool;

import java.io.File;
import java.util.List;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.importTool.imp.ImportData;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

public class StartImportForm extends WDialog {
	private ImportToolForm importToolForm;
	
	private FileUpload xlsFile;
	private FileUpload fastaFile;
	private ComboBox<Dataset> dataset;
	private Label errorsL = new Label(tr("form.importTool.start.errors"));
	private WText errorsT;

	public StartImportForm(ImportToolForm importToolForm) {
		super(tr("form.importTool.start.title"));
		
		this.importToolForm = importToolForm;
		
		FormTable table = new FormTable(this.getContents());
		
		Label xlsFileL = new Label(tr("form.importTool.start.xlsFile"));
		xlsFile = new FileUpload(InteractionState.Editing, null);
		table.addLineToTable(xlsFileL, xlsFile);
		Label fastaFileL = new Label(tr("form.importTool.start.fastaFile"));
		fastaFile = new FileUpload(InteractionState.Editing, null);
		table.addLineToTable(fastaFileL, fastaFile);
		Label datasetL = new Label(tr("form.importTool.start.dataset"));
		dataset = new ComboBox<Dataset>(InteractionState.Editing, null);
		table.addLineToTable(datasetL, dataset);
		errorsT = new WText();
		table.addLineToTable(errorsL, errorsT);
		hideErrors(true);
		
		Transaction tr = RegaDBMain.getApp().createTransaction();
		for (Dataset d : tr.getDatasets()) {
			dataset.addItem(new DataComboMessage<Dataset>(d, d.getDescription()));
		}
		
		WPushButton startImport = new WPushButton(tr("form.importTool.start.startButton"));
		getContents().addWidget(startImport);
		startImport.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent arg) {
				ImportData importData = 
					new ImportData(StartImportForm.this.importToolForm.getDefinition(), 
							new File(xlsFile.getFileUpload().getSpoolFileName()),
							new File(fastaFile.getFileUpload().getSpoolFileName()),
							dataset.currentValue());
				List<WString> errors = importData.doImport(true);
				if (errors == null) {
					importData.doImport(false);
					errorsT.setText(tr("form.importTool.start.success"));
					hideErrors(false);
				} else {
					String text = "";
					for (WString e : errors) {
						text += e.toString() + "<br/>";
 					}
					errorsT.setText(text);
					hideErrors(false);
				}
			}
		});
		
		WPushButton close = new WPushButton(tr("form.importTool.start.close"));
		getContents().addWidget(close);
		close.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent arg) {
				StartImportForm.this.hide();
			}
		});
	}
		
	private void hideErrors(boolean hide) {
		errorsT.setHidden(hide);
		errorsL.setHidden(hide);
	}
}
