package net.sf.regadb.ui.form.impex;

import java.io.File;
import java.io.FileOutputStream;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.exportXML.ExportPatient;
import net.sf.regadb.io.exportXML.ExportToXMLOutputStream.PatientXMLOutputStream;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WFileResource;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class ExportForm extends FormWidget {
	private FormTable table_;
	private ComboBox<Dataset> datasets;
	private File exportFile;
	
	public ExportForm(WString formName, InteractionState interactionState) {
		super(formName, interactionState);
		init();
	}
	
	public void init() {
		table_ = new FormTable(this);
		
		Label datasetsL = new Label(tr("form.impex.export.dataset"));;
		datasets = new ComboBox<Dataset>(InteractionState.Editing, this);
		fillData();
		table_.addLineToTable(datasetsL, datasets);
		
		
		WPushButton export = new WPushButton(tr("form.impex.export.title"));
		Label exportL = new Label(tr("form.impex.export.title"));
		WWidget[] widgets = {exportL, export};
		table_.addLineToTable(widgets);
		
		export.clicked.addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
			    try{
    			    Dataset ds = datasets.currentValue();
    
                    deleteExportFile();
                    exportFile = RegaDBMain.getApp().createTempFile(ds.getDescription() + "_export", "xml");
                    FileOutputStream fout = new FileOutputStream(exportFile);
                    PatientXMLOutputStream xmlout = new PatientXMLOutputStream(fout);
                    
                    ExportPatient<Patient> exportPatient = new ExportPatient<Patient>(RegaDBMain.getApp().getLogin(),ds.getDescription(),xmlout);
                    exportPatient.run();
                    
                    table_.elementAt(0, 2).clear();
                    
                    new WAnchor(new WFileResource("text/txt", exportFile.getAbsolutePath()),
                    		lt(ds.getDescription() + "_export.xml"),
                    		table_.elementAt(0, 2)).setStyleClass("link");
                }catch(Exception e){
                    e.printStackTrace();
                }
			}
		});
		
		addControlButtons();
    }
	
	public void fillData() {
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		// Fill in dataset combo box
//		datasets.clearItems();
        for(Dataset ds : t.getDatasets())
        {
        	datasets.addItem(new DataComboMessage<Dataset>(ds, ds.getDescription()));
        }
        datasets.sort();
        datasets.selectIndex(0);
	}
	
	private void deleteExportFile() {
		if ( exportFile != null ) exportFile.delete();
	}
	
	@Override
	public void cancel() {}
	
	@Override
	public WString deleteObject() {return null;}
	
	@Override
	public void redirectAfterDelete() {}
	
	@Override
	public void saveData() {}
	
	@Override
	public WString leaveForm() {
		deleteExportFile();
		return null;
	}
}
