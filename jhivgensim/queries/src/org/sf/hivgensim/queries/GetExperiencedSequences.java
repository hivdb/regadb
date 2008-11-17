package org.sf.hivgensim.queries;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.sf.hivgensim.queries.framework.Query;
import org.sf.hivgensim.queries.framework.QueryImpl;
import org.sf.hivgensim.queries.framework.QueryUtils;
import org.sf.hivgensim.queries.framework.QueryInput;
import org.sf.hivgensim.queries.framework.QueryOutput;
import org.sf.hivgensim.queries.input.FromDatabase;
import org.sf.hivgensim.queries.output.ToMutationTable;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;

/**
 * This query returns a list of NtSequences
 * Each sequence is the latest of a therapy that included a drug class in therapyTypes
 * 
 * 
 * @author gbehey0
 *
 */

public class GetExperiencedSequences extends QueryImpl<NtSequence, Patient> {

	private String[] druggenerics = new String[]{"AZT","3TC"};

	public GetExperiencedSequences(Query<Patient> inputQuery) {
		super(inputQuery);
	}

	public GetExperiencedSequences(Query<Patient> query, String[] druggenerics){
		super(query);
		this.druggenerics = druggenerics;		
	}

	@Override
	protected void populateOutputList() {
		Set<NtSequence> temp = new HashSet<NtSequence>();
		for(Patient p : inputQuery.getOutputList()){
			Therapy latestGoodExperienceTherapy = null;
			for(Therapy t : QueryUtils.sortTherapies(p.getTherapies())){
				if(QueryUtils.isGoodExperienceTherapy(t, druggenerics)){
					latestGoodExperienceTherapy = t;
				}else{
					//check if this therapy uses drugs in same drug class of
					//wanted drug combination
					//if such a therapy has been followed break loop and use
					//latestGoodExperienceTherapy as latest therapy to extract sequence
					if(!QueryUtils.isGoodPreviousTherapy(t, druggenerics)){
						break;
					}
				}

			}
			if(latestGoodExperienceTherapy != null){
				Set<NtSequence> seqs = QueryUtils.getLatestExperiencedSequences(p,latestGoodExperienceTherapy);
				if(seqs != null)
					temp.addAll(seqs);
			}						
		}
		outputList.addAll(temp);
	}

	public static void main(String[] args){
		QueryInput qi = new FromDatabase("gbehey0","bla123");
		Query<NtSequence> q = new GetExperiencedSequences(qi,new String[]{"AZT","3TC"});
		QueryOutput<NtSequence> qo = new ToMutationTable(new File("/home/gbehey0/queries/test2"));
		qo.generateOutput(q);
	}

}
