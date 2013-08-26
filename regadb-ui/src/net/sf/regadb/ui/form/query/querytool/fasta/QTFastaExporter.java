package net.sf.regadb.ui.form.query.querytool.fasta;

import net.sf.regadb.io.export.fasta.FastaExporter;

import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.Query.FastaExport;

public class QTFastaExporter extends FastaExporter implements FastaExport {
	private OutputVariable output;

	public QTFastaExporter() {
		
	}

	public OutputVariable getOutput() {
		return output;
	}

	public void setOutput(OutputVariable output) {
		this.output = output;
	}

	public OutputVariable getViralIsolate() {
		return output;
	}
}
