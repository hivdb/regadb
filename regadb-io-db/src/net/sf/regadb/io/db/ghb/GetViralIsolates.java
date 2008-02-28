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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.regadb.analysis.functions.NtSequenceHelper;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.Utils;

public class GetViralIsolates {
    private int counterS;
    private Map<String, List<TestResult>> excellList;
    private Table spreadSampleIds;
    private Table samplesToBeIgnored;
    
    public Map<String, Patient> eadPatients;
    
    public static void main(String [] args) {
        GetViralIsolates gvi = new GetViralIsolates();
        //run mergelis and provide the testresults obtained to the run method
        gvi.run();
    }
    
    public GetViralIsolates() {
        
    }
    
    public void run() {
        counterS = 0;
        excellList = this.parseExcelFile(new File("/home/plibin0/import/ghb/seqs/Stalen Leuven.csv"));

        spreadSampleIds = Utils.readTable("/home/plibin0/import/ghb/seqs/SPREAD_stalen.csv");
        samplesToBeIgnored = Utils.readTable("/home/plibin0/myWorkspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/sequencesToIgnore.csv");
                
        String seq,id;

        try {
            BufferedReader br= new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/plibin0/import/ghb/seqs/MAC_final.fasta"))));
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
            BufferedReader br= new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/plibin0/import/ghb/seqs/PC_final.fasta"))));
            while((id = br.readLine())!=null) {
                seq = br.readLine();
                handleIsolate(id.substring(1, id.length()), seq);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        System.err.println(counterS);
    }

    public void handleIsolate(String id, String seq) {
        if(seq!=null) {
            String processedSeq = correctSeq(seq);
            boolean vi = getViralIsolate(id, processedSeq);
            if(!vi) {
                if(!isSpreadSample(id) && !canBeIgnored(id)) {
                    System.err.println("Cannot find reference to: " + id);
                }
            } else {
                counterS++;
            }
        }
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
    
    public boolean getViralIsolate(String id, String seq) {
        for(Entry<String, List<TestResult>> es : excellList.entrySet()) {
            for(TestResult res : es.getValue()) {
                if(res.getSampleId().equals(id)) {
                    Patient p = eadPatients.get(es.getKey());
                    if(p!=null) {
                        ViralIsolate vi = eadPatients.get(es.getKey()).createViralIsolate();
                        vi.setSampleDate(res.getTestDate());
                        vi.setSampleId(res.getSampleId());
                        NtSequence ntseq = new NtSequence();
                        ntseq.setViralIsolate(vi);
                        vi.getNtSequences().add(ntseq);
                        ntseq.setLabel("Sequence 1");
                        ntseq.setSequenceDate(res.getTestDate());
                        ntseq.setNucleotides(seq);
                        return true; 
                    } else {
                        System.err.println("No patient with ead " + es.getKey() + " for id " + id);
                        return false;
                    }
                }
            }
        }
        for(Entry<String, Patient> es : eadPatients.entrySet()) {
            for(TestResult res : es.getValue().getTestResults()) {
                if(res.getSampleId().equals(id)) {
                        ViralIsolate vi = eadPatients.get(es.getKey()).createViralIsolate();
                        vi.setSampleDate(res.getTestDate());
                        vi.setSampleId(res.getSampleId());
                        NtSequence ntseq = new NtSequence();
                        ntseq.setViralIsolate(vi);
                        vi.getNtSequences().add(ntseq);
                        ntseq.setLabel("Sequence 1");
                        ntseq.setSequenceDate(res.getTestDate());
                        ntseq.setNucleotides(seq);
                        return true;
                }
            }
        }
        return false;
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
