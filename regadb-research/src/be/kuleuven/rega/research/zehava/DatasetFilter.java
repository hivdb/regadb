package be.kuleuven.rega.research.zehava;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.regadb.db.Patient;

public class DatasetFilter extends Query<Patient,Patient> {

	protected DatasetFilter(IQuery<Patient> nextQuery) {
		super(nextQuery);
	}

	public void process(Patient input) {
		String ds = input.getDatasets().iterator().next().getDescription();
		if(ds.equals("EgazMoniz") || ds.equals("VIROLAB_LEUVEN")){
			getNextQuery().process(input);
		}
	}

}
