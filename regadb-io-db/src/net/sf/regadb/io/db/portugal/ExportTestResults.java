package net.sf.regadb.io.db.portugal;

import java.util.List;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

public class ExportTestResults {

	public static void main(String[] args) throws WrongUidException, WrongPasswordException, DisabledUserException{
		Arguments as = new Arguments();
		PositionalArgument user = as.addPositionalArgument("user", true);
		PositionalArgument pass = as.addPositionalArgument("pass", true);
		
		PositionalArgument aTest = as.addPositionalArgument("test", true);
		PositionalArgument aTestType = as.addPositionalArgument("test-type", true);
		PositionalArgument aGenome = as.addPositionalArgument("genome", false);
		
		ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		Login login = Login.authenticate(user.getValue(), pass.getValue());
		Transaction t = login.createTransaction();
		
		Test test = null;
		if(!aGenome.isSet())
			test = t.getTest(aTest.getValue(), aTestType.getValue());
		else
			test = t.getTest(aTest.getValue(), aTestType.getValue(), aGenome.getValue());
		
		if(test != null){
			ExportTestResults exp = new ExportTestResults();
			exp.export(t, test, aTest.getValue(), aTestType.getValue(), aGenome.getValue());
		} else
			System.err.println("test not found");
		
		t.commit();
	}
	
	
	@SuppressWarnings("unchecked")
	public void export(Transaction t, Test test, String testDescr, String testTypeDescr, String genome){
		Query q = t.createQuery("select p.patientId, tr from TestResult tr left outer join tr.patient p where tr.test = :test");
		q.setParameter("test", test);
		
		for(Object[] o : (List<Object[]>)q.list()){
			TestResult tr = (TestResult)o[1];
			DrugGeneric dg = tr.getDrugGeneric();
			ViralIsolate v = tr.getViralIsolate();
			NtSequence n = tr.getNtSequence();
			if(v == null && n != null)
				v = n.getViralIsolate();
			
			export(
					(String)o[0],
					testDescr,
					testTypeDescr,
					genome,
					dg == null ? null : dg.getGenericId(),
					v.getSampleId(),
					n == null ? null : n.getLabel(),
					tr.getValue(),
					tr.getData()
					);
		}
	}
	
	public void export(Transaction t, TestType testType){
		Query q = t.createQuery("select tr from TestResult tr where tr.test.testType = :testType order by tr.test.testIi");
		q.setParameter("testType", testType);
	}
	
	public void export(String patientId, String test, String testType, String genome, String genericId, String sampleId, String label, String value, byte[] data){
		System.out.print("insert into test_result ");
		System.out.print("(version, test_ii, generic_ii, viral_isolate_ii, patient_ii, nt_sequence_ii, value, data, test_date)");
		System.out.print(" values (0, ");

		if(genome == null)
			System.out.println("(select test_ii from test t join test_type tt using(test_type_ii) where t.description='"+ test +"' and tt.description='"+ testType +"' limit 1),");
		else
			System.out.println("(select test_ii from test t join test_type tt using(test_type_ii) join genome g using(genome_ii) where t.description='"+ test +"' and tt.description='"+ testType +"' and g.organism_name='"+ genome +"' limit 1),");
		
		if(genericId == null)
			System.out.print("null,");
		else
			System.out.println("(select generic_ii from drug_generic where generic_id = '"+ genericId +"' limit 1),");

		if(sampleId == null)
			System.out.println("null,");
		else
			System.out.println("(select viral_isolate_ii from viral_isolate where sample_id='"+ sampleId +"' limit 1),");
			
		if(patientId == null)
			System.out.print("null,");
		else
			System.out.println("(select patient_ii from patient where patient_id = '"+ patientId +"' limit 1),");
		
		if(label == null)
			System.out.print("null,");
		else
			System.out.println("(select nt_sequence_ii from viral_isolate join nt_sequence using(viral_isolate_ii) where sample_id = '"+ sampleId +"' and label = '"+ label +"' limit 1),");
		
		if(value == null)
			System.out.print("null,");
		else
			System.out.print("'"+ value +"',");
		
		if(data == null)
			System.out.print("null,");
		else
			System.out.print("'"+ new String(data) +"',");
		
		System.out.println("'2011-05-03');");
	}
}
