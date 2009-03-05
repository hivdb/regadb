package net.sf.hivgensim.fastatool;

import net.sf.hivgensim.preprocessing.Utils;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.Protein;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;

public class SelectionWindow {
	/*
	 * Because the HIVGENSIM calculations/programs/functions use a fixed length of sequences and assumes these are 
	 * (complete) PR or/and RT sequences we only allow 4 windows. Later we can allow the user to customize the start and stop points 
	 * of the windows but at the moment this would cause problems identifying the positions in the sequences further downstream
	 * in the program.
	 *  
	 */
	public static final SelectionWindow PR_WINDOW_CLEAN = new SelectionWindow(Utils.getProtein("HIV-1", "pol", "PR"),10,95);
	public static final SelectionWindow RT_WINDOW_CLEAN = new SelectionWindow(Utils.getProtein("HIV-1", "pol", "PR"),44,200);
	
	public static final SelectionWindow PR_WINDOW_REGION = new SelectionWindow(Utils.getProtein("HIV-1", "pol", "PR"));
	public static final SelectionWindow RT_WINDOW_REGION = new SelectionWindow(Utils.getProtein("HIV-1", "pol", "PR"));
	
	
	private Protein protein;
	//starting point of this window relative to the start of the protein in the AA sequence
	private int start;
	//stopping point of this window relative to the start of the protein in the AA sequence
	private int stop;
	
	private SelectionWindow(Protein protein){
		this(protein,1,protein.getStopPosition()-protein.getStartPosition());
	}
	private SelectionWindow(Protein protein, int start, int stop){
		this.protein = protein;
		this.start = start;
		this.stop = stop;
	}

	public String getReferenceAaSequence(){
		try {
			String ntseq = protein.getOpenReadingFrame().getReferenceSequence().substring(getStartCheck(),getStopCheck());
			String aaseq = RNATools.translate(DNATools.toRNA(DNATools.createDNA(ntseq))).seqString();
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
	
}
