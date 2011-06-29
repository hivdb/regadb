package net.sf.regadb.align.view;

import net.sf.regadb.analysis.functions.AaSequenceHelper;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Protein;

public abstract class VisualizeAaSequence {
    public String getAlignmentView(AaSequence aaseq) {
    	clear();
    	
        String proteinNt = getNtSequenceForProtein(aaseq.getProtein());
                
        AaMutation[] mutations = AaSequenceHelper.getSortedAaMutationArray(aaseq);
        int mutationIndex = 0;
        AaInsertion[] insertions = AaSequenceHelper.getSortedAaInsertionArray(aaseq);
        int insertionIndex = 0;
        
        int codonIndex;
        String mutationCodon;
        for(int i = 0; i<proteinNt.length()/3; i++) {
        	codonIndex = i+1;
    		if(mutations.length != mutationIndex && mutations[mutationIndex].getId().getMutationPosition()==codonIndex){
                mutationCodon = mutations[mutationIndex].getNtMutationCodon();
                addNt(proteinNt.charAt(i*3), mutationCodon.charAt(0), codonIndex, false);
                addNt(proteinNt.charAt(i*3+1), mutationCodon.charAt(1), codonIndex, false);
                addNt(proteinNt.charAt(i*3+2), mutationCodon.charAt(2), codonIndex, false);
                mutationIndex++;
            } else {
            	if(codonIndex<aaseq.getFirstAaPos() || codonIndex>aaseq.getLastAaPos()) {
                    addNt(proteinNt.charAt(i*3), '-', codonIndex, false);
                    addNt(proteinNt.charAt(i*3+1), '-', codonIndex, false);
                    addNt(proteinNt.charAt(i*3+2), '-', codonIndex, false);
            	} else {
                    addNt(proteinNt.charAt(i*3), proteinNt.charAt(i*3), codonIndex, false);
                    addNt(proteinNt.charAt(i*3+1), proteinNt.charAt(i*3+1), codonIndex, false);
                    addNt(proteinNt.charAt(i*3+2), proteinNt.charAt(i*3+2), codonIndex, false);
            	}
            }
    		if(insertions.length != insertionIndex && insertions[insertionIndex].getId().getInsertionPosition()==codonIndex) {
                short pos = insertions[insertionIndex].getId().getInsertionPosition();
                while(insertionIndex!=insertions.length && insertions[insertionIndex].getId().getInsertionPosition()==pos) {
                    mutationCodon = insertions[insertionIndex].getNtInsertionCodon();
                    addNt('-', mutationCodon.charAt(0), codonIndex, true);
                    addNt('-', mutationCodon.charAt(1), codonIndex, true);
                    addNt('-', mutationCodon.charAt(2), codonIndex, true);
                    
                    insertionIndex++;
                }
    		}
        }
        
        end();
        
        return getStringRepresentation();
    }
    
    protected String getNtSequenceForProtein(Protein protein){
        return protein.getOpenReadingFrame().getReferenceSequence().substring(protein.getStartPosition()-1,protein.getStopPosition()-1);
    }
    
    public abstract void addNt(char reference, char target, int codonIndex, boolean insertion);
    
    public abstract String getStringRepresentation();
    
    public abstract void clear();
    
    public abstract void end();
}