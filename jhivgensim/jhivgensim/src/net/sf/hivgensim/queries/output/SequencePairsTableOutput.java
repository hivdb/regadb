package net.sf.hivgensim.queries.output;

import java.io.File;
import java.text.SimpleDateFormat;

import net.sf.hivgensim.queries.framework.TableQueryOutput;
import net.sf.hivgensim.queries.framework.datatypes.SequencePair;
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
			addColumn("source");
			addColumn("nt_sequence_ii_1");
			addColumn("nt_sequence_ii_2");
			addColumn("sample_id_1");
			addColumn("sample_id_2");
			addColumn("sample_date_1");
			addColumn("sample_date_2");
			for(String s : p.getDrugs()){
				addColumn("baseline_resistance_"+s);
			}
			for(String s : p.getDrugs()){
				addColumn("followup_resistance_"+s);
			}
			addColumn("therapy regimen");
			addColumn("sequence_1");
			addColumn("sequence_2",true);
			first = false;
		}
		addColumn(p.getPatient().getPatientId());
		addColumn(p.getPatient().getDatasets().iterator().next().getDescription());
		addColumn(String.valueOf(p.getSeq1().getNtSequenceIi()));
		addColumn(String.valueOf(p.getSeq2().getNtSequenceIi()));
		addColumn(p.getSeq1().getViralIsolate().getSampleId());
		addColumn(p.getSeq2().getViralIsolate().getSampleId());
		addColumn(format.format(p.getSeq1().getViralIsolate().getSampleDate()));
		addColumn(format.format(p.getSeq2().getViralIsolate().getSampleDate()));
		for(String s : p.getDrugs()){
			addColumn(p.getNaiveResistance(s));
		}
		for(String s : p.getDrugs()){
			addColumn(p.getTreatedResistance(s));
		}
		addColumn(p.getTherapyRegimen());
		addColumn(p.getSeq1().getNucleotides());
		addColumn(p.getSeq2().getNucleotides(),true);
		if(getOut().numRows() > 4){
			this.close();
			System.exit(0);
		}
	}	
}