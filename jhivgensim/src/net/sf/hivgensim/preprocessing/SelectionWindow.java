package net.sf.hivgensim.preprocessing;

import java.util.Set;
import java.util.TreeSet;

import net.sf.hivgensim.queries.framework.utils.AaSequenceUtils;
import net.sf.hivgensim.queries.framework.utils.NtSequenceUtils;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Protein;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;

public class SelectionWindow {
	
	/**
     * @deprecated
     * use constructor
     */
	public static SelectionWindow getWindow(String organism, String orf, String protein){
		return new SelectionWindow(organism,orf,protein);
	}
	
	public static final SelectionWindow PR_WINDOW_CLEAN = new SelectionWindow("HIV-1", "pol", "PR",10,95);
	public static final SelectionWindow RT_WINDOW_CLEAN = new SelectionWindow("HIV-1", "pol", "RT",44,200);
	
	public static final SelectionWindow PR_WINDOW_REGION = new SelectionWindow("HIV-1", "pol", "PR");
	public static final SelectionWindow RT_WINDOW_REGION = new SelectionWindow("HIV-1", "pol", "RT");
	
	public static final SelectionWindow HIV_2_P6_WINDOW = new SelectionWindow("HIV-2A", "gag", "p6");
	public static final SelectionWindow HIV_2_PR_WINDOW = new SelectionWindow("HIV-2A", "pol", "PR");
	public static final SelectionWindow HIV_2_RT_WINDOW = new SelectionWindow("HIV-2A", "pol", "RT");
	
	private String organism;
	private String proteinAbbreviation;
	private Protein protein;
	
	private int start;
	private int stop;
	
	private String referenceAaSequence = null;
	private String referenceNtSequence = null;
	
	public SelectionWindow(String organism, String orf, String proteinAbbreviation){
		this.organism = organism;
		this.proteinAbbreviation = proteinAbbreviation;
		this.protein = Utils.getProtein(organism, orf, proteinAbbreviation);
		this.start = 1;
		this.stop = (protein.getStopPosition() - protein.getStartPosition())/3; 
	}
	
	public SelectionWindow(String organism, String orf, String protein, int start, int stop){
		this(organism,orf,protein);
		this.start = start;
		this.stop = stop;
	}	
	
	public String getReferenceNtSequence(){
		if(referenceNtSequence != null){
			return referenceNtSequence;
		}		
		String ntseq = protein.getOpenReadingFrame().getReferenceSequence().substring(getStartCheck(),getStopCheck());
		referenceNtSequence = ntseq;
		return ntseq;
	}
	
	public String getReferenceAaSequence(){
		if(referenceAaSequence != null){
			return referenceAaSequence;
		}
		try {			
			String aaseq = RNATools.translate(DNATools.toRNA(DNATools.createDNA(getReferenceNtSequence()))).seqString();
			this.referenceAaSequence = aaseq;
			return aaseq;
		} catch (IllegalAlphabetException e) {
			e.printStackTrace();
		} catch (IllegalSymbolException e) {
			e.printStackTrace();
		}
		return "";
	}

	public Protein getProtein() {
		return protein;
	}
	public void setProtein(Protein protein) {
		this.protein = protein;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getStop() {
		return stop;
	}
	public void setStop(int stop) {
		this.stop = stop;
	}

	//starting point of this window relative to the start of the ORF in the NT sequence
	public int getStartCheck(){
		return getProtein().getStartPosition()+3*(getStart()-1)-1;
	}

	//stopping point of this window relative to the start of the ORF in the NT sequence
	public int getStopCheck(){
		return getProtein().getStartPosition()+3*(getStop())-1;
	}
	
	public boolean contains(int proteinPosition){
		return getStart() <= proteinPosition && getStop() >= proteinPosition;
	}
	
	public boolean contains(AaMutation mut){
		return contains(mut.getId().getMutationPosition());
	}
	public Set<String> getAllMutations() {
		Set<String> mutations = new TreeSet<String>();
		for(int pos = getStart(); pos <= getStop(); pos++){
			mutations.add(getProtein().getAbbreviation()+pos+"A");
			mutations.add(getProtein().getAbbreviation()+pos+"C");
			mutations.add(getProtein().getAbbreviation()+pos+"D");
			mutations.add(getProtein().getAbbreviation()+pos+"E");
			mutations.add(getProtein().getAbbreviation()+pos+"F");
			mutations.add(getProtein().getAbbreviation()+pos+"G");
			mutations.add(getProtein().getAbbreviation()+pos+"H");
			mutations.add(getProtein().getAbbreviation()+pos+"I");
			mutations.add(getProtein().getAbbreviation()+pos+"K");
			mutations.add(getProtein().getAbbreviation()+pos+"L");
			mutations.add(getProtein().getAbbreviation()+pos+"M");
			mutations.add(getProtein().getAbbreviation()+pos+"N");
			mutations.add(getProtein().getAbbreviation()+pos+"P");
			mutations.add(getProtein().getAbbreviation()+pos+"Q");
			mutations.add(getProtein().getAbbreviation()+pos+"R");
			mutations.add(getProtein().getAbbreviation()+pos+"S");
			mutations.add(getProtein().getAbbreviation()+pos+"T");
			mutations.add(getProtein().getAbbreviation()+pos+"V");
			mutations.add(getProtein().getAbbreviation()+pos+"W");
			mutations.add(getProtein().getAbbreviation()+pos+"Y");
			mutations.add(getProtein().getAbbreviation()+pos+"ins");
			mutations.add(getProtein().getAbbreviation()+pos+"del");
		}
		return mutations;
	}
	
	public boolean isAcceptable(NtSequence seq){
		return NtSequenceUtils.coversRegion(seq, organism, proteinAbbreviation, start, stop);
	}
	
	public boolean isAcceptable(AaSequence seq){
		return AaSequenceUtils.coversRegion(seq, organism, proteinAbbreviation, start, stop);
	}
	
}
