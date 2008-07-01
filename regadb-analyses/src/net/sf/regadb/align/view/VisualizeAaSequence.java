package net.sf.regadb.align.view;

import net.sf.regadb.analysis.functions.AaSequenceHelper;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.genome.HivGenome;

public abstract class VisualizeAaSequence {
    public String getAlignmentView(AaSequence aaseq) {
    	clear();
    	
        HivGenome genome = HivGenome.getHxb2();
        String proteinNt = genome.getNtSequenceForProtein(aaseq.getProtein().getAbbreviation());
                
        AaMutation[] mutations = AaSequenceHelper.getSortedAaMutationArray(aaseq);
        int mutationIndex = 0;
        AaInsertion[] insertions = AaSequenceHelper.getSortedAaInsertionArray(aaseq);
        int insertionIndex = 0;
        
        int codonIndex;
        String mutationCodon;
        for(int i = 0; i<proteinNt.length()/3; i++) {
        	codonIndex = i+1;
        	if(codonIndex==77) {
        		System.err.println("wait");
        	}
        	if(mutations.length != mutationIndex && mutations[mutationIndex].getId().getMutationPosition()==codonIndex) {
                mutationCodon = mutations[mutationIndex].getNtMutationCodon();
                addNt(proteinNt.charAt(i*3), mutationCodon.charAt(0), codonIndex);
                addNt(proteinNt.charAt(i*3+1), mutationCodon.charAt(1), codonIndex);
                addNt(proteinNt.charAt(i*3+2), mutationCodon.charAt(2), codonIndex);
                mutationIndex++;
            } else if(insertions.length != insertionIndex && insertions[insertionIndex].getId().getInsertionPosition()==codonIndex) {
                short pos = insertions[insertionIndex].getId().getInsertionPosition();
                while(insertionIndex!=insertions.length && insertions[insertionIndex].getId().getInsertionPosition()==pos) {
                    mutationCodon = insertions[insertionIndex].getNtInsertionCodon();
                    addNt('-', mutationCodon.charAt(0), codonIndex);
                    addNt('-', mutationCodon.charAt(1), codonIndex);
                    addNt('-', mutationCodon.charAt(2), codonIndex);
                    
                    insertionIndex++;
                }
            } else {
            	if(codonIndex<aaseq.getFirstAaPos() || codonIndex>aaseq.getLastAaPos()) {
                    addNt(proteinNt.charAt(i*3), '-', codonIndex);
                    addNt(proteinNt.charAt(i*3+1), '-', codonIndex);
                    addNt(proteinNt.charAt(i*3+2), '-', codonIndex);
            	} else {
                    addNt(proteinNt.charAt(i*3), proteinNt.charAt(i*3), codonIndex);
                    addNt(proteinNt.charAt(i*3+1), proteinNt.charAt(i*3+1), codonIndex);
                    addNt(proteinNt.charAt(i*3+2), proteinNt.charAt(i*3+2), codonIndex);
            	}
            }
        }
        
        end();
        
        return getStringRepresentation();
    }
    
    public abstract void addNt(char reference, char target, int codonIndex);
    
    public abstract String getStringRepresentation();
    
    public abstract void clear();
    
    public abstract void end();
}