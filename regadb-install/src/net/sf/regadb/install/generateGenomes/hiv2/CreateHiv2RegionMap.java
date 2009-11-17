package net.sf.regadb.install.generateGenomes.hiv2;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;
import net.sf.regadb.genbank.Hiv2;
import net.sf.regadb.install.generateGenomes.GenerateGenome;
import net.sf.regadb.install.generateGenomes.Region;
import net.sf.regadb.install.generateGenomes.GenerateGenome.RegionValue;


public class CreateHiv2RegionMap {
	String annotatedAlignedRefSeq;
	
	public CreateHiv2RegionMap(String annotatedAlignedRefSeq) {
		this.annotatedAlignedRefSeq = annotatedAlignedRefSeq;
	}
	
	public static void main(String [] args) {
		CreateHiv2RegionMap chiv2rm = new CreateHiv2RegionMap(Hiv2.benAligned);
        GenerateGenome hiv2benGen = new GenerateGenome("HIV-2-BEN","HIV-2-BEN","",GenerateGenome.getReferenceSequence("NC_001722.fasta"));
        Genome ben = hiv2benGen.generateFromFile("hiv2ben.genome");
		chiv2rm.run(Hiv2.ehoAligned, hiv2benGen, ben);
	}
	
	public void run(String alignedRefSeq, GenerateGenome generateGenome, Genome genome) {
        System.err.println(generateGenome.toString());
        
        if(alignedRefSeq.length()!=annotatedAlignedRefSeq.length()) {
        	throw new RuntimeException("ref seqs should be the same size!!!");
        }

        alignedRefSeq = simplifyAlignments(alignedRefSeq);
        //alignedRefSeq = benAlignedRefSeq;
        
        StringBuilder mapping = new StringBuilder();
        for(OpenReadingFrame orf : genome.getOpenReadingFrames()) {
        	mapping.append("orf\n");
        	mapping.append(orf.getName() + "," + orf.getDescription() + ",");
        	
        	mapping.append(translateRegions(getRegions(orf,generateGenome), alignedRefSeq));
        	mapping.append("\n");
        	
        	for(Protein p : orf.getProteins()){
        		mapping.append(p.getFullName() +","+ p.getAbbreviation() +",");
        		mapping.append(translateRegions(getRegions(p,generateGenome), alignedRefSeq));
        		mapping.append("\n");
        	}
        	
        	mapping.append("\n");
        }
        
        System.out.println(mapping.toString());
	}
	
	private String translateRegions(List<Region> regions, String alignedRefSeq){
		StringBuilder mapping = new StringBuilder();
		
		boolean first=true;
		
		for(Region rv : regions){
    		int startPosBenAligned = toAlignedPos(annotatedAlignedRefSeq, rv.getStart());
    		int startPosRefSeq = toUnAlignedPos(alignedRefSeq, startPosBenAligned);
    		
    		int endPosBenAligned = toAlignedPos(annotatedAlignedRefSeq, rv.getEnd());
    		int endPosRefSeq = toUnAlignedPos(alignedRefSeq, endPosBenAligned);
    		
    		if(first)
    			first = false;
    		else
    			mapping.append('+');
    		mapping.append(startPosRefSeq +"-"+ endPosRefSeq);
		}
		
		return mapping.toString();
	}
	
	private int toAlignedPos(String alignedSeq, int unalignedPos){
		return unalignedPos + countCharBeforeUnalignedPos(alignedSeq, unalignedPos, '-');
	}
	private int toUnAlignedPos(String alignedSeq, int alignedPos){
		return alignedPos - countCharBeforePos(alignedSeq, alignedPos, '-');
	}
	
	private boolean equalsIgnoreMinus(String aligned, String ref) {
		System.err.println(aligned);
		System.err.println(ref);
		return aligned.toLowerCase().replace("-", "").equals(ref);
	}
	
	private int countCharBeforePos(String alignedSeq, int end, char c) {
		return countCharBetweenPos(alignedSeq, 0, end, c);
	}
	private int countCharBetweenPos(String alignedSeq, int start, int numChars, char c) {
		int minusCounter = 0;
		for(int i = 0; i<(numChars); i++) {
			if(alignedSeq.charAt(start+i)==c) 
				minusCounter++;
		}
		return minusCounter;
	}
	
	//this function does not count at the pos location!!!
	private int countCharBeforeUnalignedPos(String alignedSeq, int end, char c) {
		return countCharBetweenUnalignedPos(alignedSeq, 0, end, c);
	}
	private int countCharBetweenUnalignedPos(String alignedSeq, int alignedOffset, int numChars, char c) {
		int minusCounter = 0;
		for(int i = 0; i<(numChars+minusCounter); i++) {
			if(alignedSeq.charAt(alignedOffset+i)==c) 
				minusCounter++;
		}
		return minusCounter;
	}
	
	private List<Region> getRegions(OpenReadingFrame orf, GenerateGenome gen) {
        List<Region> regionValues = new ArrayList<Region>();
		for(RegionValue<OpenReadingFrame> rv : gen.getOrfRegions()) {
        	if(rv.value.getDescription().equals(orf.getDescription()) && rv.value.getName().equals(orf.getName())) {
        		regionValues.add(rv);
        	}
        }
        
        return regionValues;
	}
	private List<Region> getRegions(Protein prot, GenerateGenome gen) {
        List<Region> regionValues = new ArrayList<Region>();
		for(RegionValue<Protein> rv : gen.getProteinRegions()) {
        	if(rv.value.getAbbreviation().equals(prot.getAbbreviation()) && rv.value.getFullName().equals(prot.getFullName())) {
        		regionValues.add(rv);
        	}
        }
        
        return regionValues;
	}
	
	private String simplifyAlignments(String alignedRefSeq) {
		StringBuilder newBen = new StringBuilder();
		StringBuilder newRef = new StringBuilder();
		
		for(int i = 0; i<annotatedAlignedRefSeq.length(); i++) {
			if(!(annotatedAlignedRefSeq.charAt(i)=='-' && alignedRefSeq.charAt(i)=='-')) {
				newBen.append(annotatedAlignedRefSeq.charAt(i));
				newRef.append(alignedRefSeq.charAt(i));
			}
		}
		
		annotatedAlignedRefSeq = newBen.toString();
		return newRef.toString();
	}
}
