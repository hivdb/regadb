package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.HibernateUtil;
import net.sf.regadb.io.importXML.impl.ImportXML;
import net.sf.regadb.util.xls.Xls2Csv;

public class UZBrusselAutoImport {
	public static void main(String [] args) {
		//"/home/plibin0/import/jette/import/cd/080321/"
		//String mappingBasePath = "/home/plibin0/myWorkspace/regadb-io-db/src/net/sf/regadb/io/db/uzbrussel/mappings";
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
		
		File tmpXmlFile = null;
		try {
			tmpXmlFile = File.createTempFile("regadb-patients", "xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		splitExcelFile(args[0]);
		
		ParseAll.exec(args[0], args[1], proxyHost, proxyPort, tmpXmlFile.getAbsolutePath());
		
        Connection c = HibernateUtil.getJDBCConnection();
        try {
            c.createStatement().execute("truncate patient cascade");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
		try {
			ImportXML instance = new ImportXML(args[2], args[3]);
	        instance.importPatients(new InputSource(new FileReader(tmpXmlFile)), "UZBrussel");
	        instance.login.closeSession();
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
