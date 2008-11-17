package org.sf.hivgensim.queries;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;

import org.sf.hivgensim.queries.framework.Query;
import org.sf.hivgensim.queries.framework.QueryImpl;

public class GetPatientsFromDataset extends QueryImpl<Patient, Patient> {
	
	String dataset;
	
	public GetPatientsFromDataset(Query<Patient> inputQuery, String dataset) {
		super(inputQuery);
		this.dataset = dataset;
	}

	@Override
	public void populateOutputList() {
		for(Patient p : inputQuery.getOutputList()){
			for(Dataset d : p.getDatasets()){
				if(d.getDescription().equals(dataset)){
					outputList.add(p);
					break;
				}
			}
		}
	}
	
	

}
