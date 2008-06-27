package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;

import org.apache.commons.io.FileUtils;

public class ParseSeqs {
    private static DateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd");
    
    private ParseIds parseIds_;
    private Map<Integer, Patient> patients_;
    private String basePath_;
    private File seqPath_;
    private Table patientIdsToIgnore;
    
    public ParseSeqs(String basePath, ParseIds parseIds, Map<Integer, Patient> patients) {
        parseIds_ = parseIds;
        patients_ = patients;
        basePath_ = basePath;
        seqPath_ = new File(basePath_ + File.separatorChar + "labo" + File.separatorChar + "sequentions");
        patientIdsToIgnore = Utils.readTable(seqPath_.getAbsolutePath() + File.separatorChar + "seq_ignore.csv");
    }
    
    public void exec() {
        File seqMapping = new File(seqPath_.getAbsolutePath() + File.separatorChar + "seq_match.csv");
        Table seqMappingTable = Utils.readTable(seqMapping.getAbsolutePath(), ';');
        int counter = 0;
        for(int i = 0; i<seqMappingTable.numRows(); i++) {
            String id = seqMappingTable.valueAt(0, i).trim();
            String seqId = seqMappingTable.valueAt(1, i).trim();
            String seqDate = seqMappingTable.valueAt(2, i).trim();
            String code = seqMappingTable.valueAt(3, i).trim();
            if(!id.equals("") && !seqId.equals("") && code.toLowerCase().equals("zbr") && !ignorePatientId(id)) {
                if(!seqDate.equals("")) {
                    Date d = null;
                    try {
                        d = dateFormatter.parse(seqDate);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    File seq = new File(getSequencePath(seqPath_, d) + getFileName(seqId)+".seq");
                    if(!seq.exists()) {
                        seq = new File(getSequencePath(seqPath_, d) + "0"+seqId+".seq");
                    }
                    if(seq.exists()) {
                        Patient p = getPatientForId(id);
                        if(p==null) {
                        	ConsoleLogger.getInstance().logWarning("Cannot find patient for sequence: " + id);
                        } else {
                            ViralIsolate vi = p.createViralIsolate();

                            vi.setSampleId(seqId);
                            vi.setSampleDate(d);
                            
                            getNtSeqsFromFile(seq, vi);

                            counter++;
                        }
                    } else {
                        ConsoleLogger.getInstance().logWarning("Cannot find sequenceFile for sequenceId - patientId: " + seqId + " - " +id);
                    }
                }
                else {
                    ConsoleLogger.getInstance().logError("A viral isolate should have a date: " + id + " " + seqId);
                }
            }
        }
        
        System.err.println("Amount of succesfully imported sequences: "+ counter);
    }
    
    private boolean ignorePatientId(String id) {
    	for(int i = 1; i<patientIdsToIgnore.numRows(); i++) {
    		String c = patientIdsToIgnore.valueAt(0, i);
    		if(id.equals(c)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private String getSequencePath(File seqPath, Date d) {
    	Calendar c = Calendar.getInstance();
        c.setTime(d);
    	return seqPath.getAbsolutePath()+File.separatorChar+"sequences"+File.separatorChar+c.get(Calendar.YEAR)+File.separatorChar;
    }
    
    private List<NtSequence> getNtSeqsFromFile(File seqFile, ViralIsolate vi) {
    	List<NtSequence> seqs = new ArrayList<NtSequence>();
    	String content = null;
        try {
            content = new String(FileUtils.readFileToByteArray(seqFile));
            String[] lines = content.split("\n");
            int counter = 1;
            for(String l : lines) {
            	if(!l.trim().equals("")) {
                    NtSequence ntseq = new NtSequence();
                    ntseq.setLabel("Sequence " + counter);
                    ntseq.setNucleotides(Utils.clearNucleotides(l));
                    vi.getNtSequences().add(ntseq);
                    counter ++;
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return seqs;
    }
    
    public String getFileName(String seqId) {
        StringBuffer id = new StringBuffer();
        boolean firstNotNull = false;
        for(int i = 2; i <seqId.length(); i++) {
            if(seqId.charAt(i)!='0') {
                firstNotNull = true;
            }
            if(firstNotNull) {
                id.append(seqId.charAt(i));
            }
        }
        
        if(id.length()==2)
            id.insert(0, "0");
        
        return "0"+seqId.charAt(0)+id.toString();
    }
    
    public Patient getPatientForId(String pId) {
        Integer id = parseIds_.getPatientId(pId);
        if(id!=null){
            return patients_.get(id);
        } else {
            return null;
        }
    }
}
