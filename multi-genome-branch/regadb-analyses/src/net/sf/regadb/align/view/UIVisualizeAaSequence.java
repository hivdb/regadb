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

	private static final String newLine = "<br/>";

	public void addNt(char reference, char target, int codonIndex) {
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

		if (aaCounter == (LINE_SIZE / 3.0)) {
			endOfAlignment();
		}
	}

	private void endOfAlignment() {
		int fromNt = ((LINE_SIZE * lineCounter) + 1);
		int toNt = fromNt + (aaCounter * 3) - 1;
		int fromAa = (((LINE_SIZE / 3) * lineCounter) + 1);
		int toAa = fromAa + aaCounter - 1;

		page.append("Going from " + fromNt + " to " + toNt + " (" + fromAa
				+ " to " + toAa + ")" + newLine);

		appendLineToPage(refAa);
		appendLineToPage(refNt);
		appendLineToPage(diff);
		appendLineToPage(tarNt);
		appendLineToPage(tarAa);

		aaCounter = 0;
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
	}
}
