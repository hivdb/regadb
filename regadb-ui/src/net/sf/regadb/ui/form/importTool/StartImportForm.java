package net.sf.regadb.ui.form.importTool;

import java.io.File;
import java.util.List;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.ui.form.importTool.imp.ImportData;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WTimer;
import eu.webtoolkit.jwt.WContainerWidget.Overflow;

public class StartImportForm extends WDialog {
	private ImportToolForm importToolForm;
	
	private FileUpload xlsFile;
	private FileUpload fastaFile;
	private ComboBox<Dataset> dataset;
	private Label statusL = new Label(tr("form.importTool.start.status"));
	private WText statusT;
	private WContainerWidget statusScroll;
	
	private List<WString> errors;
	private WTimer importTimer;
	
	private WPushButton startImport;
	private WPushButton closeButton;

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
		statusScroll = new WContainerWidget();
		statusScroll.setOverflow(Overflow.OverflowScroll);
		statusScroll.setMaximumSize(WLength.Auto, new WLength(100));
		statusT = new WText(statusScroll);
		table.addLineToTable(statusL, statusScroll);
		hideErrors(true);
		
		Transaction tr = RegaDBMain.getApp().createTransaction();
		for (Dataset d : tr.getDatasets()) {
			dataset.addItem(new DataComboMessage<Dataset>(d, d.getDescription()));
		}
		
		importTimer = new WTimer();
		importTimer.setInterval(2000);
		importTimer.timeout().addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent arg) {
				if (errors != null) {
					if (errors.size() == 0) {
						statusT.setText(tr("form.importTool.start.success"));
						hideErrors(false);
					} else {
						String text = "";
						for (WString e : errors) {
							text += e.toString() + "<br/>";
						}
						statusT.setText(text);
						hideErrors(false);
					}
					
					closeButton.setEnabled(true);
					startImport.setEnabled(true);
					errors = null;
					importTimer.stop();
				}
			}
		});
		
		closeButton = new WPushButton(tr("form.importTool.start.close"));
		getContents().addWidget(closeButton);
		closeButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent arg) {
				StartImportForm.this.hide();
			}
		});
		
		startImport = new WPushButton(tr("form.importTool.start.startButton"));
		getContents().addWidget(startImport);
		startImport.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent arg) {
				statusT.setText(tr("form.importTool.start.importing"));
				closeButton.setEnabled(false);
				startImport.setEnabled(false);
				
				final WApplication app = RegaDBMain.getApp();
				
				Thread t = new Thread(new Runnable(){
					public void run() {
						app.attachThread();
						
						final Login workerLogin = RegaDBMain.getApp().getLogin().copyLogin();
						try {
							final Transaction tr = workerLogin.getTransaction(true);
	
							ImportData importData = 
								new ImportData(StartImportForm.this.importToolForm.getDefinition(), 
										new File(xlsFile.getFileUpload().getSpoolFileName()),
										new File(fastaFile.getFileUpload().getSpoolFileName()),
										dataset.currentValue());
							List<WString> errors = importData.doImport(tr, true);
							if (errors.size() == 0) {
								importData.doImport(tr, false);
							}
							
							StartImportForm.this.errors = errors;
						} finally {
							workerLogin.closeSession();
						}
					}
				});
				t.start();
				importTimer.start();
			}
		});
	}
		
	private void hideErrors(boolean hide) {
		statusScroll.setHidden(hide);
		statusL.setHidden(hide);
	}
}
