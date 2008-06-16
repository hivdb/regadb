package net.sf.regadb.io.autoImport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
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

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AutoImport {
	public interface ViralIsolateComparator {
		public boolean equals(ViralIsolate oldVI, ViralIsolate newVI);
	}
	
	public static File exportViralIsolates(Login login, Dataset ds, ILogger logger) {
		List<Patient> patients  = login.createTransaction().getPatients(ds);
		
		Map<String, Patient> patientsMap = new HashMap<String, Patient>();
		for(Patient p : patients) {
			patientsMap.put(p.getPatientId(), p);
		}
		
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile("viral_isolates", "xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		IOUtils.exportNTXMLFromPatients(patientsMap, tmpFile.getAbsolutePath(), logger);
	
		return tmpFile;
	}
	
	public static void removeOldDatabase() {
        Connection c = HibernateUtil.getJDBCConnection();
        try {
            c.createStatement().execute("truncate patient cascade");
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public static void importPatients(Login login, File xmlInputFile, String dataset) throws WrongUidException, WrongPasswordException, DisabledUserException, FileNotFoundException, SAXException, IOException {
        ImportXML instance;
        instance = new ImportXML(login);
	    instance.importPatients(new InputSource(new FileReader(xmlInputFile)), dataset);
	    instance.login.closeSession();
	}
	
	public static void importFormerViralIsolates(Login login, File xmlInputFile, String dataset) throws WrongUidException, WrongPasswordException, DisabledUserException, FileNotFoundException, SAXException, IOException {
        ImportXML instance;
        instance = new ImportXML(login);
	    instance.importViralIsolates(new InputSource(new FileReader(xmlInputFile)), dataset);
	    instance.login.closeSession();
	}
	
	public static void importNewViralIsolate(Login login, File formerXml, File newXml, String dataset, ViralIsolateComparator comparator) throws SAXException, IOException {
		List<ViralIsolate> oldVis = getViralIsolateList(formerXml);
		List<ViralIsolate> newVis = getViralIsolateList(newXml);
		
		//only keep new viral isolates
		for(Iterator<ViralIsolate> i = newVis.iterator(); i.hasNext(); ) {
			for(ViralIsolate oldVI : oldVis) {
				ViralIsolate newVI = i.next();
				if(comparator.equals(oldVI, newVI)) {
					i.remove();
				}
			}
		}
		
		System.err.println("old and new");
		System.err.println(oldVis.size() + " : " + newVis.size());
	}
	
	private static List<ViralIsolate> getViralIsolateList(File xmlFile) throws SAXException, IOException {
		final List<ViralIsolate> vis = new ArrayList<ViralIsolate>();
		
		ImportFromXML instance = new ImportFromXML();
		instance.readViralIsolates(new InputSource(xmlFile.getAbsolutePath()), new ImportHandler<ViralIsolate>() {
			public void importObject(ViralIsolate object) {
				vis.add(object);
			}
		});
		
		return vis;
	}
}