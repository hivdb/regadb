package be.kuleuven.rega.research.gss;

import java.io.File;

import net.sf.hivgensim.queries.framework.TableQueryOutput;
import net.sf.regadb.csv.Table;

public class FailedTherapyOutput extends TableQueryOutput<FailedTherapy> {
	
	public static final String algorithmDescription = "REGA v8.0.2";

	private boolean first = true;
	
	public FailedTherapyOutput(Table out, File file, TableOutputType type) {
		super(out, file, type);
	}

	public void process(FailedTherapy ft) {
		if(first){
			addHeader();
		}
		addColumn(ft.getPatient().getPatientId());
		addColumn(ft.getTherapy().getTherapyMotivation().getValue());
		addColumn(""+ft.getNumberOfPreviousTherapies());
		addColumn(""+ft.getPreGSS(algorithmDescription));
		addColumn(""+ft.getPostGSS(algorithmDescription),true);
	}
	
	private void addHeader() {
		first = false;
		addColumn("patient");
		addColumn("therapy motivation");
		addColumn("nb-of-previous-therapies");
		addColumn("pre-gss");
		addColumn("post-gss",true);
	}

}
