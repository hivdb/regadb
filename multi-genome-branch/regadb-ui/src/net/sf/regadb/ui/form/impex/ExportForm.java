package net.sf.regadb.ui.form.impex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.exportCsv.FullCsvExport;
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
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WFileResource;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;

public class ExportForm extends FormWidget {
	private FormTable table_;
	private ComboBox<Dataset> datasets;
	private WComboBox format;
	private WAnchor anchor;
	private File exportFile;
	
	public ExportForm(WString formName, InteractionState interactionState) {
		super(formName, interactionState);
		init();
	}
	
	public void init() {
		table_ = new FormTable(this);
		
		Label datasetsL = new Label(tr("form.impex.export.dataset"));
		datasets = new ComboBox<Dataset>(InteractionState.Editing, this);
		
		fillData();
		table_.addLineToTable(datasetsL, datasets);
		
		Label formatL = new Label(tr("form.impex.export.format"));
		format = new WComboBox();
		format.addItem(lt("XML"));
		format.addItem(lt("CSV"));
		table_.addLineToTable(formatL, format);
		
		WPushButton export = new WPushButton(tr("form.impex.export.title"));
		Label exportL = new Label(tr("form.impex.export.title"));
		anchor = new WAnchor();
		anchor.setStyleClass("link");
		anchor.setHidden(true);
		table_.addLineToTable(exportL, export, anchor);
		
		
		export.clicked.addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
			    try{
			    	anchor.setHidden(true);
    			    Dataset ds = datasets.currentValue();
    			    deleteExportFile();
    			    if(format.currentText().value().equals("XML")) {
    			    	exportXml(ds);
    			    } else if (format.currentText().value().equals("CSV")){
    			    	exportCsv(ds);
    			    }
    			    anchor.setHidden(false);
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
			}
		});
		
		addControlButtons();
    }
	
	private void exportCsv(Dataset ds) {
		exportFile = RegaDBMain.getApp().createTempFile(ds.getDescription() + "_export", "zip");
		FullCsvExport csvExport = new FullCsvExport();
		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
		
		try {
			csvExport.export(t.getPatients(ds), t.getAttributes(), exportFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        table_.elementAt(0, 2).clear();
        
        String fileName = ds.getDescription() + "_csv_export.zip";
        anchor.setText(lt(fileName));
        anchor.setResource(new WFileResource("application/zip", exportFile.getAbsolutePath()));
        anchor.resource().suggestFileName(fileName);
	}
	
	private void exportXml(Dataset ds) throws FileNotFoundException {
        exportFile = RegaDBMain.getApp().createTempFile(ds.getDescription() + "_export", "xml");
        FileOutputStream fout = new FileOutputStream(exportFile);
        PatientXMLOutputStream xmlout = new PatientXMLOutputStream(fout);
        
        ExportPatient<Patient> exportPatient = new ExportPatient<Patient>(RegaDBMain.getApp().getLogin(),ds.getDescription(),xmlout);
        exportPatient.run();
        
        table_.elementAt(0, 2).clear();
        
        String fileName = ds.getDescription() + "_export.xml";
        anchor.setText(lt(fileName));
        anchor.setResource(new WFileResource("text/txt", exportFile.getAbsolutePath()));
        anchor.resource().suggestFileName(fileName);
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
