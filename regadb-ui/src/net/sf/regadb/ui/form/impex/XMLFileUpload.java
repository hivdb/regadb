package net.sf.regadb.ui.form.impex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import net.sf.regadb.io.importXML.impl.ImportXML;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFileUpload;
import net.sf.witty.wt.WResource;
import net.sf.witty.wt.i8n.WMessage;

import org.xml.sax.InputSource;

public class XMLFileUpload extends WFileUpload {
	private String dataset_;
	private UploadStatus status = UploadStatus.UPLOADING;
	private boolean harakiri = false;
	private File logf;
	private WCheckBox wc_;
	
	public XMLFileUpload(WContainerWidget parent) {
		super(parent);
		
		super.uploaded.addListener(new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				status = UploadStatus.PROCESSING;
				
				if ( emptyFileName() ) {
					harakiri = true;
					status = UploadStatus.FAILED;
				} else {
					PrintStream ps = System.out;
					
					try {
						ImportXML instance = new ImportXML("admin", "admin");
						File xmlFile = new File(spoolFileName());
						
						logf = RegaDBMain.getApp().createTempFile(clientFileName().replace('.', '_'), "log");
						ps = new PrintStream(logf);
						instance.setPrintStream(ps);
						
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFile)));
						
						br.readLine();
						String line = br.readLine();
						
						if ( line == null ) {
							status = UploadStatus.FAILED;
							ps.println(WResource.tr("form.impex.import.progress.status.invalid").value());
						} else if (line.equals("<patients>")) {
							instance.importPatients(new InputSource(new FileReader(xmlFile)), dataset_);
						} else if (line.equals("<viralIsolates>")) {
							instance.importViralIsolates(new InputSource(new FileReader(xmlFile)), dataset_);
						} else {
							status = UploadStatus.FAILED;
							ps.println(WResource.tr("form.impex.import.progress.status.invalid").value());
						}
						
						status = UploadStatus.SUCCEEDED;
					} catch (Exception e) {
						status = UploadStatus.FAILED;
						e.printStackTrace(ps);
					}
				}
			}
		});
	}
	
	@Override
	public String clientFileName() {
		String fn = super.clientFileName();
		return (fn==null)?"unknown":fn;
	}
	
	public File getLog() {
		return logf;
	}
	
	public void setDatasetName(String dataset) {
		dataset_ = dataset;
	}
	public WMessage getDatasetName() {
		return new WMessage(dataset_, true);
	}
	
	public void setCheckbox(WCheckBox wc) {
		wc_ = wc;
	}
	
	public boolean isChecked() {
		return wc_.isChecked();
	}
	
	public boolean harakiri() {
		return harakiri;
	}
	
	public WMessage getStatus() {
		if ( isUploading() ) {
			return WResource.tr("form.impex.import.progress.status.uploading");
		} else if ( uploadedSucces() ) {
			return WResource.tr("form.impex.import.progress.status.done");
		} else {
			return WResource.tr("form.impex.import.progress.status.failed");
		}
	}
	
	public UploadStatus getUploadStatus() { return status; }
	public boolean isUploaded() { return status != UploadStatus.UPLOADING; }
	public boolean isUploading() { return status == UploadStatus.UPLOADING; }
	public boolean uploadedSucces() { return status == UploadStatus.SUCCEEDED; }
	public boolean uploadedFailed() { return status == UploadStatus.FAILED; }
}
