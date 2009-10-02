package net.sf.regadb.io.db.ghb;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.regadb.analysis.functions.NtSequenceHelper;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.Utils;

public class GetViralIsolates {
    private Map<String, List<TestResult>> excellList;
    private Table spreadSampleIds;
    private Table samplesToBeIgnored;
    
    private Set<String> sampleIds = new HashSet<String>();
    
    public Map<String, Patient> eadPatients;
    
    public static void main(String [] args) {
        GetViralIsolates gvi = new GetViralIsolates();
        //run mergelis and provide the testresults obtained to the run method
        if(args.length >= 5){
            gvi.run(args[0], args[1], args[2], args[3], args[4]);
        }
        else{
            gvi.run("/home/simbre1/tmp/import/ghb/seqs/Stalen Leuven.csv",
                    "/home/simbre1/tmp/import/ghb/seqs/SPREAD_stalen.csv",
                    "/home/simbre1/workspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/sequencesToIgnore.csv",
                    "/home/simbre1/tmp/import/ghb/seqs/MAC_final.fasta",
                    "/home/simbre1/tmp/import/ghb/seqs/PC_final.fasta");
        }
    }
    
    public GetViralIsolates() {
        
    }
    
    public void run(String sequencesDir){
    	String d = new File(sequencesDir).getAbsolutePath() + File.separatorChar;
		run(d + "Stalen leuven.csv",
			d + "SPREAD_stalen.csv",
			d + "sequencesToIgnore.csv",
			d + "MAC_final.fasta",
			d + "PC_final.fasta");
    }
    
    public void run(String stalenLeuvenFile, String spreadStalenFile, String seqsIgnoreFile, String macFastaFile, String pcFastaFile) {
        excellList = this.parseExcelFile(new File(stalenLeuvenFile));

        spreadSampleIds = Utils.readTable(spreadStalenFile);
        samplesToBeIgnored = Utils.readTable(seqsIgnoreFile);
                
        String seq,id;

        try {
            BufferedReader br= new BufferedReader(new InputStreamReader(new FileInputStream(new File(macFastaFile))));
            while((id = br.readLine())!=null) {
                seq = br.readLine();
                handleIsolate(id.substring(1, id.length()), seq);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br= new BufferedReader(new InputStreamReader(new FileInputStream(new File(pcFastaFile))));
            while((id = br.readLine())!=null) {
                seq = br.readLine();
                handleIsolate(id.substring(1, id.length()), seq);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        System.err.println(sampleIds.size());
    }

    public void handleIsolate(String sampleId, String nucleotides) {
        if(nucleotides!=null) {
            if(!isDuplicate(sampleId)){
            	String processedNucleotides = correctSeq(nucleotides);
            	
        		if(findInExcellFile(sampleId, processedNucleotides)
        				|| findInTestResults(sampleId, processedNucleotides)){
        			addSampleId(sampleId);
        		}
        		else if(!isSpreadSample(sampleId) && !canBeIgnored(sampleId)){
                    System.err.println("Cannot find reference to: " + sampleId);
        		}
        	} else {
        		System.err.println("Duplicate sample id: '"+ sampleId +'\'');
        	}
        }
    }
    
    protected boolean isDuplicate(String sampleId){
    	return sampleIds.contains(sampleId);
    }
    
    protected void addSampleId(String sampleId){
    	sampleIds.add(sampleId);
    }
    
    public String correctSeq(String seq) {
        StringBuffer validChars = new StringBuffer();
        
        for(int i = 0; i<seq.length(); i++) {
            if(NtSequenceHelper.isValidNtCharacter(seq.charAt(i))) {
                validChars.append(seq.charAt(i));
            }
        }
        
        return validChars.toString();
    }
    
    private boolean canBeIgnored(String id) {
        for(int i = 1; i<samplesToBeIgnored.numRows(); i++) {
            String ignoreId = samplesToBeIgnored.valueAt(0, i);
            ignoreId = ignoreId.substring(ignoreId.indexOf(':')+2, ignoreId.length());
            if(ignoreId.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSpreadSample(String id) {
        for(int i = 1; i<spreadSampleIds.numRows(); i++) {
            if(id.equals(spreadSampleIds.valueAt(0, i).trim()))
                return true;
        }
        return false;
    }
    
    protected ViralIsolate createViralIsolate(String sampleId, Date sampleDate, String nucleotides){
    	ViralIsolate vi = new ViralIsolate();
    	vi.setSampleId(sampleId);
    	vi.setSampleDate(sampleDate);

    	NtSequence nt = new NtSequence();
    	nt.setLabel("Sequence 1");
    	nt.setSequenceDate(sampleDate);
    	nt.setNucleotides(nucleotides);
    	
    	nt.setViralIsolate(vi);
    	vi.getNtSequences().add(nt);

    	return vi;
    }
    
    protected boolean findInExcellFile(String sampleId, String nucleotides) {
        for(Entry<String, List<TestResult>> es : excellList.entrySet()) {
            for(TestResult res : es.getValue()) {
                if(res.getSampleId().equals(sampleId)) {
                    Patient p = getPatient(es.getKey());
                    if(p!=null) {
                    	ViralIsolate vi = createViralIsolate(res.getSampleId(), res.getTestDate(), nucleotides);
                        p.addViralIsolate(vi);
                        return true;
                    } else {
                        System.err.println("No patient with ead " + es.getKey() + " for id " + sampleId);
                        return false;
                    }
                }
            }
        }
        return false;
    }
    
    protected boolean findInTestResults(String sampleId, String nucleotides) {
    	for(Patient p : getPatients()) {
            for(TestResult res : p.getTestResults()) {
                if(res.getSampleId().equals(sampleId)) {
            		ViralIsolate vi = createViralIsolate(res.getSampleId(), res.getTestDate(), nucleotides);
                    p.addViralIsolate(vi);
                    return true;
                }
            }
        }
    	return false;
    }
    
    protected Patient getPatient(String ead){
    	return eadPatients.get(ead);
    }
    protected Collection<Patient> getPatients(){
    	return eadPatients.values();
    }
    
    public Map<String, List<TestResult>> parseExcelFile(File excelFile) {
        DateFormat excellDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Map<String, List<TestResult>> map = new HashMap<String, List<TestResult>>();
        Table seqMapping = null;
        try {
            seqMapping = new Table(new BufferedInputStream(new FileInputStream(excelFile)), false);
            int firstDateCol = -1;
            for(int i = 0; i<seqMapping.numColumns(); i++) {
                if(seqMapping.valueAt(i, 0).startsWith("Datum")) {
                    for(int j = 0; j<seqMapping.numRows(); j++) {
                        String datum = seqMapping.valueAt(i, j);
                        String number = seqMapping.valueAt(i+1, j);
                        String ead = seqMapping.valueAt(0, j);
                        if(!datum.equals("") && !number.equals("") && !ead.equals("") && !datum.startsWith("Datum")) {
                            List<TestResult> results = map.get(ead);
                            if(results==null) {
                                results = new ArrayList<TestResult>();
                                map.put(ead, results);
                            }
                            
                            TestResult tr = new TestResult();
                            results.add(tr);
                            try {
                                tr.setTestDate(excellDateFormat.parse(datum));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            tr.setSampleId(number);
                        }
                    }
                }
            }
            System.err.println(firstDateCol);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }
}
