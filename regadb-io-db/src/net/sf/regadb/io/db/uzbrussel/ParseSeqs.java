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
    private File seqMatchOldVl_;
    
    public ParseSeqs(String basePath, ParseIds parseIds, Map<Integer, Patient> patients, File seqMatchOldVl) {
        parseIds_ = parseIds;
        patients_ = patients;
        basePath_ = basePath;
        seqPath_ = new File(basePath_ + File.separatorChar + "labo" + File.separatorChar + "sequentions");
        patientIdsToIgnore = Utils.readTable(seqPath_.getAbsolutePath() + File.separatorChar + "seq_ignore.csv");
        seqMatchOldVl_ = seqMatchOldVl;
    }
    
    public void exec() {
        File seqMapping = new File(seqPath_.getAbsolutePath() + File.separatorChar + "seq_match.csv");
        int counter = handleSeqs(seqMapping);
        counter += handleSeqs(seqMatchOldVl_);
        seqMatchOldVl_.delete();
        
        System.err.println("Amount of succesfully imported sequences: "+ counter);
    }
    
    public int handleSeqs(File seqMapping) {
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

                    File seq = getSequence(seqId);
                    
                    if(seq != null) {
                        Patient p = getPatientForId(id);
                        if(p==null) {
                        	ConsoleLogger.getInstance().logWarning("Cannot find patient for sequence: " + id);
                        } else {
                        	if(checkUniqueSample(p, seqId)) {
                        		Integer erroneousPatientId = usedForOtherPatient(seqId);
                        		if(erroneousPatientId==null) {
		                            ViralIsolate vi = p.createViralIsolate();
		
		                            vi.setSampleId(seqId);
		                            vi.setSampleDate(d);
		                            
		                            getNtSeqsFromFile(seq, vi);
		
		                            counter++;
                        		} else {
                                    ConsoleLogger.getInstance().logError("Seqid was already used for patient(" +seqId + ")"+ erroneousPatientId + " - " + p.getPatientId() + "-" + seqDate);
                        		}
                        	} else {
                                ConsoleLogger.getInstance().logWarning("Seqid was already loaded" + seqId);
                        	}
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
        
        return counter;
    }
    
    private boolean checkUniqueSample(Patient p, String sampleId) {
    	for(ViralIsolate vi : p.getViralIsolates()) {
    		if(vi.getSampleId().equals(sampleId)) 
    			return false;
    	}
    	
    	return true;
    }
    
    private Integer usedForOtherPatient(String sampleId) {
    	for(Map.Entry<Integer, Patient> e : patients_.entrySet()) {
    		for(ViralIsolate vi : e.getValue().getViralIsolates()) {
    			if(vi.getSampleId().equals(sampleId)) {
    				return e.getKey();
    			}
    		}
    	}
    	
    	return null;
    }
    
    private File getSequence(String seqId) {
    	List<File> fileList = new ArrayList<File>();
    	
    	for(File dir : seqPath_.listFiles()) {
    		if(dir.isDirectory()) {
    			File seq = new File(dir.getAbsolutePath() + File.separatorChar +  getFileName(seqId)+".seq");
    			fileList.add(seq);
    			seq = new File(dir.getAbsolutePath() + File.separatorChar + "0"+seqId+".seq");
    			fileList.add(seq);
    			seq = new File(dir.getAbsolutePath() + File.separatorChar + getFileName(seqId));
    			fileList.add(seq);
    		}
    	}

    	for(File f : fileList) {
    		if(f.exists()) {
    			return f;
    		}
    	}
    	
    	return null;
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
