package net.sf.hivgensim.queries;

import java.util.ArrayList;
import java.util.Arrays;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;


public class GetPatientsFromDatasets extends Query<Patient, Patient> {
	
	private ArrayList<String> datasets  = new ArrayList<String>();
	
	public GetPatientsFromDatasets(String[] dataset,IQuery<Patient> nextQuery) {
		super(nextQuery);
		datasets.addAll(Arrays.asList(dataset));
	}

	@Override
	public void process(Patient p) {
		for(Dataset d : p.getDatasets()){
			if(datasets.contains(d.getDescription().toLowerCase())){
				getNextQuery().process(p);
				return;
			}
		}		
	}
	
	public void close(){
		super.close();
	}
	
	

}
