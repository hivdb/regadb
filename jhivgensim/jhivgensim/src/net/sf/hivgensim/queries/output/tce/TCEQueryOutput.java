package net.sf.hivgensim.queries.output.tce;

import java.io.File;
import java.util.List;

import net.sf.hivgensim.queries.framework.TableQueryOutput;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.io.db.drugs.ImportDrugsFromCentralRepos;

public class TCEQueryOutput extends TableQueryOutput<TCE> {
	public TCEQueryOutput(Table out, File file, TableOutputType type) {
		super(out, file, type);
	}

	protected void generateOutput(List<TCE> tces) {
		List<DrugGeneric> genericDrugs = prepareRegaDrugGenerics();
		
		//header
		addColumn("start date");
		addColumn("data source");
		addColumn("patient id");
		
		addColumn("vi id");
		addColumn("vi date");
		addColumn("nt sequence");
		addColumn("subtype");
		
		
		for(TCE tce : tces) {
			
		}
	}

	// TODO
	// standard func
	public static List<DrugGeneric> prepareRegaDrugGenerics() {
		ImportDrugsFromCentralRepos imDrug = new ImportDrugsFromCentralRepos();
		return imDrug.getGenericDrugs();
	}
}
