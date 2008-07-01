package net.sf.regadb.io.db.telaviv;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.Logging;
import net.sf.regadb.io.db.util.Utils;

public class ParseSequences extends Parser{
    DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    
    public static void main(String [] args) {
        ParseSequences ps = new ParseSequences();
        ps.run(new File("/home/plibin0/import/telaviv/csv/Sequences.csv"), new File("/home/plibin0/import/telaviv/csv/Samples.csv"), new HashMap<String, Patient> ());
    }
    
    public ParseSequences(){
    }
    
    public ParseSequences(Logging logger, List<DateFormat> df) {
        super(logger,df);
    }
    
    public void run(File csvSequenceFile, File csvSampleFile, Map<String, Patient> patients) {
        Table seqsTable = Utils.readTable(csvSequenceFile.getAbsolutePath());
        Table sampleTable = Utils.readTable(csvSampleFile.getAbsolutePath());
        
        int CSampleSampleNo = Utils.findColumn(sampleTable, "SampleNo");
        int CSampleDateTaken = Utils.findColumn(sampleTable, "DateTaken");
        int CSampleDateAnswer = Utils.findColumn(sampleTable, "DateAnswer");
        
        int CSeqsampleNo = Utils.findColumn(seqsTable, "SampleNo");
        int CID = Utils.findColumn(seqsTable, "ID");
        //int CNotes = Utils.findColumn(seqsTable, "Notes");
        
        int CProtease = Utils.findColumn(seqsTable, "PrSeq");
        int CReverseTranscriptase = Utils.findColumn(seqsTable, "RTSeq");
        int CGP41 = Utils.findColumn(seqsTable, "gp41Seq");
        int CGP120 = Utils.findColumn(seqsTable, "gp120Seq");
        int CGAG = Utils.findColumn(seqsTable, "gagSeq");
        
        //int CRTAA = Utils.findColumn(seqsTable, "RTAA");

        for(int i=1; i<seqsTable.numRows(); i++) {
            String sampleNo = seqsTable.valueAt(CSeqsampleNo, i);
            String dateTaken = null;
            String dateAnswer = null;
            for(int j=1; j<sampleTable.numRows(); j++) {
                String sampleSampleNo = sampleTable.valueAt(CSampleSampleNo, j);
                if(sampleNo.equals(sampleSampleNo)) {
                    dateTaken = sampleTable.valueAt(CSampleDateTaken, j);
                    if(dateTaken.trim().equals("")) 
                        dateTaken = null;
                    dateAnswer = sampleTable.valueAt(CSampleDateAnswer, j);
                    if(dateAnswer.trim().equals("")) 
                        dateAnswer = null;
                }
            }
            if(dateTaken==null || dateAnswer==null) {
                System.err.println("No dates for sequence with sampleno " + sampleNo);
            } else {
                List<NtSequence> seqs = new ArrayList<NtSequence>();
                String pro = seqsTable.valueAt(CProtease, i);
                    handleSequence(pro, seqs, dateAnswer, sampleNo);
                String rt = seqsTable.valueAt(CReverseTranscriptase, i);
                    handleSequence(rt, seqs, dateAnswer, sampleNo);
                String gp41 = seqsTable.valueAt(CGP41, i);
                    handleSequence(gp41, seqs, dateAnswer, sampleNo);
                String gp120 = seqsTable.valueAt(CGP120, i);
                    handleSequence(gp120, seqs, dateAnswer, sampleNo);
                //String gag = seqsTable.valueAt(CGAG, i);
                //    handleSequence(gag, seqs, dateAnswer, sampleNo);
                    
                Patient p = patients.get(seqsTable.valueAt(CID, i));
                if(seqs.size()>0) {
                    ViralIsolate vi = p.createViralIsolate();
                    vi.setSampleId(sampleNo);
                    try {
                        vi.setSampleDate(df.parse(dateTaken));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    for(NtSequence ntseq : seqs) {
                        vi.getNtSequences().add(ntseq);
                    }
                }
            }
        }
    }
    
    public void handleSequence(String seq, List<NtSequence> seqs, String dateAnswer, String sampleNo) {
        if(!seq.equals("")) {
            NtSequence ntseq = new NtSequence();
            try {
                ntseq.setSequenceDate(df.parse(dateAnswer));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ntseq.setNucleotides(Utils.clearNucleotides(seq));
            ntseq.setLabel("Sequence " + (seqs.size()+1));
            
            seqs.add(ntseq);
        }
    }
}