package be.kuleuven.rega.research.gss;

import java.io.File;
import java.util.List;

import net.sf.hivgensim.queries.framework.TableQueryOutput;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugGeneric;

public class FailedTherapyOutput extends TableQueryOutput<FailedTherapy> {
	
	public static final String algorithmDescription = "REGA v8.0.2";
	public static final List<DrugGeneric> drugs = DrugGenericUtils.getDrugsSortedOnResistanceRanking(DrugGenericUtils.prepareRegaDrugGenerics(),true);
	private boolean first = true;
	
	public FailedTherapyOutput(Table out, File file, TableOutputType type) {
		super(out, file, type);
	}

	public void process(FailedTherapy ft) {
		if(first){
			addHeader();
		}
		addColumn(ft.getPatient().getPatientId());
		addColumn(ft.getPatient().getDatasets().iterator().next().getDescription());
		if(ft.getTherapy().getTherapyMotivation() == null)
			addColumn("");
		else
			addColumn(ft.getTherapy().getTherapyMotivation().getValue().replace(",", ""));
		addColumn(""+ft.getNumberOfPreviousTherapies());
		List<String> regimen = ft.getDrugNames();
		for(DrugGeneric dg : drugs){
			if(regimen.contains(dg.getGenericId())){
				addColumn("TRUE");
			} else {
				addColumn("FALSE");
			}
		}
		addColumn(""+ft.getPreGSS(algorithmDescription));
		addColumn(""+ft.getPostGSS(algorithmDescription),true);
	}
	
	private void addHeader() {
		first = false;
		addColumn("patient");
		addColumn("dataset");
		addColumn("therapy motivation");
		addColumn("nb-of-previous-therapies");
		for(DrugGeneric dg : drugs){
			addColumn(dg.getGenericId());
		}
		addColumn("pre-gss");
		addColumn("post-gss",true);
		
	}

}
