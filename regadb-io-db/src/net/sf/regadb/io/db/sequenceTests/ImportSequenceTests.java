package net.sf.regadb.io.db.sequenceTests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ImportSequenceTests {
	public static void main(String[] args) throws WrongUidException, WrongPasswordException, DisabledUserException, IOException {
    	Arguments as = new Arguments();
    	PositionalArgument user = as.addPositionalArgument("user", true);
    	PositionalArgument pass = as.addPositionalArgument("pass", true);
    	PositionalArgument inputCsv = as.addPositionalArgument("input.csv", true);
    	PositionalArgument testType = as.addPositionalArgument("test-type", true);
    	PositionalArgument test = as.addPositionalArgument("test", true);
    	PositionalArgument ds = as.addPositionalArgument("dataset", false);
    	ValueArgument conf = as.addValueArgument("conf-dir", "configuration directory", false);
    	
    	if(!as.handle(args))
    		return;

        if(conf.isSet())
        	RegaDBSettings.createInstance(conf.getValue());
        else
        	RegaDBSettings.createInstance();
        
        Login login = Login.authenticate(user.getValue(), pass.getValue());
        
        BufferedReader br = new BufferedReader(new FileReader(inputCsv.getValue()));
        try {
        	//first line of the CSV file; these are the col names
            String line = br.readLine();
            if (line == null)
            	throw new RuntimeException("Empty CSV file!");
            
            ArrayList<String> columnNames = Table.splitHandleQuotes(line, ',', '"', '\\');
            Map<String, Integer> ci = getColumnIndexMap(columnNames);
            
            //rest of the CSV file
            line = br.readLine();
            while (line != null) {
            	ArrayList<String> columns = Table.splitHandleQuotes(line, ',', '"', '\\');
            	
            	String patientId = columns.get(ci.get("patient-id"));
            	String sampleId = columns.get(ci.get("sample-id"));
            	String sequenceLabel = columns.get(ci.get("sequence-label"));
            	String testResult = columns.get(ci.get("test-result"));
            	
            	{
	            	Transaction tr = login.createTransaction();
	            	
	            	Dataset dataset = null;
	            	if (ds.isSet())
	            		dataset = tr.getDataset(ds.getValue());
	            	
	            	Patient p = null;
	            	if (dataset == null) {
	            		List<Patient> patients = new ArrayList<Patient>();
	            		for (Dataset d : tr.getDatasets()) {
	            			Patient _p = tr.getPatient(d, patientId);
	            			if (_p != null)
	            				patients.add(_p);
	            		}
	            		if (patients.size() > 1) {
	            			System.err.println("Error: More than one patient with id \"" + patientId + "\" was retrieved.");
	            			System.exit(1);
	            		} else if (patients.size() == 1) {
	            			p = patients.get(0);
	            		}
	            	} else {
	            		tr.getPatient(dataset, patientId);
	            	}
	            	
	            	if (p == null) {
            			System.err.println("Error: Could not find patient with id \"" + patientId + "\".");
            			System.exit(1);
	            	}
	            	
	            	ViralIsolate vi = null;
	            	for (ViralIsolate _vi : p.getViralIsolates()) {
	            		if (sampleId.equals(_vi.getSampleId())) {
	            			vi = _vi;
	            			break;
	            		}
	            	}
	            	
	            	if (vi == null) {
            			System.err.println("Error: Could not find isolate with id \"" + sampleId + "\" in patient with id \"" + patientId + "\"");
            			System.exit(1);
	            	}
	            	
	            	NtSequence seq = null;
	            	for (NtSequence _seq : vi.getNtSequences()) {
	            		if (sequenceLabel.equals(_seq.getLabel())) {
	            			seq = _seq;
	            			break;
	            		}
	            	}
	            	
	            	if (seq == null) {
            			System.err.println("Error: Could not find sequence with label \"" + sequenceLabel + "\" in isolate with id \"" + sampleId + "\" in patient with id \"" + patientId + "\"");
            			System.exit(1);
	            	}
	            	
	            	Test t = tr.getTest(test.getValue(), testType.getValue());
	            	
	            	TestResult ter = null;
	            	for (TestResult _ter : seq.getTestResults()) {
	            		if (_ter.getTest().getTestIi() == t.getTestIi()) {
	            			ter = _ter;
	            			break;
	            		}
	            	}
	            	if (ter != null) {
	            		seq.getTestResults().remove(ter);
	            		tr.delete(ter);
	            	}
	            		        
		            	TestResult result = new TestResult();
	            	if (testResult != null && !testResult.trim().equals("")) {
		            	result.setTest(t);
		            	setValue(t, result, testResult);
		            	result.setPatient(vi.getPatient());
		            	result.setNtSequence(seq);
		            	result.setTestDate(new Date());
		            	seq.getTestResults().add(result);
		            	tr.save(result);
	            	}
	            	
	            	tr.commit();
	            	
	            	System.err.println("Imported test result for sequence \"" + sequenceLabel + "\" viral isolate \"" + sampleId + "\"");
            	}
            	
                line = br.readLine();
            }
        } finally {
            br.close();
        }
	}
	
	private static void setValue(Test test, TestResult result, String value) {
		if (ValueTypes.getValueType(test.getTestType().getValueType()) == ValueTypes.NOMINAL_VALUE) {
			for( TestNominalValue tnv : test.getTestType().getTestNominalValues()) {
				if (value.equals(tnv.getValue())) 
					result.setTestNominalValue(tnv);
			}
			throw new RuntimeException("Could not find the nominal value \"" + value + "\"");
		} else {
			result.setValue(value);
		}
	}
	
	private static Map<String, Integer> getColumnIndexMap(List<String> columnNames) {
		Map<String, Integer> m = new HashMap<String, Integer>();
		int index = 0;
		for (String cn : columnNames) {
			m.put(cn, index);
			++index;
		}
		return m;
	}
}
