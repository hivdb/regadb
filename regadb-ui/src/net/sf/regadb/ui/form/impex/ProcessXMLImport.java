package net.sf.regadb.ui.form.impex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.importXML.impl.ImportXML;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WFileUpload;
import net.sf.witty.wt.WResource;
import net.sf.witty.wt.i8n.WMessage;

import org.xml.sax.InputSource;

public class ProcessXMLImport extends Thread {
	private File xmlFile;
	private String dataset_;
	private String clientFileName;
	private File logFile;
	private WCheckBox wc_;
	private UploadStatus status = UploadStatus.PROCESSING;
	private String uid;
	private Login login_;
	
	public ProcessXMLImport(Login login, WFileUpload fileUpload, String dataset) {
		clientFileName = fileUpload.clientFileName();
		xmlFile = new File(fileUpload.spoolFileName());
		dataset_ = dataset;
		logFile = RegaDBMain.getApp().createTempFile(clientFileName.replace('.', '_'), "log");
		login_ = login;
		uid = login.getUid();
	}
	
	public void run() {
		PrintStream ps = System.out;
		
		try {
			ImportXML instance = new ImportXML(login_.copyLogin());
			
			ps = new PrintStream(logFile);
			instance.setPrintStream(ps);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFile)));
			
			br.readLine();
			String line = br.readLine().trim();
			
			if ( line == null ) {
				ps.println(WResource.tr("form.impex.import.progress.status.invalid").value());
				status = UploadStatus.FAILED;
			} else if (line.contains("<patients>")) {
				instance.importPatients(new InputSource(new FileReader(xmlFile)), dataset_);
				status = UploadStatus.SUCCEEDED;
			} else if (line.contains("<viralIsolates>")) {
				instance.importViralIsolates(new InputSource(new FileReader(xmlFile)), dataset_);
				status = UploadStatus.SUCCEEDED;
			} else {
				ps.println(WResource.tr("form.impex.import.progress.status.invalid").value());
				status = UploadStatus.FAILED;
			}
			
			ps.close();
		} catch (Exception e) {
			e.printStackTrace(ps);
			status = UploadStatus.FAILED;
		}
	}
	
	public File getLogFile() {
		return logFile;
	}
	
	public WMessage getDatasetName() {
		return new WMessage(dataset_, true);
	}
	
	public String clientFileName() {
		return clientFileName;
	}
	
	public String getUid() {
		return uid;
	}
	
	public void setCheckbox(WCheckBox wc) {
		wc_ = wc;
	}
	
	public boolean isChecked() {
		return wc_ == null ? false : wc_.isChecked();
	}
	
	public UploadStatus getStatus(){
		return status;
	}
	public WMessage getStatusName() {
		String key = "form.impex.import.progress.status.";
		if ( status == UploadStatus.PROCESSING ) key += "processing";
		else if ( status == UploadStatus.SUCCEEDED ) key += "done";
		else if ( status == UploadStatus.FAILED ) key += "failed";
		return WResource.tr(key);
	}
}
