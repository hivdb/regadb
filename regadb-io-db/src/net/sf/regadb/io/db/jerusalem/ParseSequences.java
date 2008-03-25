package net.sf.regadb.io.db.jerusalem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.regadb.csv.Table;
import net.sf.regadb.io.db.util.Utils;

public class ParseSequences {
    public static void main(String [] args) {
        ParseSequences ps = new ParseSequences();
        ps.run("/home/plibin0/research/zehava/ResistanceDB_Sequences.csv");
    }
    
    public ParseSequences() {
        
    }
    
    public void run(String csvFile) {
        Table seqsTable = Utils.readTable(csvFile);
        
        int CsampleNo = Utils.findColumn(seqsTable, "SampleNo");
        int CID = Utils.findColumn(seqsTable, "ID");
        int CNotes = Utils.findColumn(seqsTable, "Notes");
        int CProtease = Utils.findColumn(seqsTable, "PrSeq");
        int CReverseTranscriptase = Utils.findColumn(seqsTable, "RTSeq");
        int CGP41 = Utils.findColumn(seqsTable, "gp41Seq");
        int CGP120 = Utils.findColumn(seqsTable, "gp120Seq");
        int CGAG = Utils.findColumn(seqsTable, "gagSeq");
        int CRTAA = Utils.findColumn(seqsTable, "RTAA");

        int counter = 0;
        FileWriter fw = null;
        try {
            fw = new FileWriter(new File("/home/plibin0/research/zehava/RT.fasta"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 1; i<seqsTable.numRows(); i++) {
            try {
                if(!seqsTable.valueAt(CReverseTranscriptase, i).trim().equals("")) {
                    fw.append(">"+seqsTable.valueAt(CsampleNo, i) + "" + seqsTable.valueAt(CID, i) + "\n");
                    fw.append(seqsTable.valueAt(CReverseTranscriptase, i)+"\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //if(!seqsTable.valueAt(CReverseTranscriptase, i).trim().equals(""))
            //    counter++;
        }
        System.err.println(counter);
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
}
