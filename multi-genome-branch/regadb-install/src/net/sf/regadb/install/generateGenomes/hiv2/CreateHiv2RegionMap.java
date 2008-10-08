package net.sf.regadb.install.generateGenomes.hiv2;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.genbank.Hiv2;
import net.sf.regadb.install.generateGenomes.GenerateGenome;
import net.sf.regadb.install.generateGenomes.GenerateGenome.RegionValue;


public class CreateHiv2RegionMap {
	String benAlignedRefSeq = Hiv2.benAligned;
	
	public static void main(String [] args) {
		CreateHiv2RegionMap chiv2rm = new CreateHiv2RegionMap();
		chiv2rm.run(Hiv2.ehoAligned);
	}
	
	public void run(String alignedRefSeq) {
        GenerateGenome hiv2benGen = new GenerateGenome("HIV-2-BEN","HIV-2-BEN","",GenerateGenome.getReferenceSequence("NC_001722.fasta"));
        Genome ben = hiv2benGen.generateFromFile("hiv2ben.genome");
        
        if(alignedRefSeq.length()!=benAlignedRefSeq.length()) {
        	throw new RuntimeException("ref seqs should be the same size!!!");
        }

        alignedRefSeq = simplifyAlignments(alignedRefSeq);
        
        StringBuilder mapping = new StringBuilder();
        for(OpenReadingFrame orf : ben.getOpenReadingFrames()) {
        	mapping.append("orf");
        	mapping.append(orf.getName() + "," + orf.getDescription() + ",");
        	List<RegionValue<OpenReadingFrame>> values = getRegionValues(orf, hiv2benGen);
        	for(RegionValue<OpenReadingFrame> rv : values) {
        		int addToStartPos = countCharBeforePos(benAlignedRefSeq, rv.getStart(), '-');
//        		/addToStartPos += countCharBetween(benAlignedRefSeq, rv.getStart()+addToStartPos, '-');
        		int addToEndPos = addToStartPos;
        		String strNoCorrectEnding = benAlignedRefSeq.substring(rv.getStart()+addToStartPos, rv.getEnd()+addToEndPos);
        		addToEndPos += countChar(strNoCorrectEnding, '-');
//        		System.err.println(benAlignedRefSeq.substring(rv.getStart()+addToStartPos, rv.getEnd()+addToEndPos));
//        		System.err.println(orf.getReferenceSequence());
        		System.err.println(orf.getName());
        		System.err.println(equalsIgnoreMinus(benAlignedRefSeq.substring(rv.getStart()+addToStartPos-1, rv.getEnd()+addToEndPos-1),orf.getReferenceSequence()));
        	}
        }
	}
	
	private boolean equalsIgnoreMinus(String aligned, String ref) {
		System.err.println(aligned);
		System.err.println(ref);
		return aligned.toLowerCase().replace("-", "").equals(ref);
	}
	
	private int countCharBetween(String alignedSeq, int startPosition, char delim) {
		int minusCounter = 0;
		for(int i = startPosition; i<alignedSeq.length(); i++) {
			if(alignedSeq.charAt(i)==delim) 
				minusCounter++;
			else 
				return minusCounter;
		}
		return minusCounter;
	}
	
	//this function does not count at the pos location!!!
	private int countCharBeforePos(String alignedSeq, int pos, char c) {
		int minusCounter = 0;
		for(int i = 0; i<(pos+minusCounter); i++) {
			if(alignedSeq.charAt(i)==c) 
				minusCounter++;
		}
		return minusCounter;
	}
	
	private int countChar(String alignedSeq, char c) {
		int minusCounter = 0;
		for(int  i = 0 ; i<alignedSeq.length(); i++) {
			if(alignedSeq.charAt(i)==c) 
				minusCounter++;
		}
		return minusCounter;
	}
	
	private List<RegionValue<OpenReadingFrame>> getRegionValues(OpenReadingFrame orf, GenerateGenome gen) {
        List<RegionValue<OpenReadingFrame>> regionValues = new ArrayList<RegionValue<OpenReadingFrame>>();
		for(RegionValue<OpenReadingFrame> rv : gen.getOrfRegions()) {
        	if(rv.value.getDescription().equals(orf.getDescription()) && rv.value.getName().equals(orf.getName())) {
        		regionValues.add(rv);
        	}
        }
        
        return regionValues;
	}
	
	private String simplifyAlignments(String alignedRefSeq) {
		StringBuilder newBen = new StringBuilder();
		StringBuilder newRef = new StringBuilder();
		
		for(int i = 0; i<benAlignedRefSeq.length(); i++) {
			if(!(benAlignedRefSeq.charAt(i)=='-' && alignedRefSeq.charAt(i)=='-')) {
				newBen.append(benAlignedRefSeq.charAt(i));
				newRef.append(alignedRefSeq.charAt(i));
			}
		}
		
		benAlignedRefSeq = newBen.toString();
		return newRef.toString();
	}
}
