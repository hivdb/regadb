package org.sf.hivgensim.queries;

import org.sf.hivgensim.queries.framework.Query;
import org.sf.hivgensim.queries.framework.QueryImpl;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;

/**
 * This query returns all the sequences of the given patients
 * @author gbehey0
 *
 */
public class GetAllSequences extends QueryImpl<NtSequence,Patient> {
	
	public GetAllSequences(Query<Patient> query){
		super(query);
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
