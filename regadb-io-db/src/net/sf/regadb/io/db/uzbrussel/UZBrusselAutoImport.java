package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.autoImport.AutoImport;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.util.xls.Xls2Csv;

import org.xml.sax.SAXException;

public class UZBrusselAutoImport {
	public static void main(String [] args) {
		int obligatoryArguments = 4;
		if(args.length<obligatoryArguments) {
			System.err.println("Usage baseDir mappingDir user password [proxyHost proxyPort]");
			System.exit(0);
		}
		
		String proxyHost = null;
		String proxyPort = null;
		if(args.length>obligatoryArguments) {
			proxyHost = args[obligatoryArguments];
			proxyPort = args[obligatoryArguments+1];
		}
		
		UZBrusselAutoImport ai = new UZBrusselAutoImport();
		
		try {
			ai.run(args[0], args[1], args[2], args[3], proxyHost, proxyPort);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(String baseDir, String mappingDir, String user, String password, String proxyHost, String proxyPort) throws WrongUidException, WrongPasswordException, DisabledUserException, FileNotFoundException, SAXException, IOException {
		File tempPatientsXmlFile = null;
		File tempViralIsolatesXmlFile = null;
		try {
			tempPatientsXmlFile = File.createTempFile("regadb-patients", "xml");
			tempViralIsolatesXmlFile = File.createTempFile("regadb-viralisolates", "xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		splitExcelFile(baseDir);
		
		ParseAll.exec(baseDir, mappingDir, proxyHost, proxyPort, tempPatientsXmlFile.getAbsolutePath(), tempViralIsolatesXmlFile.getAbsolutePath());
		
		Login login = Login.authenticate(user, password);
		AutoImport auto = new AutoImport(login, ConsoleLogger.getInstance(), "UZBrussel");
		
		System.err.println("Start exporting former viral isolates");
		File oldViralIsolates = auto.exportViralIsolates();
		System.err.println(oldViralIsolates);
		
		System.err.println("Start removing former database");
		auto.removeOldDatabase();
		
		System.err.println("Start importing new patients");
		auto.importPatients(tempPatientsXmlFile);
		
		System.err.println("Start importing former viralisolates");
		auto.importFormerViralIsolates(oldViralIsolates);
		
		System.err.println("Start importing new viralisolates");
		auto.importNewViralIsolate(oldViralIsolates, tempViralIsolatesXmlFile, new AutoImport.ViralIsolateComparator() {
			public boolean equals(ViralIsolate oldVI, ViralIsolate newVI) {
				if(oldVI.getSampleId().equals(newVI.getSampleId()))
					return true;
				else
					return false;
			}
		});
		
		login.closeSession();
		
		auto.cleanTempFiles();
		
		tempPatientsXmlFile.delete();
		tempViralIsolatesXmlFile.delete();
	}
	
    public static void splitExcelFile(String baseDir) {
    	File patHistoryCsv = new File(baseDir+"emd" + File.separatorChar + "pathistory.csv");
    	File patCodesCsv = new File(baseDir+"emd" + File.separatorChar + "patcodes.csv");
    	File patcodesToIgnoreCsv = new File(baseDir+"emd" + File.separatorChar + "patcodesToIgnore.csv");
    	File ignoreOldViralLoadCsv = new File(baseDir+"emd" + File.separatorChar + "ignoreOldViralLoad.csv");
    
		Xls2Csv xls = new Xls2Csv();
		
        Workbook wb = null;
        try {
            wb = Workbook.getWorkbook(new File(baseDir+File.separatorChar + "emd" + File.separatorChar + "patcodes.xls" ));
            xls.process(wb.getSheet(0),patHistoryCsv);
            xls.process(wb.getSheet(1),patCodesCsv);
            xls.process(wb.getSheet(2),patcodesToIgnoreCsv);
            xls.process(wb.getSheet(3),ignoreOldViralLoadCsv);
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
