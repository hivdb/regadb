package net.sf.hivgensim.queries;


import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;


/**
 * This query returns all the sequences of the given patients
 * @author gbehey0
 *
 */
public class GetAllSequences extends Query<Patient,NtSequence> {

	public GetAllSequences(IQuery<NtSequence> nextQuery){
		super(nextQuery);
	}

	@Override
	public void process(Patient p) {
		for(ViralIsolate vi : p.getViralIsolates()){
			for(NtSequence seq : vi.getNtSequences()){
				getNextQuery().process(seq);
			}
		}
	}
}
