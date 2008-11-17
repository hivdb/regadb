package org.sf.hivgensim.queries.framework;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Patient;

public abstract class QueryInput implements Query<Patient> {
	
	protected List<Patient> outputList;
	protected abstract void populateOutputList();
	
	public List<Patient> getOutputList(){
		if(outputList == null){
			outputList = new ArrayList<Patient>();
			populateOutputList();
		}
		return outputList;
	}
	
	
		
	
	

}
