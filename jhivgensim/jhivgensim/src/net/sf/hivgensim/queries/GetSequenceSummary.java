package net.sf.hivgensim.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;


public class GetSequenceSummary extends DefaultQueryOutput<Patient> {

	public GetSequenceSummary(File file) throws FileNotFoundException {
		super(new PrintStream(file));		
	}

	public void process(Patient p){
		for(ViralIsolate vi : p.getViralIsolates()){
			for(NtSequence seq : vi.getNtSequences()){
				
				getOut().println();
				getOut().println();
				getOut().println(vi.getSampleId() + "|" + seq.getLabel() + ":\t"+seq.getViralIsolate().getSampleDate());
				for(Therapy t : QueryUtils.sortTherapies(p.getTherapies())){
					if(!vi.getSampleDate().after(t.getStartDate())){
						break;
					}
					if(t.getStopDate() == null){
						getOut().print(t.getStartDate()+"\t\t\t");
					}else{
						getOut().print(t.getStartDate()+"\t"+t.getStopDate()+"\t");
					}
					for(TherapyCommercial tc : t.getTherapyCommercials()) {
						for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
							getOut().print(dg.getGenericId() + " ");
						}
					}
					for(TherapyGeneric tg : t.getTherapyGenerics()) {
						getOut().print(tg.getId().getDrugGeneric().getGenericId()+ " ");
					}
					getOut().print("\t");
					if(QueryUtils.isGoodExperienceTherapy(t,new String[]{"AZT","3TC"})){
						getOut().print("G.E.T. ");
						//								break;
					}else if(QueryUtils.isGoodPreviousTherapy(t, new String[]{"AZT","3TC"})){
						getOut().print("G.P.T. ");
					}else {
						getOut().print("BREAK ");								
						break;
					}
					getOut().println();
				}
				getOut().println();
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		QueryInput q = 	new FromDatabase("gbehey0","bla123",
						new GetSequenceSummary(new File("/home/gbehey0/bla")));
		q.run();
	}
}
