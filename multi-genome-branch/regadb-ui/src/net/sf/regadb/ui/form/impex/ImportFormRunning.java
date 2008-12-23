package net.sf.regadb.ui.form.impex;

import java.util.ArrayList;

import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WTimer;

public class ImportFormRunning extends FormWidget {
	private SimpleTable table;
	private static ArrayList<ProcessXMLImport> processList = new ArrayList<ProcessXMLImport>();
	private WTimer timer = new WTimer();
	
	public ImportFormRunning(WString formName, InteractionState interactionState) {
		super(formName, interactionState);
		timer.setInterval(1000);
		timer.timeout.addListener(this, new Signal.Listener() {
			public void trigger() {
				refreshprogressTable();
			}
		});
		
		init();
		refreshprogressTable();
	}
	
	private void init() {
		WGroupBox progress = new WGroupBox(tr("form.impex.import.progress"), this);
		table = new SimpleTable(progress);
		addControlButtons();
	}
	
	public static void add(ProcessXMLImport xmlImport) {
		processList.add(xmlImport);
		xmlImport.start();
	}
	
	public void refreshprogressTable() {
		if ( table != null ) {
			table.clear();
			
			table.setHeaders( tr("form.impex.import.progress.header.user"),
					tr("form.impex.import.progress.header.file"),
					tr("form.impex.import.progress.header.dataset"),
					tr("form.impex.import.progress.header.status"));
			
			table.setWidths(20,40,20,20);
			table.elementAt(0, 4).setStyleClass("column-action");

			int row = 1;
			int running = 0;
			for (final ProcessXMLImport importXml : processList) {
				if (importXml.getStatus() == UploadStatus.PROCESSING ) {
					running++;
				}			
				
				table.elementAt(row, 0).addWidget(new WText( lt(importXml.getUid()) ));
				table.elementAt(row, 1).addWidget( new WText( lt(importXml.clientFileName()) ));
				table.elementAt(row, 2).addWidget( new WText( importXml.getDatasetName() ));
				table.elementAt(row, 3).addWidget( new WText( importXml.getStatusName() ));
				table.elementAt(row, 4).setStyleClass("column-action");				
				
//				if (importXml.getLogFile() != null) {
//					new WAnchor(new WFileResource("text/txt", importXml.getLogFile().getAbsolutePath()),
//							tr("form.impex.import.progress.logfile"),
//							table.elementAt(row, 3)).setStyleClass("link");
//				}
				
				if (importXml.getStatus() != UploadStatus.PROCESSING) {
					WPushButton clearButton = new WPushButton(tr("form.impex.import.clearchecked"), table.elementAt(row, 4));
					clearButton.clicked.addListener(this, new Signal1.Listener<WMouseEvent>() {
						public void trigger(WMouseEvent a) {
							int row = processList.indexOf(importXml);
							table.deleteRow(row+1);
							processList.remove(importXml);
							if (importXml.getLogFile() != null) {
								importXml.getLogFile().delete();
							}
						}
					});
				}
				row++;
			}
			
			// Auto refresh progress table
			if ( running == 0 && timer.isActive()) {
				 timer.stop();
			} 
			else if (running > 0 && !timer.isActive()){
				timer.start();
			}
		}
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

	@Override
	public void saveData() {
	}
}
