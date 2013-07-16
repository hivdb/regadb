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
import org.hibernate.Query;
import org.hibernate.ScrollableResults;

import com.sun.org.apache.xml.internal.security.utils.Base64;


public class ExportResistanceResults {
	public static void main(String[] args) throws WrongUidException, WrongPasswordException, DisabledUserException, IOException {
		if (args.length < 5) {
			System.err.println("export-resistance-results user password test-org test-name output-file");
			System.exit(0);
		}
		
		String user = args[0];
		String password = args[1];
		String testOrganism = args[2];
		String testName = args[3];
		String outputFile = args[4];
		
		RegaDBSettings.createInstance();

		Login login = Login.authenticate(user, password);
	
		Transaction t = login.createTransaction();
		
		String testTypeDescription = "Genotypic Susceptibility Score (GSS)";
        
        Test test = t.getTest(testName, testTypeDescription, testOrganism);
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
		
		int counter = 0;
		while (r.next()) {
			Patient p = (Patient) r.get()[0];
			for (ViralIsolate vi : p.getViralIsolates()) {
				for (TestResult tr : vi.getTestResults()) {
					if (tr.getTest().getTestIi().equals(test.getTestIi())) {
						String drugQuery = 
								"select generic_ii " +
								"from regadbschema.drug_generic " +
								"where generic_id = ':generic_id'";
						drugQuery = drugQuery.replaceAll(":generic_id", tr.getDrugGeneric().getGenericId());
						
						String testQuery = 
								"select test_ii from regadbschema.test " +
								"where " +
								"	description = ':test_name' and " +
								"	test_type_ii = " +
								"		(select test_type_ii from regadbschema.test_type " +
								"			where " +
								"			description=':test_type_description' and " +
								"			genome_ii = (select genome_ii from regadbschema.genome where organism_name = ':organism_name'))";
						testQuery = testQuery.replaceAll(":test_name", testName);
						testQuery = testQuery.replaceAll(":test_type_description", testTypeDescription);
						testQuery = testQuery.replaceAll(":organism_name", testOrganism);
						
						String insert = 
								"INSERT INTO regadbschema.test_result " +
								"(test_ii, version, patient_ii, viral_isolate_ii, generic_ii, value, test_date, data) " +
								"VALUES " +
								"((:test_query), 0, :patient_ii, :isolate_ii, (:drug_query), :value, now(), decode(':data_base64','base64'))";
						insert = insert.replaceAll(":test_query", testQuery);
						insert = insert.replaceAll(":drug_query", drugQuery);
						insert = insert.replaceAll(":patient_ii", p.getPatientIi().toString());
						insert = insert.replaceAll(":isolate_ii", tr.getViralIsolate().getViralIsolateIi()+"");
						insert = insert.replaceAll(":value", tr.getValue());
						insert = insert.replaceAll(":data_base64", Base64.encode(tr.getData(), 0));
						
						fw.write(insert + ";\n");
					}
				}
			}

			if (counter == 100) {
				t.clearCache();
				counter = 0;
			}

			counter++;
		}
		
		fw.close();
	}
}
