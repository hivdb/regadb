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

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.util.pair.Pair;

public class GetViralIsolates {
    private int counterS;
    private Map<String, List<TestResult>> excellList;
    private Table spreadSampleIds;
    private Table samplesToBeIgnored;
    private Map<String, ViralIsolate> vis = new HashMap<String, ViralIsolate>();
    private Map<String, List<TestResult>> lisResults_;
    
    public static void main(String [] args) {
        GetViralIsolates gvi = new GetViralIsolates();
        //run mergelis and provide the testresults obtained to the run method
        gvi.run(null);
    }
    
    public GetViralIsolates() {
        
    }
    
    public void run(Map<String, List<TestResult>> lisResults) {
        counterS = 0;
        excellList = this.parseExcelFile(new File("/home/plibin0/import/ghb/seqs/Stalen Leuven.csv"));

        lisResults_ = lisResults;
        
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
            Pair<String, ViralIsolate> vi = getViralIsolate(id, seq);
            if(vi!=null) {
                vis.put(vi.getKey(), vi.getValue());
                //System.err.println("Can find reference to: " + id);     
                //counterS++;
            } else {
                if(!isSpreadSample(id) && !canBeIgnored(id)) {
                    System.err.println("Cannot find reference to: " + id);     
                    counterS++;
                }
            }
        }
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
    
    public Pair<String, ViralIsolate> getViralIsolate(String id, String seq) {
        for(Entry<String, List<TestResult>> es : excellList.entrySet()) {
            for(TestResult res : es.getValue()) {
                if(res.getSampleId().equals(id)) {
                    ViralIsolate vi = new ViralIsolate();
                    vi.setSampleDate(res.getTestDate());
                    vi.setSampleId(res.getSampleId());
                    NtSequence ntseq = new NtSequence();
                    ntseq.setLabel("Sequence 1");
                    ntseq.setSequenceDate(res.getTestDate());
                    ntseq.setNucleotides(seq);
                    return new Pair<String, ViralIsolate>(es.getKey(), vi);
                }
            }
        }
        for(Entry<String, List<TestResult>> es : lisResults_.entrySet()) {
            for(TestResult res : es.getValue()) {
                if(res.getSampleId().equals(id)) {
                    ViralIsolate vi = new ViralIsolate();
                    vi.setSampleDate(res.getTestDate());
                    vi.setSampleId(res.getSampleId());
                    NtSequence ntseq = new NtSequence();
                    ntseq.setLabel("Sequence 1");
                    ntseq.setSequenceDate(res.getTestDate());
                    ntseq.setNucleotides(seq);
                    return new Pair<String, ViralIsolate>(es.getKey(), vi);
                }
            }
        }
        return null;
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