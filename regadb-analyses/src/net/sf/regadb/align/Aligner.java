/*
 * Created on Jan 5, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.align;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaInsertionId;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaMutationId;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.io.SymbolTokenization;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;

public class Aligner {
    private AlignmentService service;

    private final SymbolTokenization aatok;
    private final SymbolTokenization nttok;
    
    private static Map<OpenReadingFrame,Sequence> referenceSequences = new HashMap<OpenReadingFrame,Sequence>();

    public Aligner(AlignmentService service) {
        this.service = service;
        try {
            aatok = ProteinTools.getTAlphabet().getTokenization("token");
            nttok = DNATools.getDNA().getTokenization("token");
        } catch (BioException e) {
            throw new RuntimeException(e);
        }
    }

//    public List<AaSequence> alignHiv(NtSequence seq) throws IllegalSymbolException {
//
//        return align(seq, HivGenome.getHxb2());
//    }

    public List<AaSequence> align(NtSequence seq, Genome genome) throws IllegalSymbolException {

        Sequence s = DNATools.createDNASequence(seq.getNucleotides(), "target");

        List<AaSequence> result = new ArrayList<AaSequence>();

        for (OpenReadingFrame orf : genome.getOpenReadingFrames()) {
            System.err.println("Trying: " + orf.getName());
            
            List<AaSequence> aas = align(s, orf);
            System.err.println(orf.getName() + ": " + (aas != null ? aas.size() : 0) + " proteins");            

            if (aas != null) {
                /*for (AaSequence aa:aas) {
                    aa.setNtSequence(seq);
                    seq.getAaSequences().add(aa);
                }*/

                result.addAll(aas);
            }
        }

        return result;
    }

    private List<AaSequence> align(Sequence s, OpenReadingFrame orf) {
        AlignmentResult r=null;
        r = service.alignTo(s, getReferenceSequence(orf));

        if (r != null)
            return convertToAaSequences(orf, r);
        else
            return null;
    }

    private List<AaSequence> convertToAaSequences(OpenReadingFrame orf,
            AlignmentResult aligned) {
        try {
            List<AaSequence> result = new ArrayList<AaSequence>();

            for (Protein protein : orf.getProteins()) {

                if (coversProtein(aligned, protein)) {
                    AaSequence s = new AaSequence();
                    result.add(s);

                    s.setProtein(protein);
                    s.setFirstAaPos((short)Math.max(firstPositionInProtein(aligned, protein), Math.max(1, posInProtein(protein, aligned.getFirstAa()))));
                    s.setLastAaPos((short) Math.min(lastPositionInProtein(aligned, protein), Math.min(getAaLength(protein), posInProtein(protein, aligned.getLastAa()))));

                    Set<AaMutation> mutations = s.getAaMutations();
                    Set<AaInsertion> insertions = s.getAaInsertions();

                    for (Mutation m:aligned.getMutations()) {
                        //System.err.println(m);
                        if (m.getAaPos() >= s.getFirstAaPos() + getFirstAa(protein) - 1
                            && m.getAaPos() <= s.getLastAaPos() + getFirstAa(protein) - 1) {
                            if (m.getInsIndex() == -1) {
                                AaMutation aam = new AaMutation();
                                aam.setId(new AaMutationId((short) posInProtein(protein, m.getAaPos()), s));
                                aam.setAaMutation(asString(m.getTargetAminoAcids()));
                                aam.setAaReference(aatok.tokenizeSymbol(m.getRefAminoAcid()));
                                aam.setNtMutationCodon(asCodonString(nttok, m.getTargetCodon()));
                                aam.setNtReferenceCodon(asCodonString(nttok, m.getRefCodon()));
                                
                                mutations.add(aam);
                            } else if(m.getAaPos() != getLastAa(protein)){
                                AaInsertion aai = new AaInsertion();
                                aai.setId(new AaInsertionId((short) posInProtein(protein, m.getAaPos()), s, (short) m.getInsIndex()));
                                aai.setAaInsertion(asString(m.getTargetAminoAcids()));
                                aai.setNtInsertionCodon(asCodonString(nttok, m.getTargetCodon()));
                                
                                insertions.add(aai);
                            }
                        }
                    }
                 }
            }

            return result;
        } catch (IllegalSymbolException e) {
            throw new RuntimeException(e);
        }
    }
    
    /*
     * Checks if the AlignmentResult covers a (part of a) protein
     * 
     * The method checks the start- and stop position of the aligned sequence (relative to the open reading frame) but also checks if 
     * there are actual AA in the region. The alignment result contains only mutations and no information about the reference so this
     * method checks if there is at least one mutation that is not an insertion or that at least on one position there is no mutation. 
     */
    private boolean coversProtein(AlignmentResult aligned, Protein protein){
    	if ((aligned.getFirstAa() < getLastAa(protein)) && (aligned.getLastAa() > getFirstAa(protein))){
    		int nbMutationsInThisProtein = 0;
    		for (Mutation m:aligned.getMutations()) {
    			if (m.getAaPos() >= getFirstAa(protein) && m.getAaPos() <= getLastAa(protein)) {
    				nbMutationsInThisProtein++;
    				if(!"---".equals(m.getTargetCodon().seqString())){
    					return true;
    				}
    			}
    		}
    		return nbMutationsInThisProtein < getAaLength(protein);
    	}
    	return false;
    }

    /*
     * Returns the first AA position in the protein that doesn't correspond to an insertion.
     */
    private int firstPositionInProtein(AlignmentResult aligned, Protein protein){
    	for(int position = getFirstAa(protein); position <= getLastAa(protein); ++position){
    		boolean mutationAtThisPosition = false;
    		for(Mutation m : aligned.getMutations()){
    			if(m.getAaPos() == position && !"---".equals(m.getTargetCodon().seqString())){
    				return position - getFirstAa(protein) + 1;
    			}else if(m.getAaPos() == position){
    				mutationAtThisPosition = true;
    				break;
    			}
    		}
    		if(!mutationAtThisPosition){
    			return position - getFirstAa(protein) + 1;
    		}
    	}		
    	return -1;
    }

    /*
     * Returns the last AA position in the protein that doesn't correspond to an insertion.
     */
    private int lastPositionInProtein(AlignmentResult aligned, Protein protein){
    	for(int position = getLastAa(protein); position >= getFirstAa(protein); --position){
    		boolean mutationAtThisPosition = false;
    		for(Mutation m : aligned.getMutations()){
    			if(m.getAaPos() == position && !"---".equals(m.getTargetCodon().seqString())){
    				return position - getFirstAa(protein) + 1;
    			}else if(m.getAaPos() == position){
    				mutationAtThisPosition = true;
    				break;
    			}
    		}
    		if(!mutationAtThisPosition){
    			return position - getFirstAa(protein) + 1;
    		}
    	}		
    	return -1;
    }	
    
    private int posInProtein(Protein p, int aa) {
        return aa - p.getStartPosition()/3;
    }
    
    private int getAaLength(Protein p) {
        return (p.getStopPosition() - p.getStartPosition())/3;
    }

    private String asCodonString(SymbolTokenization st, SymbolList codon) {
        try {
            String result = "";
            for (int i = 1; i <= codon.length(); ++i) {
                result += st.tokenizeSymbol(codon.symbolAt(i));
            }
            return result;
        } catch (IllegalSymbolException e) {
            throw new RuntimeException(e);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        }
    }

    private String asString(Set<Symbol> targetAminoAcids) {
        try {
            String result = new String();

            for (Symbol s:targetAminoAcids) {
                result += aatok.tokenizeSymbol(s);
            }
            
            return result;
        } catch (IllegalSymbolException e) {
            throw new RuntimeException(e);
        }
    }
    
    private int getFirstAa(Protein p){
        return (int)Math.ceil(p.getStartPosition()/3.0);
    }
    
    private int getLastAa(Protein p){
        return (int)Math.floor(p.getStopPosition()/3.0);
    }
    
    private static Sequence getReferenceSequence(OpenReadingFrame orf){
        Sequence s = referenceSequences.get(orf);
        if(s == null){
            try {
                s = DNATools.createDNASequence(orf.getReferenceSequence(),orf.getName());
                referenceSequences.put(orf, s);
            } catch (IllegalSymbolException e) {
                e.printStackTrace();
            }
        }
        return s;
    }
}
