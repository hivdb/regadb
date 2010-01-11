package net.sf.regadb.tools.exportFasta;

import net.sf.regadb.align.view.VisualizeAaSequence;
import net.sf.regadb.analysis.functions.AaSequenceHelper;

public class ExportAaSequence extends VisualizeAaSequence {
	private StringBuffer sequence = new StringBuffer();

	private StringBuffer codon = new StringBuffer();
	
	private FastaExporter.Symbol symbol;
	
	public ExportAaSequence(FastaExporter.Symbol symbol) {
		this.symbol = symbol;
	}

	public void addNt(char reference, char target, int codonIndex) {
		codon.append(target);
		if(codon.length()==3) {
			addAa();
		}
	}

	private void addAa() {
		if (symbol == FastaExporter.Symbol.Nucleotides) {
			sequence.append(codon);
		} else /*AminoAcids*/ {
			sequence.append(AaSequenceHelper.getAminoAcid(codon.toString()).trim());
		}

		codon.delete(0, 3);
	}

	private void endOfAlignment() {

	}

	private void appendLineToPage(StringBuffer b) {

	}

	public void clear() {
		sequence.delete(0, sequence.length());
		codon.delete(0, 3);
	}

	public String getStringRepresentation() {
		return sequence.toString();
	}

	public void end() {

	}
}