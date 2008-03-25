package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

import org.apache.commons.io.FileUtils;
import org.biojava.bio.symbol.IllegalSymbolException;

public class ParseSeqs {
    private static DateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd");
    
    private ParseIds parseIds_;
    private Map<Integer, Patient> patients_;
    private String basePath_;
    
    public ParseSeqs(String basePath, ParseIds parseIds, Map<Integer, Patient> patients) {
        parseIds_ = parseIds;
        patients_ = patients;
        basePath_ = basePath;
    }
    
    public void exec() {
        File seqPath = new File(basePath_ + File.separatorChar + "labo" + File.separatorChar + "sequentions");
        File seqMapping = new File(seqPath.getAbsolutePath() + File.separatorChar + "seq_match.csv");
        Table seqMappingTable = Utils.readTable(seqMapping.getAbsolutePath(), ';');
        int counter = 0;
        for(int i = 0; i<seqMappingTable.numRows(); i++) {
            String id = seqMappingTable.valueAt(0, i).trim();
            String seqId = seqMappingTable.valueAt(1, i).trim();
            String seqDate = seqMappingTable.valueAt(2, i).trim();
            if(!id.equals("") && !seqId.equals("")) {
                if(!seqDate.equals("")) {
                    File seq = new File(seqPath.getAbsolutePath()+File.separatorChar+getFileName(seqId)+".seq");
                    if(!seq.exists()) {
                        seq = new File(seqPath.getAbsolutePath()+File.separatorChar+"0"+seqId+".seq");
                    }
                    if(seq.exists()) {
                        Patient p = getPatientForId(id);
                        //p = new Patient();
                        if(p==null) {
                            ConsoleLogger.getInstance().logError("Cannot find patient for sequence: " + id);
                        } else {
                            ViralIsolate vi = p.createViralIsolate();
                            try {
                                vi.setSampleDate(dateFormatter.parse(seqDate));
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            vi.setSampleId(seqId);
                            
                            NtSequence ntseq = new NtSequence();
                            ntseq.setLabel("Sequence 1");
                            ntseq.setNucleotides(getNtSeqFromFile(seq));
                            vi.getNtSequences().add(ntseq);
                        }
                    }
                }
                else {
                    ConsoleLogger.getInstance().logError("A viral isolate should have a date: " + id + " " + seqId);
                }
            }
        }
    }
    
    private String getNtSeqFromFile(File seqFile) {
        String content = null;
        try {
            content = new String(FileUtils.readFileToByteArray(seqFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return Utils.clearNucleotides(content);
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
