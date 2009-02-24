package net.sf.hivgensim.fastatool;

import net.sf.regadb.db.Protein;

public class SelectionWindow {
	
	private Protein protein;
	private int start;
	private int stop;
	
	public SelectionWindow(Protein protein){
		this(protein,1,protein.getStopPosition()-protein.getStartPosition());
	}
	public SelectionWindow(Protein protein, int start, int stop){
		this.protein = protein;
		this.start = start;
		this.stop = stop;
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
	
	public int getStartCheck(){
		return getProtein().getStartPosition()+3*(getStart()-1)-1;
	}
	
	public int getStopCheck(){
		return getProtein().getStartPosition()+3*(getStop())-1;
	}

}
