package net.sf.hivgensim.queries.output;

import java.io.File;
import java.text.SimpleDateFormat;

import net.sf.hivgensim.queries.framework.SequencePair;
import net.sf.hivgensim.queries.framework.TableQueryOutput;
import net.sf.regadb.csv.Table;

public class SequencePairsTableOutput extends TableQueryOutput<SequencePair> {

	private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	private boolean first = true;

	public SequencePairsTableOutput(Table out, File file, TableOutputType type) {
		super(out, file, type);
	}	

	public void process(SequencePair p) {
		if(first){
			addColumn("patient_id");
			addColumn("sample_id_1");
			addColumn("sample_id_2");
			addColumn("sample_date_1");
			addColumn("sample_date_2");
			addColumn("therapy regimen");
			addColumn("sequence_1");
			addColumn("sequence_2",true);
			first = false;
		}
		addColumn(p.getPatientId());
		addColumn(p.getSeq1().getViralIsolate().getSampleId());
		addColumn(p.getSeq2().getViralIsolate().getSampleId());
		addColumn(format.format(p.getSeq1().getViralIsolate().getSampleDate()));
		addColumn(format.format(p.getSeq2().getViralIsolate().getSampleDate()));
		addColumn(p.getTherapyRegimen());
		addColumn(p.getSeq1().getNucleotides());
		addColumn(p.getSeq2().getNucleotides(),true);
	}
	
	public void close(){
		super.close();
	}

}