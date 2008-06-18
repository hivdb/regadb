package net.sf.regadb.io.db.irsicaixa;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.db2csv.DBToCsv;
import net.sf.regadb.io.db.util.db2csv.MysqlConnectionProvider;

public class IrsicaixaAutoImport {
	public static void main(String [] args) {
		try {
	        System.setProperty("http.proxyHost", "www-proxy");
	        System.setProperty("http.proxyPort", "3128");
			IrsicaixaAutoImport auto = new IrsicaixaAutoImport("/home/plibin0/myWorkspace/regadb-io-db/src/net/sf/regadb/io/db/irsicaixa/mappings/", new MysqlConnectionProvider("virolab", "root", "Eatnomeat001"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public IrsicaixaAutoImport(String mappingPath, MysqlConnectionProvider connectionProvider) throws IOException {
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
		
	}
}
