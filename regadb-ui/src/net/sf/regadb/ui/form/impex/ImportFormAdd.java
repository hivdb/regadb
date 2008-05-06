package net.sf.regadb.ui.form.impex;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFileUpload;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WLabel;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WResource;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class ImportFormAdd extends FormWidget {
	private WTable addFileTable;
	private WComboBox datasets;
	private WFileUpload wfu;
	
	public ImportFormAdd(WMessage formName, InteractionState interactionState) {
		super(formName, interactionState);
		init();
	}
	
	private void init() {
		WGroupBox add = new WGroupBox(tr("form.impex.import.addupload"), this);

		addFileTable = new WTable();
		addFileTable.setStyleClass("spacyTable");
		add.addWidget(addFileTable);

		new WLabel(tr("form.impex.import.select"), addFileTable.elementAt(0, 0));
		new WLabel(tr("form.impex.import.dataset"), addFileTable
				.elementAt(1, 0));
		datasets = new WComboBox(addFileTable.elementAt(1, 1));
		
		wfu = new WFileUpload(addFileTable.elementAt(0, 2));
		wfu.uploaded.addListener(new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				if ( wfu.clientFileName() != null ) {
					addFileTable.elementAt(0, 1).clear();
					new WLabel(new WMessage(wfu.clientFileName(), true), addFileTable.elementAt(0, 1));
				}
			}
		});
		
		WPushButton cmdUpload = new WPushButton(WResource.tr("form.impex.import.upload"), addFileTable.elementAt(0, 2));
		cmdUpload.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				wfu.upload();
			}
		});
		
		fillComboBox();

		addControlButtons();
	}
	
	private void fillComboBox() {
		Transaction t = RegaDBMain.getApp().createTransaction();

		datasets.clear();

		for (Dataset ds : t.getDatasets()) {
			datasets.addItem(new DataComboMessage<Dataset>(ds, ds
					.getDescription()));
		}

		datasets.sort();
	}
	
	@Override
	public void cancel() {
		redirectToView(
				RegaDBMain.getApp().getTree().getTreeContent().importXMLrun,
				RegaDBMain.getApp().getTree().getTreeContent().importXMLrun);
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
		if ( wfu.clientFileName() != null ) {
			ImportFormRunning.add( new ProcessXMLImport(RegaDBMain.getApp().getLogin(), wfu, datasets.currentText().value()) );
			redirectToView(RegaDBMain.getApp().getTree().getTreeContent().importXMLrun, RegaDBMain.getApp().getTree().getTreeContent().importXMLrun);
		}
	}
}
