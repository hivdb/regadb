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
 * @author gbehey0
 *
 */

public class GetExperiencedSequences extends QueryImpl<NtSequence, Patient> {

	private String[] therapyTypes = new String[]{"PI"};

	public GetExperiencedSequences(Query<Patient> inputQuery) {
		super(inputQuery);
	}

	public GetExperiencedSequences(Query<Patient> query, String[] therapyTypes){
		super(query);
		this.therapyTypes = therapyTypes;		
	}

	@Override
	protected void populateOutputList() {
		Set<NtSequence> temp = new HashSet<NtSequence>();
		for(Patient p : inputQuery.getOutputList()){
			for(Therapy t : p.getTherapies()) {
				for(String tT : therapyTypes){
					if(hasClassExperience(tT, t)) {
						Set<NtSequence> seqs = getLatestExperiencedSequence(p,t);
						if(seqs != null)
							temp.addAll(getLatestExperiencedSequence(p,t));					
					}
				}
			}		
		}
		outputList.addAll(temp);
	}

	private boolean hasClassExperience(String drugClass, Therapy t) {
		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
				if(dg.getDrugClass().getClassName().equals(drugClass)) {
					return true;
				}
			}
		}
		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			if(tg.getId().getDrugGeneric().getDrugClass().getClassId().equals(drugClass)) {
				return true;
			}
		}
		return false;
	}

	private Set<NtSequence> getLatestExperiencedSequence(Patient p, Therapy t){
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

	public static void main(String[] args){
		QueryInput qi = new FromDatabase("gbehey0","bla123");
		Query<NtSequence> q = new GetExperiencedSequences(qi,new String[]{"NRTI","NNRTI"});
		QueryOutput<NtSequence> qo = new ToMutationTable(new File("/home/gbehey0/queries/test2"));
		qo.generateOutput(q);
	}

}
