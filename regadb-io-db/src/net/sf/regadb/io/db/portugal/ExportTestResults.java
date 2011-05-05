package net.sf.regadb.io.db.portugal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

public class ExportTestResults {

	public static void main(String[] args) throws WrongUidException, WrongPasswordException, DisabledUserException, FileNotFoundException{
		Arguments as = new Arguments();
		PositionalArgument user = as.addPositionalArgument("user", true);
		PositionalArgument pass = as.addPositionalArgument("pass", true);
		
		PositionalArgument aTest = as.addPositionalArgument("test", false);
		PositionalArgument aTestType = as.addPositionalArgument("test-type", false);
		PositionalArgument aGenome = as.addPositionalArgument("genome", false);
		
		ValueArgument inFile = as.addValueArgument("i", "input-file", false);
		ValueArgument outFile = as.addValueArgument("o", "output-file", false);
		
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
		if(aTest.isSet() && aTestType.isSet()){
			if(!aGenome.isSet())
				test = t.getTest(aTest.getValue(), aTestType.getValue());
			else
				test = t.getTest(aTest.getValue(), aTestType.getValue(), aGenome.getValue());
			
			if(test == null)
				System.err.println("test not found");
		}
		
		if(test != null || inFile.isSet()){
			PrintStream out = System.out;
			if(outFile.isSet())
				out = new PrintStream(new FileOutputStream(new File(outFile.getValue())));
			
			ExportTestResults exp = new ExportTestResults();
			if(inFile.isSet())
				exp.export(out, login, new File(inFile.getValue()));
			else
				exp.export(out, t, test, aTest.getValue(), aTestType.getValue(), aGenome.getValue(), null, null);
			
			if(outFile.isSet())
				out.close();
		}
		
		t.commit();
	}
	
	
	public void export(PrintStream out, Login login, File input) throws FileNotFoundException{
		BufferedReader br = new BufferedReader(new FileReader(input));
		Transaction t = login.createTransaction();
		
		Test subtypeTest = t.getTest(StandardObjects.getSubtypeTestDescription());
		String genome = StandardObjects.getHiv1Genome().getOrganismName();
		String gssTestType = StandardObjects.getGssDescription();
		
		String line;
		Test test = null;
		
		List<String> sampleIds = new ArrayList<String>();
		List<String> labels = new ArrayList<String>();
		
		try {
			while((line = br.readLine()) != null){
				line = line.trim();

				if(line.length() == 0)
					continue;
				
				if(line.contains(":")){
					if(test != null){
						export(out,
								t,
								test,
								test.getDescription(),
								test.getTestType().getDescription(),
								test.getTestType().getGenome() == null ? null : test.getTestType().getGenome().getOrganismName(),
								sampleIds,
								labels);
						t.commit();
						t.clearCache();
						t = login.createTransaction();
						t.attach(subtypeTest);
					}
					
					sampleIds.clear();
					labels.clear();
					
					if(line.contains("subtype")){
						test = subtypeTest;
					} else {
						test = t.getTest(line.substring(0,line.indexOf('(')), gssTestType, genome);
					}
				} else if(line.contains(",")) {
					String[] s = line.split(",");
					
					sampleIds.add(s[0]);
					if(test == subtypeTest){
						labels.add(s[1]);
					}
				}
			}
			export(out,
					t,
					test,
					test.getDescription(),
					test.getTestType().getDescription(),
					test.getTestType().getGenome() == null ? null : test.getTestType().getGenome().getOrganismName(),
					sampleIds,
					labels);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings("unchecked")
	public void export(PrintStream out, Transaction t, Test test, String testDescr, String testTypeDescr, String genome, List<String> sampleIds, List<String> labels){
		out.println("set search_path to regadbschema;");
		
		Query q = t.createQuery("select p.patientId, tr from TestResult tr left outer join tr.patient p where tr.test = :test");
		q.setParameter("test", test);
		
		for(Object[] o : (List<Object[]>)q.list()){
			TestResult tr = (TestResult)o[1];
			DrugGeneric dg = tr.getDrugGeneric();
			ViralIsolate v = tr.getViralIsolate();
			NtSequence n = tr.getNtSequence();
			if(v == null && n != null)
				v = n.getViralIsolate();
			
			if(sampleIds != null){
				int i = sampleIds.indexOf(v.getSampleId());
				
				if(i == -1)
					continue;
				
				if(labels != null && labels.size() > 0){
					if(n == null || !labels.get(i).equals(n.getLabel()))
						continue;
				}
			}
			
			export(
					out,
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
	
	public void export(PrintStream out, String patientId, String test, String testType, String genome, String genericId, String sampleId, String label, String value, byte[] data){
		out.print("insert into test_result ");
		out.print("(version, test_ii, generic_ii, viral_isolate_ii, patient_ii, nt_sequence_ii, value, data, test_date)");
		out.print(" values (0, ");

		if(genome == null)
			out.println("(select test_ii from test t join test_type tt using(test_type_ii) where t.description='"+ test +"' and tt.description='"+ testType +"' limit 1),");
		else
			out.println("(select test_ii from test t join test_type tt using(test_type_ii) join genome g using(genome_ii) where t.description='"+ test +"' and tt.description='"+ testType +"' and g.organism_name='"+ genome +"' limit 1),");
		
		if(genericId == null)
			out.print("null,");
		else
			out.println("(select generic_ii from drug_generic where generic_id = '"+ genericId +"' limit 1),");

		if(sampleId == null || label != null)
			out.println("null,");
		else
			out.println("(select viral_isolate_ii from viral_isolate where sample_id='"+ sampleId +"' limit 1),");
			
		if(patientId == null)
			out.print("null,");
		else
			out.println("(select patient_ii from patient where patient_id = '"+ patientId +"' limit 1),");
		
		if(label == null)
			out.print("null,");
		else
			out.println("(select nt_sequence_ii from viral_isolate join nt_sequence using(viral_isolate_ii) where sample_id = '"+ sampleId +"' and label = '"+ label +"' limit 1),");
		
		if(value == null)
			out.print("null,");
		else
			out.print("'"+ value +"',");
		
		if(data == null)
			out.print("null,");
		else
			out.print("'"+ new String(data) +"',");
		
		out.println("'2011-05-03');");
	}
}
