package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.HibernateUtil;
import net.sf.regadb.io.importXML.impl.ImportXML;

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
}
