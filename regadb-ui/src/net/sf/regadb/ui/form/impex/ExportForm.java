package net.sf.regadb.ui.form.impex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.export.PatientExporter;
import net.sf.regadb.io.export.hicdep.HicdepCsvExporter;
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
import net.sf.regadb.util.process.StreamReaderThread;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.AnchorTarget;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WResource;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.servlet.WebRequest;
import eu.webtoolkit.jwt.servlet.WebResponse;
import eu.webtoolkit.jwt.utils.StreamUtils;

public class ExportForm extends FormWidget {
	private FormTable table_;
	private ComboBox<Dataset> datasets;
	private WComboBox format;
	private WAnchor anchor;
	private WCheckBox exportMutations;

	private static String CSV = "CSV";
	private static String XML = "XML";
	private static String HICDEP = "HICDEP";
	private static String DATABASE = "Database";
	
	public ExportForm(WString formName, InteractionState interactionState) {
		super(formName, interactionState);
		init();
	}
	
	private boolean disableDatasets(String type) {
		return type.equals(DATABASE) || type.equals(HICDEP);
	}
	
	public void init() {
		table_ = new FormTable(this);
		
		Label formatL = new Label(tr("form.impex.export.format"));
		format = new WComboBox();
		format.addItem(XML);
		format.addItem(HICDEP);
		format.addItem(CSV);
		if (RegaDBSettings.getInstance().getInstituteConfig().getDatabaseBackupScript() != null) {
			format.addItem(DATABASE);	
		}
		table_.addLineToTable(formatL, format);
		
		Label datasetsL = new Label(tr("form.impex.export.dataset"));
		datasets = new ComboBox<Dataset>(InteractionState.Editing, this);
		
		fillData();
		table_.addLineToTable(datasetsL, datasets);
		
		exportMutations = new WCheckBox();
		final int i = table_.addLineToTable(new Label(tr("form.impex.export.mutations")),exportMutations);
		table_.getRowAt(i).hide();
		
		table_.addLineToTable(new Label(tr("form.impex.export.download")), anchor = new WAnchor());
		anchor.setText(tr("form.impex.export.download"));
		anchor.setTarget(AnchorTarget.TargetNewWindow);
		anchor.setResource(new WResource() {
			protected void handleRequest(WebRequest request, WebResponse response) throws IOException {
				File f = File.createTempFile("regadb", "export-form");
				
				try {
					String format = ExportForm.this.format.getCurrentText().getValue();
					String mimeType = mimetType(format);
					
					List<String> errors = new ArrayList<String>();
					generateFile(format, f, errors);
					
					if (errors.isEmpty()) {
						handleFileRequest(response, f, mimeType);
					} else {
						response.setContentType("text/plain");
						response.getOutputStream().write(tr("form.impex.export.error").toString().getBytes());
						for (String error : errors)
							response.getOutputStream().write(error.getBytes());
					}
				} finally {
					f.delete();
				}
			}
		});
		
		format.changed().addListener(this, new Signal.Listener()
        {
			public void trigger()
			{
				String format = ExportForm.this.format.getCurrentText().getValue();
				if(CSV.equals(format)){
					table_.getRowAt(i).show();
				}
				else{
					table_.getRowAt(i).hide();
				}
				
				datasets.setDisabled(disableDatasets(format));
				
				String fileName = ExportForm.this.fileName(format);
				ExportForm.this.anchor.getResource().suggestFileName(fileName);
			}
        });
		
		//TODO 
		//temporary fix, 
		//without this, the value returned by getCurrentText is not up to date
		//I hope this workaround will not be necessary any more after a JWt upgrade
		datasets.addComboChangeListener(new Signal.Listener()
        {
			public void trigger()
			{
			}
        });
		
		addControlButtons();
    }
	
