package net.sf.regadb.io.db.ucsc;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.io.util.StandardObjects;

import org.apache.commons.io.FileUtils;
import org.biojava.bio.symbol.IllegalSymbolException;

public class ImportUcsc {
    public static void main(String [] args) {
        ImportUcsc imp = new  ImportUcsc();
        imp.run(new File(args[0]));
    }
    
    public void run(File workingDirectory) {
        try {
            getSequences(workingDirectory);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    //to obtain this, run the following query in microsoft access
    //SELECT T_genotipo_HIV.[cartella UCSC], T_genotipo_HIV.[data genotipo], T_genotipo_HIV.[sequenza basi azotate (fasta)], ""
    //FROM T_genotipo_HIV;
    //export with (; and no text separation sign)
    public void getSequences(File workingDirectory) throws IOException {
        File onlySequences = new File(workingDirectory.getAbsolutePath()+File.separatorChar+"getOnlyGenotypes.txt");
        String fileContent = new String(FileUtils.readFileToByteArray(onlySequences));
        StringTokenizer st = new StringTokenizer(fileContent, ";");
        System.err.println(st.countTokens());
        String token;
        int linePositionCounter = 0;
        for(int i = 0; i<st.countTokens(); i++) {
            token = st.nextToken();
            if(linePositionCounter==2) {
                Aligner aligner = new Aligner(new LocalAlignmentService(), StandardObjects.getProteinMap());
                try {
                    NtSequence ntseq = new NtSequence();
                    ntseq.setNucleotides(clearNucleotides(token));
                    aligner.alignHiv(ntseq).size();
                } catch (IllegalSymbolException e) {
                    e.printStackTrace();
                }
                linePositionCounter = 0;
            } else {
                linePositionCounter++;
            }
        }
    }
    
    public String clearNucleotides(String nucleotides) {
        StringBuffer toReturn = new StringBuffer();
        for(char c : nucleotides.toCharArray()) {
            if(Character.isLetter(c)) {
                toReturn.append(c);
            }
        }
        return toReturn.toString();
    }
}
