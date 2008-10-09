package org.sf.hivgensim.queries;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;

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
			Therapy t = getFirstLineTherapy(p);
			if(t != null){
				boolean experienced = true;
				for(String drug : druggenerics){
					if(!hasDrugExperience(drug, t)) {
						experienced = false;											
					}
				}
				if(experienced){
					Set<NtSequence> seqs = getLatestExperiencedSequences(p,t);
					if(seqs != null)
						temp.addAll(getLatestExperiencedSequences(p,t));
				}
			}
		}		
		outputList.addAll(temp);
	}

	private boolean hasDrugExperience(String drugClass, Therapy t) {
		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
				if(dg.getGenericId().equals(drugClass)) {
					return true;
				}
			}
		}
		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			if(tg.getId().getDrugGeneric().getGenericId().equals(drugClass)) {
				return true;
			}
		}
		return false;
	}

	private Set<NtSequence> getLatestExperiencedSequences(Patient p, Therapy t){
		Date stop = t.getStopDate();
		Date start = t.getStartDate();
		Date sampleDate;
		ViralIsolate latestVi = null;
		for(ViralIsolate vi : p.getViralIsolates()){
			sampleDate = vi.getSampleDate();
			if(sampleDate != null && start != null && stop!=null 
					&& !sampleDate.before(start) && !sampleDate.after(stop)){ // sample is taken during therapy
				if(latestVi == null || vi.getSampleDate().after(latestVi.getSampleDate())){
					latestVi = vi;
				}
			}
		}		
		return latestVi == null ? null : latestVi.getNtSequences(); 
	}

	private Therapy getFirstLineTherapy(Patient p){
		Therapy result = null;
		for(Therapy t : p.getTherapies()){
			if(result == null || t.getStartDate().before(result.getStartDate())){
				result = t;
			}
		}
		return result;
	}
	
	private boolean isGoodExperienceTherapy(Patient p, Therapy t){
		boolean result = true;
		for(Therapy other : p.getTherapies()){
			if(other.getStartDate().before(t.getStartDate())){
				
			}
		}
		return result;
	}

	public static void main(String[] args){
		QueryInput qi = new FromDatabase("gbehey0","bla123");
		Query<NtSequence> q = new GetExperiencedSequences(qi,new String[]{"AZT","3TC"});
		QueryOutput<NtSequence> qo = new ToMutationTable(new File("/home/gbehey0/queries/test2"));
		qo.generateOutput(q);
	}

}
