package net.sf.regadb.ui.form.impex;

import java.util.ArrayList;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFileResource;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WLabel;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WResource;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WTableCell;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.i8n.WMessage;

public class ImportForm extends FormWidget {
	private WTable progressTable, progressControlTable, addFileTable;
	private WComboBox datasets;
	private static ArrayList<XMLFileUpload> uploadList = new ArrayList<XMLFileUpload>();
	private WPushButton cmdClearChecked;
	
	public ImportForm(WMessage formName, InteractionState interactionState) {
		super(formName, interactionState);
		init();
	}
	
	private void init()
    {
		WGroupBox add = new WGroupBox(tr("form.impex.import.addupload"), this);
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
				for(int i=0; i<uploadList.size(); i++) {
					XMLFileUpload fu = uploadList.get(i);
					if ( fu.isChecked() ) {
						removeUpload(i);
						i--;
					}
				}
				refreshprogressTable();
			}
		});
		
		addFileTable = new WTable();
		addFileTable.setStyleClass("spacyTable");
		add.addWidget(addFileTable);
		
		new WLabel(tr("form.impex.import.select"), addFileTable.elementAt(0, 0));
		
		new WLabel(tr("form.impex.import.dataset"), addFileTable.elementAt(1, 0));
		datasets = new WComboBox(addFileTable.elementAt(1, 1));
		
		WPushButton cmdadd = new WPushButton(tr("form.impex.import.addupload"), addFileTable.elementAt(2, 1));
		cmdadd.clicked.addListener(new SignalListener<WMouseEvent>(){
			public void notify(WMouseEvent a) {
				addFileUpload();
			}
		});
		((WTableCell)cmdadd.parent()).setContentAlignment(WHorizontalAlignment.AlignRight);
		
		fillComboBox();
		refreshprogressTable();
		refreshAddForm();
    }
	
	private void fillComboBox() {
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		datasets.clear();
		
        for(Dataset ds : t.getDatasets())
        {
        	datasets.addItem(new DataComboMessage<Dataset>(ds, ds.getDescription()));
        }
        
        datasets.sort();
	}
	
	private void refreshprogressTable() {
		progressTable.clear();
		
		for(int i=0; i<uploadList.size(); i++) {
			XMLFileUpload fu = uploadList.get(i);
			
			if ( fu.harakiri() ) {
				removeUpload(i);
				i--;
				break;
			}
			
			new WLabel(new WMessage(fu.clientFileName(), true), progressTable.elementAt(i, 0));
			new WLabel(fu.getDatasetName(), progressTable.elementAt(i, 1));
			new WLabel(fu.getStatus(), progressTable.elementAt(i, 2));
			
			if ( fu.getLog() != null ) { 
				WAnchor anch = new WAnchor(new WFileResource("text/plain", fu.getLog().getAbsolutePath()), WResource.tr("form.impex.import.progress.logfile"), progressTable.elementAt(i, 3));
				anch.setStyleClass("link");
			}
			
			fu.setCheckbox(new WCheckBox(new WMessage(" ", true), progressTable.elementAt(i, 4)));
		}
		
		progressControlTable.elementAt(0, 0).clear();
		
		if ( uploadList.size() == 0 ) {
			new WLabel(tr("form.impex.import.progress.none"), progressControlTable.elementAt(0, 0));
		}
		
		if ( uploadList.size() == 0 ) {
			cmdClearChecked.setHidden(true);
		} else {
			cmdClearChecked.setHidden(false);
		}
	}
	
	private void refreshAddForm() {
		WTableCell cell = addFileTable.elementAt(0, 1);
		new XMLFileUpload(cell);
		if ( datasets != null ) datasets.setCurrentIndex(0);
	}
	
	private void addFileUpload() {
		final XMLFileUpload file = (XMLFileUpload)addFileTable.elementAt(0, 1).children().get(addFileTable.elementAt(0, 1).children().size() - 1);
		
		file.uploaded.addListener(new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				while ( file.getUploadStatus() == UploadStatus.PROCESSING ) {}
				refreshprogressTable();
			}
		});
		
		file.setDatasetName(datasets.currentText().value());
		file.upload();
		
		uploadList.add(file);
		
		file.setHidden(true);
		
		refreshAddForm();
		refreshprogressTable();
	}
	
	private void removeUpload(int index) {
		XMLFileUpload fu = uploadList.get(index);
		if ( fu.getLog() != null ) fu.getLog().delete();
		uploadList.remove(index);
	}
	
	@Override
	public void cancel() {}
	
	@Override
	public WMessage deleteObject() {return null;}
	
	@Override
	public void redirectAfterDelete() {}
	
	@Override
	public void saveData() {}
}
