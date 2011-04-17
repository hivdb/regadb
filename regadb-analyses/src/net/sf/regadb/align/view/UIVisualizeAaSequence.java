package net.sf.regadb.align.view;

import net.sf.regadb.analysis.functions.AaSequenceHelper;

public class UIVisualizeAaSequence extends VisualizeAaSequence {
	public static final int LINE_SIZE = 81;

	private StringBuffer page = new StringBuffer();

	private StringBuffer refAa = new StringBuffer();
	private StringBuffer refNt = new StringBuffer();
	private StringBuffer diff = new StringBuffer();
	private StringBuffer tarNt = new StringBuffer();
	private StringBuffer tarAa = new StringBuffer();

	private StringBuffer refCodon = new StringBuffer();
	private StringBuffer tarCodon = new StringBuffer();
	
	private int aaCounter = 0;
	private int lineCounter = 0;
	
	private int prevRefAaCounter = 0;
	private int refAaCounter = 0;
	
	private int prevRefNtCounter = 0;
	private int refNtCounter = 0;


	private static final String newLine = "<br/>";

	public void addNt(char reference, char target, int codonIndex, boolean insertion) {
		if (reference == target || target == '-') {
			refNt.append(reference);
			tarNt.append(target);
		} else {
			refNt.append("<font color=\"red\">" + reference + "</font>");
			tarNt.append("<font color=\"red\">" + target + "</font>");
		}

        refCodon.append(reference);
        tarCodon.append(target);
        
		diff.append(reference == target ? '|' : ' ');
		
		if(reference != '-')
			++refNtCounter;
		
        if(refCodon.length()==3) {
        	addAa();
        }
	}

	private void addAa() {
		String ref = AaSequenceHelper.getAminoAcid(refCodon.toString());
		String tar = AaSequenceHelper.getAminoAcid(tarCodon.toString());
		if (ref.equals(tar) || tar.toString().equals(" - ")) {
			refAa.append(ref);
			tarAa.append(tar);
		} else {
			refAa.append("<font color=\"red\">" + ref + "</font>");
			tarAa.append("<font color=\"red\">" + tar + "</font>");
		}

		refCodon.delete(0, 3);
		tarCodon.delete(0, 3);
		aaCounter++;
		
		if(!" - ".equals(ref))
			++refAaCounter;

		if (aaCounter == (LINE_SIZE / 3.0)) {
			endOfAlignment();
		}
	}

	private void endOfAlignment() {
		page.append("Going from " + (prevRefNtCounter+1) + " to " + refNtCounter 
				+ " (" + (prevRefAaCounter+1) + " to " + refAaCounter + ")" + newLine);

		appendLineToPage(refAa);
		appendLineToPage(refNt);
		appendLineToPage(diff);
		appendLineToPage(tarNt);
		appendLineToPage(tarAa);

		aaCounter = 0;
		prevRefAaCounter = refAaCounter;
		prevRefNtCounter = refNtCounter;
		lineCounter++;
	}

	private void appendLineToPage(StringBuffer b) {
		page.append(b + newLine);
		b.delete(0, b.length());
	}

	public void clear() {
		if (page.length() != 0) {
			page.delete(0, page.length());
		}

		aaCounter = 0;
		lineCounter = 0;
	}

	public String getStringRepresentation() {
		return page.toString();
	}

	public void end() {
		if(refAa.length()!=0)
			endOfAlignment();
		
		prevRefAaCounter = refAaCounter = 0;
		prevRefNtCounter = refNtCounter = 0;
	}
}
