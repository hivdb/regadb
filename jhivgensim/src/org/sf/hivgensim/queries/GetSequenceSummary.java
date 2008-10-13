package org.sf.hivgensim.queries;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;

public class GetSequenceSummary extends QueryOutput<Patient> {

	public GetSequenceSummary(File file) {
		super(file);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void generateOutput(Query<Patient> query) {
		for(Patient p : query.getOutputList()){
			for(ViralIsolate vi : p.getViralIsolates()){
				for(NtSequence seq : vi.getNtSequences()){
					
						System.out.println(seq.getNtSequenceIi() + ":\t");
						for(Therapy t : QueryInfra.sortTherapies(p.getTherapies())){
							if(t.getStopDate() == null){
								System.out.print(t.getStartDate()+"\t\t\t");
							}else{
								System.out.print(t.getStartDate()+"\t"+t.getStopDate()+"\t");
							}
							for(TherapyCommercial tc : t.getTherapyCommercials()) {
								for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
									System.out.print(dg.getGenericId() + " ");
								}
							}
							for(TherapyGeneric tg : t.getTherapyGenerics()) {
								System.out.print(tg.getId().getDrugGeneric().getGenericId()+ " ");
							}
							System.out.print("\t");
							if(QueryInfra.isGoodExperienceTherapy(t,new String[]{"AZT","3TC"})){
								System.out.print("G.E.T. ");
								System.out.println();
								System.out.println();
								break;
							}else if(QueryInfra.isGoodPreviousTherapy(t, new String[]{"AZT","3TC"})){
								System.out.print("G.P.T. ");
							}else {
								System.out.print("BREAK ");
								System.out.println();
								System.out.println();
								break;
							}
							System.out.println();
						}
						System.out.println();
					
				}
			}

		}
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QueryInput qi = new FromSnapshot(new File("/home/gbehey0/queries/database.snapshot"));
		GetSequenceSummary gss = new GetSequenceSummary(new File("/home/gbehey0/queries/summaryseq"));
		gss.generateOutput(qi);
	}

}
