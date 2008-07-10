package net.sf.regadb.align.view;

import java.util.Iterator;
import java.util.TreeSet;

import net.sf.regadb.analysis.functions.AaSequenceHelper;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.SplicingPosition;

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
    
    protected String getNtSequenceForProtein(Protein protein){
        String ntSequence="";
        String pSeq = protein.getOpenReadingFrame().getReferenceSequence().substring(protein.getStartPosition()-1,protein.getStopPosition()-1);
        
        TreeSet<Integer> ts = new TreeSet<Integer>();
        
        for(SplicingPosition sp : protein.getSplicingPositions())
            ts.add(sp.getPosition());
        
        ts.add(1);
        for(Integer sp : ts){
            String sSeq;
            if(ts.tailSet(sp).size() > 1){
                Iterator<Integer> it = ts.tailSet(sp).iterator();
                it.next();
                sSeq = pSeq.substring(sp-1,it.next());
            }
            else{
                sSeq = pSeq.substring(sp-1,pSeq.length());
            }
            ntSequence += sSeq;
        }
        
        return ntSequence;
    }
    
    public abstract void addNt(char reference, char target, int codonIndex);
    
    public abstract String getStringRepresentation();
    
    public abstract void clear();
    
    public abstract void end();
}