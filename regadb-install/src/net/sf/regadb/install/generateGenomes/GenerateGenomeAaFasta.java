package net.sf.regadb.install.generateGenomes;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;

import org.apache.commons.io.FileUtils;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.SymbolList;

public class GenerateGenomeAaFasta {
	public static void main(String[] args) {
        String basename = "L20587";
        GenerateGenome hiv1ogen = new GenerateGenome("HIV-1 group O","HIV-1 group O","genbank 1",GenerateGenome.getReferenceSequence(basename +".fasta"));
        hiv1ogen.generateFromFile(basename +".genome");

        //System.out.println(hiv1ogen.toString(true));
        
        writeAaFastas(hiv1ogen.getGenome(), args[0]);
	}
	
	private static void writeAaFastas(Genome g, String basePath) {
        for(OpenReadingFrame orf : g.getOpenReadingFrames()){
            for(Protein p : orf.getProteins()){
                String pSeq = orf.getReferenceSequence().substring(p.getStartPosition()-1,p.getStopPosition()-1);
                String aaSeq = "";
                String ntSeq = "";
                for (int i = 0; i < pSeq.length(); i = i + 3) {
                	String codon = pSeq.charAt(i) + "" + pSeq.charAt(i + 1) + "" + pSeq.charAt(i + 2);
                	ntSeq += codon; 
                	aaSeq += getAminoAcid(codon);
                }
                try {
					FileUtils.writeStringToFile(new File(basePath + p.getAbbreviation() + ".fasta"), ">"+p.getAbbreviation()+"\n"+aaSeq);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
	}
	
    public static String getAminoAcid(String nt)
    {
        if(nt.charAt(0)==' ')
            return " ";
        else if(nt.charAt(0)=='-')
            return "-";
        
        try
        {
            SymbolList symL = DNATools.createDNA(nt.toString());
            symL = DNATools.toRNA(symL);
            symL = RNATools.translate(symL);
            
            return symL.seqString().charAt(0) + "";
        }
        catch(Exception e)
        {
            return "$";
        }
    }
}
