package net.sf.regadb.io.db.irsicaixa;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.db2csv.DBToCsv;
import net.sf.regadb.io.db.util.db2csv.MysqlConnectionProvider;

import org.xml.sax.SAXException;

public class IrsicaixaAutoImport {
	public static void main(String [] args) {
		try {
	        System.setProperty("http.proxyHost", "www-proxy");
	        System.setProperty("http.proxyPort", "3128");
	        Login login = Login.authenticate(args[0], args[1]);
			IrsicaixaAutoImport auto = new IrsicaixaAutoImport("/home/plibin0/myWorkspace/regadb-io-db/src/net/sf/regadb/io/db/irsicaixa/mappings/", new MysqlConnectionProvider("virolab", args[2], args[3]), login, args[4]);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	public IrsicaixaAutoImport(String mappingPath, MysqlConnectionProvider connectionProvider, Login login, String dataset) throws IOException, WrongUidException, WrongPasswordException, DisabledUserException, SAXException {
		File csvDirectory = net.sf.regadb.util.file.FileUtils.createTempDir("csvDir", "irsicaixa");
		System.err.println(csvDirectory.getAbsolutePath());
		
		DBToCsv db2csv = new DBToCsv(connectionProvider);
		db2csv.createCsv(csvDirectory);
		
		ImportIrsicaixa importIrsicaixa = new ImportIrsicaixa(ConsoleLogger.getInstance(),
				csvDirectory.getAbsolutePath(),
				mappingPath);
		importIrsicaixa.run();
		
		String patientXmlFile = importIrsicaixa.getPatientsXmlPath();
		String viralIsolateXmlFile = importIrsicaixa.getViralIsolatesXmlPath();
		
		System.err.println(patientXmlFile);
		System.err.println(viralIsolateXmlFile);
		
		//AutoImport.removeOldDatabase();
		
		//AutoImport.importPatients(login, new File(patientXmlFile), dataset);
		
		//AutoImport.
	}
}
