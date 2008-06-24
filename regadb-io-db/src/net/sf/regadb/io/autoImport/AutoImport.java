package net.sf.regadb.io.autoImport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.HibernateUtil;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.importXML.ImportHandler;
import net.sf.regadb.io.importXML.impl.ImportXML;
import net.sf.regadb.io.util.ILogger;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.service.ioAssist.IOAssist;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AutoImport {
	public interface ViralIsolateComparator {
		public boolean equals(ViralIsolate oldVI, ViralIsolate newVI);
	}
	
	private Login login_;
	private ILogger logger_;
	private String dataset_;
	private List<File> filesToRemove = new ArrayList<File>();
	
	public AutoImport(Login login, ILogger logger, String dataset) {
		login_ = login;
		logger_ = logger;
		dataset_ = dataset;
	}
	
	public File exportViralIsolates() {
		Transaction t = login_.createTransaction();
		
		Dataset ds = t.getDataset(dataset_);
		List<Patient> patients  = t.getPatients(ds);
		
		Map<String, Patient> patientsMap = new HashMap<String, Patient>();
		for(Patient p : patients) {
			patientsMap.put(p.getPatientId(), p);
		}
		
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile("viral_isolates", "xml");
			filesToRemove.add(tmpFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		IOUtils.exportNTXMLFromPatients(patientsMap, tmpFile.getAbsolutePath(), logger_);
	
		t.clearCache();
		t.commit();
		
		return tmpFile;
	}
	
	public void removeOldDatabase() {
        Connection c = HibernateUtil.getJDBCConnection();
        try {
            c.createStatement().execute("truncate patient cascade");
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public void importPatients(File xmlInputFile) throws WrongUidException, WrongPasswordException, DisabledUserException, FileNotFoundException, SAXException, IOException {
        ImportXML instance;
        instance = new ImportXML(login_);
	    instance.importPatients(new InputSource(new FileReader(xmlInputFile)), dataset_);
	}
	
	public void importFormerViralIsolates(File xmlInputFile) throws WrongUidException, WrongPasswordException, DisabledUserException, FileNotFoundException, SAXException, IOException {
        ImportXML instance;
        instance = new ImportXML(login_);
	    instance.importViralIsolates(new InputSource(new FileReader(xmlInputFile)), dataset_);
	}
	
	public void importNewViralIsolate(File formerXml, File newXml, ViralIsolateComparator comparator) throws SAXException, IOException, WrongUidException, WrongPasswordException, DisabledUserException {
		List<ViralIsolate> oldVis = getViralIsolateList(formerXml);
		List<ViralIsolate> newVis = getViralIsolateList(newXml);

		List<ViralIsolate> diffVis = new ArrayList<ViralIsolate>();
		
		//only keep new viral isolates
		for(ViralIsolate newVI : newVis) {
			boolean found = false;
			for(ViralIsolate oldVI : oldVis) {
				if(comparator.equals(oldVI, newVI)) {
					found = true;
					break;
				}
			}
			if(!found){
				diffVis.add(newVI);
			}
		}
		
		System.err.println("old - new - diff");
		System.err.println(oldVis.size() + " - " + newVis.size() + " - " + diffVis.size());
		
		if(diffVis.size()>0) {
			File diffViFile = File.createTempFile("diffViFile", "xml");
			filesToRemove.add(diffViFile);
			Map<String, ViralIsolate> diffViMap = new HashMap<String, ViralIsolate>();
			for(ViralIsolate vi : diffVis) {
				diffViMap.put(vi.getSampleId(), vi);
			}
			IOUtils.exportNTXML(diffViMap, diffViFile.getAbsolutePath(), logger_);
			
			File diffViProcessedFile = File.createTempFile("diffViFileProcessed", "xml");
			filesToRemove.add(diffViProcessedFile);
			
			IOAssist.run(diffViFile, diffViProcessedFile, null);
			
	        ImportXML instance;
	        instance = new ImportXML(login_);
		    instance.importViralIsolates(new InputSource(new FileReader(diffViProcessedFile)), dataset_);
		}
	}
	
	private List<ViralIsolate> getViralIsolateList(File xmlFile) throws SAXException, IOException {
		final List<ViralIsolate> vis = new ArrayList<ViralIsolate>();
		
		Transaction t = login_.createTransaction();
		
		ImportFromXML instance = new ImportFromXML();
		instance.loadDatabaseObjects(t);
		instance.readViralIsolates(new InputSource(xmlFile.getAbsolutePath()), new ImportHandler<ViralIsolate>() {
			public void importObject(ViralIsolate object) {
				vis.add(object);
			}
		});
		t.commit();
		
		return vis;
	}
	
	public void cleanTempFiles() {
		for(File f : filesToRemove) {
			f.delete();
		}
	}
}