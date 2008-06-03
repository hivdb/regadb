package net.sf.regadb.io.db.brescia;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

import org.biojava.bio.symbol.IllegalSymbolException;

public class ImportSequences 
{
    private Map<String, Patient> patientMap_;
    private File sequenceExcellFile_;
    private Aligner aligner_;
    private Map<String, Protein> proteinMap_;
    
    public ImportSequences(Map<String, Patient> patientMap, File sequenceExcellFile) {
        patientMap_ = patientMap;
        sequenceExcellFile_ = sequenceExcellFile;
        
        proteinMap_ = new HashMap<String, Protein>();
        
        for(Protein p : StandardObjects.getProteins()) {
            proteinMap_.put(p.getAbbreviation(), p);
        }
        
        aligner_ = new Aligner(new LocalAlignmentService(), proteinMap_);
    }
    
    public void run() {
        Workbook wb = null;
        try {
            wb = Workbook.getWorkbook(sequenceExcellFile_);
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Sheet s = wb.getSheet(0);
        
        
        int CpatientId = indexOf("ID_New", s);
        int CsampleDate = indexOf("DataTest", s);
        int Csequence = indexOf("Sequenza", s);
        
        int counter = 1;
        
        int emptyCounter = 0;
        
        for (int i = 1; i < s.getRows(); i++) 
        {
            String patientId = s.getCell(CpatientId, i).getContents().trim();
            String sampleDate = s.getCell(CsampleDate, i).getContents().trim();
            String seq = s.getCell(Csequence, i).getContents().trim();
            
            Patient p = patientMap_.get(patientId);
            
            if (p == null) 
            {
                ConsoleLogger.getInstance().logWarning(
                        "No sequence patient with id " + patientId + " found.");
            } 
            else 
            {
                    Date gtDate = Utils.parseBresciaSeqDate(sampleDate);

                    if(!"".equals(seq)) 
                    {
                        if (gtDate != null) 
                        {
                        	//ConsoleLogger.getInstance().logInfo("Aligning sequence "+i+" for patient "+patientId+"...");
                        	
                            ViralIsolate vi = p.createViralIsolate();
                            vi.setSampleDate(gtDate);
                            vi.setSampleId(counter+"");
                            counter++;
                            
                            NtSequence ntseq = new NtSequence();
                            ntseq.setLabel("Sequence 1");
                            ntseq.setNucleotides(parseNucleotides(seq, patientId, true));
                            
                            vi.getNtSequences().add(ntseq);
                            
                            //ConsoleLogger.getInstance().logInfo("Successfully aligned!");
                        } 
                        else 
                        {
                            ConsoleLogger.getInstance().logWarning(
                                    "Invalid date specified in the viral isolate file ("
                                            + i + " -> " + sampleDate + ").");
                        }
                    } 
                    else 
                    {
                        ConsoleLogger.getInstance().logWarning("Empty seq for patient "+patientId+"");
                        
                        emptyCounter++;
                    }
                }
            }
        
        ConsoleLogger.getInstance().logInfo(""+counter+" sequence(s) added");
        ConsoleLogger.getInstance().logInfo(""+emptyCounter+" blank sequence(s) found");
    }

    public int indexOf(String colName, Sheet s) {
        for(Cell c : s.getRow(0)) {
            if(colName.equals(c.getContents())) {
                return c.getColumn();
            }
        }
        return -1;
    }
    
    private String parseNucleotides(String nucleotides, String patientID, boolean cleanNts)
    {
        int index = -1;
        
        for(int i = 0; i<1000 ;i++) {
            index = nucleotides.indexOf("D"+i);
            if(index!=-1)
                break;
        }
        
        if(index != -1) {
            String tempSeq = nucleotides.substring(index+2, nucleotides.length());      
            if(cleanNts)
                nucleotides = Utils.clearNucleotides(tempSeq);
            else
                nucleotides = tempSeq;
        }
        else {
            ConsoleLogger.getInstance().logWarning("Could not determine sequence for patient "+patientID+" from string "+nucleotides);
        }
        
        return nucleotides.toLowerCase();
    }
    
    public static void main(String [] args) {
        ImportSequences is = new ImportSequences(new HashMap<String, Patient>(), 
                new File(args[0]));
        is.run();
    }
}
