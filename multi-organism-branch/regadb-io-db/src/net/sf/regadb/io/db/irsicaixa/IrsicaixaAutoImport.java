package net.sf.regadb.io.db.irsicaixa;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.autoImport.AutoImport;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.db2csv.DBToCsv;
import net.sf.regadb.io.db.util.db2csv.MysqlConnectionProvider;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

public class IrsicaixaAutoImport {
	public static void main(String [] args) {
		try {
			
			if(args.length!=6) 
	        {
	            System.err.println("Usage: IrsicaixaAutoImport [regadb user] [regadb pw] [mappingBasePath] [mysql user] [mysql pw] [dataset]");
	            System.exit(0);
	        }
			
	        //System.setProperty("http.proxyHost", "www-proxy");
	        //System.setProperty("http.proxyPort", "3128");
	        
	        Login login = Login.authenticate(args[0], args[1]);
			IrsicaixaAutoImport auto = new IrsicaixaAutoImport(args[2], new MysqlConnectionProvider("virolab", args[3], args[4]), login, args[5]);
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
		
		AutoImport auto = new AutoImport(login, ConsoleLogger.getInstance(), dataset);
		
		ImportIrsicaixa importIrsicaixa = new ImportIrsicaixa(ConsoleLogger.getInstance(),
				csvDirectory.getAbsolutePath(),
				mappingPath);
		importIrsicaixa.run();
		
		String patientXmlFile = importIrsicaixa.getPatientsXmlPath();
		String viralIsolateXmlFile = importIrsicaixa.getViralIsolatesXmlPath();
		
		System.err.println(patientXmlFile);
		System.err.println(viralIsolateXmlFile);
		
		System.err.println("Start exporting former viral isolates");
		File oldViralIsolates = auto.exportViralIsolates();
		System.err.println(oldViralIsolates);
		
		System.err.println("Start removing former database");
		auto.removeOldDatabase();
		
		System.err.println("Start importing new patients");
		auto.importPatients(new File(patientXmlFile));
		
		System.err.println("Start importing former viralisolates");
		auto.importFormerViralIsolates(oldViralIsolates);
		
		System.err.println("Start importing new viralisolates");
		auto.importNewViralIsolate(oldViralIsolates, new File(viralIsolateXmlFile), new AutoImport.ViralIsolateComparator() {
			public boolean equals(ViralIsolate oldVI, ViralIsolate newVI) {
				if(oldVI.getSampleId().equals(newVI.getSampleId()))
					return true;
				else
					return false;
			}
		});
		
		login.closeSession();
		
		FileUtils.deleteDirectory(csvDirectory);
		auto.cleanTempFiles();
	}
}
