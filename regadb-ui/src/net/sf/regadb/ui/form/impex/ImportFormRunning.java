package net.sf.regadb.ui.form.impex;

import java.util.ArrayList;

import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.WTimer;
import net.sf.witty.wt.i8n.WMessage;

public class ImportFormRunning extends FormWidget {
	private SimpleTable table;
	private static ArrayList<ProcessXMLImport> processList = new ArrayList<ProcessXMLImport>();
	private WTimer timer = new WTimer();
	
	public ImportFormRunning(WMessage formName, InteractionState interactionState, boolean literal) {
		super(formName, interactionState, literal);
		timer.setInterval(1000);
		timer.timeout.addListener(new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				refreshprogressTable();
			}
		});
		
		init();
		refreshprogressTable();
	}
	
	private void init() {
		WGroupBox progress = new WGroupBox(tr("general.status.progress"), this);
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
			
			table.setHeaders( tr("account.user"),
					tr("import.file"),
					tr("dataset.form"),
					tr("general.status"));
			
			table.setWidths(20,40,20,20);
			table.elementAt(0, 4).setStyleClass("column-action");

			int row = 1;
			int running = 0;
			for (final ProcessXMLImport importXml : processList) {
				if (importXml.getStatus() == UploadStatus.PROCESSING ) {
					running++;
				}			
				
				table.putElementAt(row, 0, new WText( lt(importXml.getUid()) ));
				table.putElementAt(row, 1, new WText( lt(importXml.clientFileName()) ));
				table.putElementAt(row, 2, new WText( importXml.getDatasetName() ));
				table.putElementAt(row, 3, new WText( importXml.getStatusName() ));
				table.elementAt(row, 4).setStyleClass("column-action");				
				
				if (importXml.getStatus() != UploadStatus.PROCESSING) {
					WPushButton clearButton = new WPushButton(tr("general.clear"), table.elementAt(row, 4));
					clearButton.clicked.addListener(new SignalListener<WMouseEvent>() {
						public void notify(WMouseEvent a) {
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
