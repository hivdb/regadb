package net.sf.regadb.io.db.irsicaixa;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.db2csv.DBToCsv;
import net.sf.regadb.io.db.util.db2csv.MysqlConnectionProvider;

public class IrsicaixaAutoImport {
	public static void main(String [] args) {
		try {
			IrsicaixaAutoImport auto = new IrsicaixaAutoImport("/home/plibin0/stuttgart_workspace/regadb-io-db/net/sf/regadb/io/db/irsicaixa/mappings/", new MysqlConnectionProvider("virolab", "root", "Eatnomeat001"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public IrsicaixaAutoImport(String mappingPath, MysqlConnectionProvider connectionProvider) throws IOException {
		File csvDirectory = net.sf.regadb.util.file.FileUtils.createTempDir("csvDir", "irsicaixa");
		
		DBToCsv db2csv = new DBToCsv(connectionProvider);
		db2csv.createCsv(csvDirectory);
		
		ImportIrsicaixa importIrsicaixa = new ImportIrsicaixa(ConsoleLogger.getInstance(),
				csvDirectory.getAbsolutePath(),
				mappingPath);
		
		
	}
}
