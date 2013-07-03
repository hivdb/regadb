package net.sf.regadb.io.db.test_results;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.xml.XMLTools;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;

public class ExportResistanceResults {
	public static void main(String[] args) throws WrongUidException, WrongPasswordException, DisabledUserException, IOException {
		if (args.length < 3) {
			System.err.println("export-resistance-results user password test-name output-file");
			System.exit(0);
		}
		
		String user = args[0];
		String password = args[1];
		String testName = args[2];
		String outputFile = args[3];
		
		RegaDBSettings.createInstance();

		Login login = Login.authenticate(user, password);
	
		Transaction t = login.createTransaction();
        
        Test test = t.getTest(testName);
        if (test == null) {
        	System.err.println("Test with name '" + testName + "' does not exist in the database");
        	System.exit(0);
        }
        
		Query q = 
				t.createQuery(
						"select new net.sf.regadb.db.Patient(patient, 1)" +
						"from PatientImpl as patient");
		q.setCacheable(false);
		ScrollableResults r = q.scroll();

		FileWriter fw = null;
		try {
			 fw = new FileWriter(new File(outputFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fw.write("<test-results test-name='" + testName + "'>\n");
		int counter = 0;
		while (r.next()) {
			Patient p = (Patient) r.get()[0];
			for (ViralIsolate vi : p.getViralIsolates()) {
				for (TestResult tr : vi.getTestResults()) {
					if (tr.getTest().getTestIi().equals(test.getTestIi())) {
						fw.write("<tr " + 
								"isolate_ii='" + vi.getViralIsolateIi() + "' " + 
								"drug='" + tr.getDrugGeneric().getGenericId() + "' " + 
								"value='" + tr.getValue() + "'" +
								"> \n");
						fw.write(XMLTools.base64Encoding(tr.getData()) + "\n");
						fw.write("</tr>\n");
					}
				}
			}

			if (counter == 100) {
				t.clearCache();
				counter = 0;
			}

			counter++;
		}
		fw.write("</test-results>");
		
		fw.close();
	}
}
