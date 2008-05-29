package net.sf.regadb.ui.form.impex;

import java.util.ArrayList;

import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFileResource;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WLabel;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WResource;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WTimer;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.i8n.WMessage;

public class ImportFormRunning extends FormWidget {
	private static WTable progressTable, progressControlTable;
	private static WPushButton cmdClearChecked;
	private static ArrayList<ProcessXMLImport> processList = new ArrayList<ProcessXMLImport>();
	private static WTimer t = new WTimer();
	
	public ImportFormRunning(WMessage formName, InteractionState interactionState) {
		super(formName, interactionState);
		
		init();
		
		t.setInterval(1000);
		t.timeout.addListener(new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				refreshprogressTable();
			}
		});

		refreshprogressTable();
	}
	
	private void init() {
		WGroupBox progress = new WGroupBox(tr("form.impex.import.progress"), this);
		
		progressTable = new WTable();
		progressTable.setStyleClass("spacyTable");
		progress.addWidget(progressTable);
		
		progressControlTable = new WTable();
		progressControlTable.setStyleClass("spacyTable");
		progressControlTable.elementAt(0, 1).setContentAlignment(WHorizontalAlignment.AlignRight);
		progress.addWidget(progressControlTable);
		
		cmdClearChecked = new WPushButton(tr("form.impex.import.clearchecked"), progressControlTable.elementAt(0, 1));
		cmdClearChecked.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				for (int i = 0; i < processList.size(); i++) {
					ProcessXMLImport fu = processList.get(i);
					if (fu.isChecked()) {
						removeUpload(i);
						i--;
					}
				}
				refreshprogressTable();
			}
		});
	}
	
	public static void add(ProcessXMLImport xmlImport) {
		processList.add(xmlImport);
		xmlImport.start();
		refreshprogressTable();
	}
	
	private void removeUpload(int index) {
		ProcessXMLImport fu = processList.get(index);
		if (fu.getLogFile() != null) fu.getLogFile().delete();
		processList.remove(index);
	}
	
	public static void refreshprogressTable() {
		if ( progressTable != null ) {
			progressTable.clear();
			
			int row = 0;
			
			progressControlTable.elementAt(0, 0).clear();
			
			String headers[] = { "form.impex.import.progress.header.user",
					"form.impex.import.progress.header.file",
					"form.impex.import.progress.header.dataset",
					"form.impex.import.progress.header.status" };
			
			for(String head : headers) {
				new WLabel(WResource.tr(head), progressTable.elementAt(row, progressTable.numColumns())).setStyleClass("table-header-bold");
			}
			
			int running = 0;
			for (final ProcessXMLImport importXml : processList) {
				row++;
				new WLabel(new WMessage(importXml.getUid(), true), progressTable.elementAt(row, 0));
				new WLabel(new WMessage(importXml.clientFileName(), true), progressTable.elementAt(row, 1));
				new WLabel(importXml.getDatasetName(), progressTable.elementAt(row, 2));
				new WLabel(importXml.getStatusName(), progressTable.elementAt(row, 3));
				
				if (importXml.getLogFile() != null) {
					new WAnchor(new WFileResource("text/txt", importXml.getLogFile().getAbsolutePath()),
							WResource.tr("form.impex.import.progress.logfile"),
							progressTable.elementAt(row, 4)).setStyleClass("link");
				}
				
				if ( !importXml.getStatusName().key().equals("form.impex.import.progress.status.processing") ) {
					final WCheckBox chk = new WCheckBox( new WMessage(" ", true), progressTable.elementAt(row, 5) );
					chk.clicked.addListener(new SignalListener<WMouseEvent>() {
						public void notify(WMouseEvent a) {
							importXml.setChecked(chk.isChecked());
						}
					});
					chk.setChecked(importXml.isChecked());
				} else {
					importXml.setChecked(false);
				}
				
				if ( importXml.getStatus() == UploadStatus.PROCESSING ) {
					running++;
				}
			}
			
			// Auto refresh progress table
			if ( running == 0 ) {
				if ( t.isActive() ) t.stop();
			} else {
				if ( !t.isActive() ) t.start();
			}
		}
	}

	@Override
	public void cancel() {
	}

	@Override
	public WMessage deleteObject() {
		return null;
	}

	@Override
	public void redirectAfterDelete() {
	}

	@Override
	public void saveData() {
	}
}