	private void handleFileRequest(WebResponse response, File f, String mimeType) {
		response.setContentType(mimeType);

		try {
			FileInputStream fis = new FileInputStream(f);
			try {
				StreamUtils.copy(fis, response.getOutputStream());
				response.getOutputStream().flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				StreamUtils.closeQuietly(fis);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void generateFile(String format, File outputFile, List<String> errors) {
		if (DATABASE.equals(format)) {
			exportDatabaseZip(outputFile);
		} else if (HICDEP.equals(format)) {
			exportHicdep(outputFile, errors);
		} else {
			Dataset ds = datasets.currentValue();
			if (XML.equals(format)) {
				exportXml(ds, outputFile, errors);
			} else if (CSV.equals(format)) {
				boolean mutations = exportMutations.isChecked();
				exportCsv(ds, outputFile, errors, mutations);
			}
		}
	}
	
	private String mimetType(String format) {
		if (DATABASE.equals(format)) {
			return "application/zip";
		} else if (XML.equals(format)) {
			return "application/xml";
		} else if (CSV.equals(format)) {
			return "application/zip";
		} else if (HICDEP.equals(format)) {
			return "application/zip";
		} else {
			return null;
		}
	}
	
	private String fileName(String format) {
		if (DATABASE.equals(format)) {
			DateFormat df = new SimpleDateFormat("MM-dd-yyyy_HH-mm");
			return "database_dump_" + df.format(new Date()) + ".zip";
		} else {
			Dataset ds = datasets.currentValue();
			if (XML.equals(format)) {
				return ds.getDescription() + "_export.xml";
			} else if (CSV.equals(format)) {
				return ds.getDescription() + "_csv_export.zip";
			} else if (HICDEP.equals(format)) {
				return ds.getDescription() + "_hicdep_export.zip";
			}  else {
				return null;
			} 
		}
	}
	
	private void exportCsv(Dataset ds, File exportFile, List<String> errors, boolean exportMutations) {
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
        
		try {
			FullCsvExport fullCsvExport = new FullCsvExport(t.getMaxAmountOfSequences(), t.getAttributes(), resistanceTestsDrugs, exportFile, exportMutations);
	        PatientExporter<Patient> csvExport = new PatientExporter<Patient>(RegaDBMain.getApp().getLogin(), ds.getDescription(), fullCsvExport);
	        csvExport.run();
	        List<String> _errors = csvExport.getErrors();
	        if (_errors != null)
	        	errors.addAll(_errors);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void exportDatabaseZip(File outputFile) {
		String script = RegaDBSettings.getInstance().getInstituteConfig().getDatabaseBackupScript();
		String userName = RegaDBSettings.getInstance().getHibernateConfig().getUsername();
		String password = RegaDBSettings.getInstance().getHibernateConfig().getPassword();

		File tmp = null;
        try {        	
        	tmp = File.createTempFile("database-dump", ".sql");
        	
    		Process ps = new ProcessBuilder(script, userName, password, tmp.getAbsolutePath()).start();
    		
    		StreamReaderThread stdout = new StreamReaderThread(ps.getInputStream(), System.out, "stdout: ");
    		stdout.start();
    		
    		StreamReaderThread stderr = new StreamReaderThread(ps.getErrorStream(), System.err, "stderr: ");
    		stderr.start();
    		
    		ps.waitFor();
    		
			DateFormat df = new SimpleDateFormat("MM-dd-yyyy_HH-mm");
			String name = "database_dump_" + df.format(new Date()) + ".sql";
			
    		zip(outputFile, tmp, name);
        } catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			tmp.delete();
		}
	}
	
	private void zip(File target, File source, String sourceName) {
		byte[] buffer = new byte[1024];
		 
    	try {
    		FileOutputStream fos = new FileOutputStream(target);
    		ZipOutputStream zos = new ZipOutputStream(fos);
    		ZipEntry ze= new ZipEntry(sourceName);
    		zos.putNextEntry(ze);
    		FileInputStream in = new FileInputStream(source);
 
    		int len;
    		while ((len = in.read(buffer)) > 0) {
    			zos.write(buffer, 0, len);
    		}
 
    		in.close();
    		zos.closeEntry();
 
    		zos.close();
    	} catch(IOException ex){
    	   ex.printStackTrace();
    	}
	}
	
	private void exportXml(Dataset ds, File exportFile, List<String> errors) {
		try {
			FileOutputStream fout = new FileOutputStream(exportFile);
	        PatientXMLOutputStream xmlout = new PatientXMLOutputStream(fout);
	        
	        PatientExporter<Patient> exportPatient = new PatientExporter<Patient>(RegaDBMain.getApp().getLogin(),ds.getDescription(),xmlout);
	        exportPatient.run();
	        errors.addAll(exportPatient.getErrors());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void exportHicdep(File exportFile, List<String> errors){        
        HicdepCsvExporter export = new HicdepCsvExporter(RegaDBMain.getApp().getLogin(), exportFile);
        export.export();
        try {
			export.close();
		} catch (IOException e) {
			errors.add(e.getMessage());
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
		return null;
	}

	@Override
	public void redirectAfterSave() {
	}

	@Override
	public void redirectAfterCancel() {
	}
}
