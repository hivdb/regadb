package net.sf.regadb.tools.exportFasta;

import net.sf.regadb.align.view.VisualizeAaSequence;
import net.sf.regadb.analysis.functions.AaSequenceHelper;

public class ExportAaSequence extends VisualizeAaSequence {
	private StringBuffer sequence = new StringBuffer();

	private StringBuffer codon = new StringBuffer();
	
	private FastaExporter.Symbol symbol;
	private boolean aligned;
	private boolean insertions;
	
	public ExportAaSequence(FastaExporter.Symbol symbol, boolean aligned, boolean insertions) {
		this.symbol = symbol;
		this.aligned = aligned;
		this.insertions = insertions;
	}

	public void addNt(char reference, char target, int codonIndex, boolean insertion) {
		if (!insertions && insertion)
			return;
			
		codon.append(target);
		if(codon.length()==3) {
			addAa();
		}
	}

	private void addAa() {
		if (symbol == FastaExporter.Symbol.Nucleotides) {
			sequence.append(getCodon());
		} else /*AminoAcids*/ {
			sequence.append(getAminoAcid());
		}

		codon.delete(0, 3);
	}
	
	private String getCodon() {
		if (aligned)
			return codon.toString();
		else
			return codon.toString().replace("-", "");
	}
	
	private String getAminoAcid() {
		String aa = AaSequenceHelper.getAminoAcid(codon.toString()).trim();
		if (aligned)
			return aa;
		else 
			return aa.replace("-", "");
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