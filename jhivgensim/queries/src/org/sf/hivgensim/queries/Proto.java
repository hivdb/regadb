package org.sf.hivgensim.queries;

import java.io.File;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;

public class Proto {
	
	public static void main(String[] args){
		
		QueryInput input = new FromDatabase("gbehey0","bla123");
		Query<Patient> q1 = new GetNaivePatients(input);
		Query<NtSequence> q2 = new GetLatestSequencePerPatient(q1);
		File outputFile = new File("/home/gbehey0/queries/result");
		QueryOutput<NtSequence> output = new ToMutationTable(outputFile);
		output.generateOutput(q2);
		
//		List<NtSequence> list = q.getOutputList();
//		for(NtSequence seq : list){
//			System.out.println(seq.getNtSequenceIi());
//				
//		}
//		System.out.println(list.size());
//		QueryOutput<Patient> qo = new GetTherapySummary(new File("/home/gbehey0/queries/summary"));
//		
//		qo.generateOutput(input);
		
//			6489:	
//			2000-12-13	2001-05-20	3TC NVP AZT 	G.E.T. 
//			2003-03-18	2004-03-30	AZT 3TC LPV/r 	G.E.T. 
//			2004-03-30	2005-09-15	DDI 3TC EFV 	G.P.T. 
			
//		for(TherapyGeneric tg : t.getTherapyGenerics()) {
//			for(DrugGeneric dg2 : tg.getId().getDrugGeneric().getDrugClass().getDrugGenerics()){ // for every generic drug, get all generic drugs belonging to the same class
//				for(String dgcheck : druggenerics){
//					if(dg2.getGenericId().equals(dgcheck)){ 
//						// tg belongs to same class as dgcheck
//						// so now check if tg equals to one of the given druggenerics
//						ok = false;
//						for(String dgcheck2 : druggenerics){
//							if(dgcheck2.equals(tg.getId().getDrugGeneric().getGenericId())){
//								ok = true;
//							}
//						}							
//					}
//				}
//			}
//		}
//		return ok;
	}

}
