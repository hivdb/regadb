package net.sf.regadb.ui.form.impex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.export.PatientExporter;
import net.sf.regadb.io.exportCsv.FullCsvExport;
import net.sf.regadb.io.exportXML.ExportToXMLOutputStream.PatientXMLOutputStream;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WFileResource;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

public class ExportForm extends FormWidget {
	private FormTable table_;
	private ComboBox<Dataset> datasets;
	private WComboBox format;
	private WAnchor anchor;
	private File exportFile;
	private WCheckBox exportMutations;
	
	private WContainerWidget errorsContainer;
	
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
		format.addItem("XML");
		format.addItem("CSV");
		table_.addLineToTable(formatL, format);
		
		exportMutations = new WCheckBox();
		final int i = table_.addLineToTable(new Label(tr("form.impex.export.mutations")),exportMutations);
		table_.getRowAt(i).hide();
		
		format.changed().addListener(this, new Signal.Listener()
        {
			public void trigger()
			{
				if(format.getCurrentText().getValue().equals("CSV")){
					table_.getRowAt(i).show();
				}
				else{
					table_.getRowAt(i).hide();
				}
			}
        });
		
		WPushButton export = new WPushButton(tr("form.impex.export.title"));
		Label exportL = new Label(tr("form.impex.export.title"));
		anchor = new WAnchor();
		anchor.setStyleClass("link");
		anchor.setHidden(true);
		table_.addLineToTable(exportL, export, anchor);
		
		
		export.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
			    try{
			    	anchor.setHidden(true);
    			    Dataset ds = datasets.currentValue();
    			    deleteExportFile();
    			    if(format.getCurrentText().getValue().equals("XML")) {
    			    	exportXml(ds);
    			    } else if (format.getCurrentText().getValue().equals("CSV")){
    			    	exportCsv(ds);
    			    }
    			    anchor.setHidden(false);
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
			}
		});
		
		errorsContainer = table_.getElementAt(table_.getRowCount(), 0);
		
		addControlButtons();
    }
	
	private void exportCsv(Dataset ds) {
		errorsContainer.clear();
		
		exportFile = RegaDBMain.getApp().createTempFile(ds.getDescription() + "_export", "zip");
		
		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
		
		List<String> resistanceTestsDrugs = new ArrayList<String>();
        for(Test test : t.getTests()) {
            if(test.getTestType().getDescription().equals(StandardObjects.getGssDescription()) ) {
                for(DrugClass dc : t.getDrugClassesSortedOnResistanceRanking()) {
                	for(DrugGeneric dg : t.getDrugGenericSortedOnResistanceRanking(dc)) {
                		resistanceTestsDrugs.add(test.getDescription() + "_" + dg.getGenericId()+"_"+test.getTestType().getGenome().getOrganismName());
                	}
                }
            }
        }
        
        List<String> errors = null;
		try {
			FullCsvExport fullCsvExport = new FullCsvExport(t.getMaxAmountOfSequences(), t.getAttributes(), resistanceTestsDrugs, exportFile, exportMutations.isChecked());
	        PatientExporter<Patient> csvExport = new PatientExporter<Patient>(RegaDBMain.getApp().getLogin(), ds.getDescription(), fullCsvExport);
	        csvExport.run();
	        errors = csvExport.getErrors();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        table_.getElementAt(0, 2).clear();
        
        String fileName = ds.getDescription() + "_csv_export.zip";
        anchor.setText(fileName);
        anchor.setResource(new WFileResource("application/zip", exportFile.getAbsolutePath(), null));
        anchor.getResource().suggestFileName(fileName);
        
        showErrors(errors);
	}
	
	private void exportXml(Dataset ds) throws FileNotFoundException {
		errorsContainer.clear();
		
        exportFile = RegaDBMain.getApp().createTempFile(ds.getDescription() + "_export", "xml");
        FileOutputStream fout = new FileOutputStream(exportFile);
        PatientXMLOutputStream xmlout = new PatientXMLOutputStream(fout);
        
        PatientExporter<Patient> exportPatient = new PatientExporter<Patient>(RegaDBMain.getApp().getLogin(),ds.getDescription(),xmlout);
        exportPatient.run();
        List<String> errors = exportPatient.getErrors();
        
        table_.getElementAt(0, 2).clear();
        
        String fileName = ds.getDescription() + "_export.xml";
        anchor.setText(fileName);
        WFileResource wfr = new WFileResource("text/txt", exportFile.getAbsolutePath(), null);
        anchor.setResource(wfr);
        wfr.suggestFileName(fileName);
        
        showErrors(errors);
	}
	
	private void showErrors(List<String> errors) {
        if (errors != null && errors.size() != 0) {
        	for (String error : errors)
        		errorsContainer.addWidget(new WText(error));
        }
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

	@Override
	public void redirectAfterSave() {
	}

	@Override
	public void redirectAfterCancel() {
	}
}
