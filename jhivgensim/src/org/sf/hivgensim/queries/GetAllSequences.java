package org.sf.hivgensim.queries;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;

public class GetAllSequences extends QueryImpl<NtSequence,Patient> {
	
	protected GetAllSequences(Query<Patient> query){
		this.inputQuery = query;
	}

	@Override
	protected void populateOutputList() {
		for(Patient p : inputQuery.getOutputList()){
			for(ViralIsolate vi : p.getViralIsolates()){
				for(NtSequence seq : vi.getNtSequences()){
					outputList.add(seq);
				}
			}
		}
		
	}
	

}
