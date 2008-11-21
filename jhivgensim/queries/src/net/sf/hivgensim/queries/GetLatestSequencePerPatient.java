package net.sf.hivgensim.queries;


import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryImpl;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;

/**
 * This query returns the latest isolated sequence of each given patient.
 * 
 * @author gbehey0
 *
 */

public class GetLatestSequencePerPatient extends QueryImpl<NtSequence,Patient> {
	
	public GetLatestSequencePerPatient(Query<Patient> query){
		super(query);
	}
	
	@Override
	public void populateOutputList() {
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
