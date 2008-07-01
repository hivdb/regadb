package net.sf.regadb.ui.form.impex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.WFileResource;
import net.sf.witty.wt.WLabel;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class ExportForm extends FormWidget {
	private WTable table_;
	private WComboBox datasets;
	private File exportFile;
	
	public ExportForm(WMessage formName, InteractionState interactionState) {
		super(formName, interactionState);
		init();
	}
	
	public void init()
    {
		table_ = new WTable();
		table_.setStyleClass("spacyTable");
		addWidget(table_);
		
		new WLabel(tr("form.impex.export.dataset"), table_.elementAt(0, 0)).setStyleClass("form-label");;
		datasets = new WComboBox(table_.elementAt(0, 1));
		
		WPushButton export = new WPushButton(tr("form.impex.export.title"), table_.elementAt(0, 1));
		export.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				ExportToXML l = new ExportToXML();
		        Element root = new Element("patients");
		        
		        Dataset ds = ((DataComboMessage<Dataset>)datasets.currentText()).getValue();
				
				Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
		        for ( Patient p : t.getPatients(ds) ) {
		            Element patient = new Element("patients-el");
		            root.addContent(patient);
		            l.writePatient(p, patient);            
		        }
		        
				Document doc = new Document(root);
	 	        XMLOutputter outputter = new XMLOutputter();
	 	        outputter.setFormat(Format.getPrettyFormat());
	 	        
                deleteExportFile();
				exportFile = RegaDBMain.getApp().createTempFile(ds.getDescription() + "_export", "xml");
				
	 	        try {
	 	        	FileWriter writer = new FileWriter(exportFile);
	 	        	outputter.output(doc, writer);
	 	 	        writer.flush();
	 	 	        writer.close();
	 	        } catch ( IOException e ) {
	 	        	e.printStackTrace();
	 	        }
                
                table_.elementAt(0, 2).clear();
                
                new WAnchor(new WFileResource("text/txt", exportFile.getAbsolutePath()),
                		new WMessage(ds.getDescription() + "_export.xml", true),
                		table_.elementAt(0, 2)).setStyleClass("link");
			}
		});
		
		fillData();
		
		addControlButtons();
    }
	
	public void fillData() {
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		// Fill in dataset combo box
		datasets.clear();
        for(Dataset ds : t.getDatasets())
        {
        	datasets.addItem(new DataComboMessage<Dataset>(ds, ds.getDescription()));
        }
        datasets.sort();
	}
	
	private void deleteExportFile() {
		if ( exportFile != null ) exportFile.delete();
	}
	
	@Override
	public void cancel() {}
	
	@Override
	public WMessage deleteObject() {return null;}
	
	@Override
	public void redirectAfterDelete() {}
	
	@Override
	public void saveData() {}
	
	@Override
	public WMessage leaveForm() {
		deleteExportFile();
		return null;
	}
}
