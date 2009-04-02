package net.sf.hivgensim.queries;

import java.util.HashSet;
import java.util.Set;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;


/**
 * This query returns a list of NtSequences
 * Each sequence has been treated with a therapy that included a drug in druggenerics
 * 
 * 
 * @author gbehey0
 *
 */

public class GetTreatedSequences extends Query<Patient,NtSequence> {

	private String[] druggenerics = new String[]{};

	public GetTreatedSequences(IQuery<NtSequence> nextQuery) {
		super(nextQuery);
	}

	public GetTreatedSequences(String[] druggenerics,IQuery<NtSequence> nextQuery){
		super(nextQuery);
		this.druggenerics = druggenerics;		
	}

	@Override
	public void process(Patient p) {
		Set<String> history = new HashSet<String>();
		history = new HashSet<String>();
		for(Therapy t : QueryUtils.sortTherapies(p.getTherapies())){
			for(TherapyGeneric tg : t.getTherapyGenerics()){
				history.add(tg.getId().getDrugGeneric().getGenericId());
			}
			for(TherapyCommercial tc : t.getTherapyCommercials()){
				for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()){
					history.add(dg.getGenericId());
				}
			}
			if(QueryUtils.isGoodExperienceTherapy(t,druggenerics,history)){
				for(NtSequence seq : QueryUtils.getAllSequencesDuringTherapy(p, t)){
					getNextQuery().process(seq);					
				}
			}else{
				//check if this therapy uses drugs in same drug class of
				//wanted drug combination
				//if such a therapy has been followed break loop and use
				//latestGoodExperienceTherapy as latest therapy to extract sequence
				if(!QueryUtils.isGoodPreviousTherapy(t, druggenerics)){
					return;
				}
			}
		}
	}
}
