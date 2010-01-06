package net.sf.regadb.ui.form.query.querytool.fasta;

import net.sf.regadb.tools.exportFasta.FastaExporter;

import com.pharmadm.custom.rega.queryeditor.OutputVariable;

public class QTFastaExporter extends FastaExporter {
	private OutputVariable output;

	public QTFastaExporter() {
		
	}

	public OutputVariable getOutput() {
		return output;
	}

	public void setOutput(OutputVariable output) {
		this.output = output;
	}
}
