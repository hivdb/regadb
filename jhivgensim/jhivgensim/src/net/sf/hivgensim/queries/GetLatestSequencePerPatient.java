package net.sf.hivgensim.queries;


import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;

/**
 * This query returns the latest isolated sequence of each given patient.
 * 
 * @author gbehey0
 *
 */

public class GetLatestSequencePerPatient extends Query<Patient,NtSequence> {

	public GetLatestSequencePerPatient(IQuery<NtSequence> nextQuery){
		super(nextQuery);
	}

	@Override
	public void process(Patient p) {
		NtSequence latestSequenceForThisPatient = null; 
		for(ViralIsolate vi : p.getViralIsolates()){
			for(NtSequence seq : vi.getNtSequences()){
				if(latestSequenceForThisPatient == null || 
						latestSequenceForThisPatient.getSequenceDate().before(seq.getSequenceDate())){
					latestSequenceForThisPatient = seq;
				}						
			}
		}
		if(latestSequenceForThisPatient != null){ //check if patient has sequences in the db
			getNextQuery().process(latestSequenceForThisPatient);
		}
	}
}
