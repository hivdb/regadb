package org.sf.hivgensim.queries;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;

public class GetLatestSequencePerPatient extends QueryImpl<NtSequence,Patient> {
	
	protected GetLatestSequencePerPatient(Query<Patient> query){
		this.inputQuery = query;
	}
	
	@Override
	protected void populateOutputList() {
		NtSequence latestSequenceForThisPatient = null; 
		for(Patient p : inputQuery.getOutputList()){
			for(ViralIsolate vi : p.getViralIsolates()){
				for(NtSequence seq : vi.getNtSequences()){
					if(latestSequenceForThisPatient == null || 
							latestSequenceForThisPatient.getSequenceDate().before(seq.getSequenceDate())){
						latestSequenceForThisPatient = seq;
					}						
				}
			}
			if(latestSequenceForThisPatient != null){ //check if patient has sequences in the db
				outputList.add(latestSequenceForThisPatient);
				latestSequenceForThisPatient = null;
			}
		}
	}
}
