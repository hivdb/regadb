package net.sf.hivgensim.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.hivgensim.queries.input.FromSnapshot;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;


public class GetSequenceSummary extends QueryOutput<Patient> {

	private HashMap<String,Boolean> selectedSequences = null; 
	
	public GetSequenceSummary(File file) {
		super(file);		
	}
	
	/*
	 * Constructor used to output information about sequence ids from a file
	 * this file was a diff between results from an old and new query
	 */
	public GetSequenceSummary(File file, File selectIds){
		super(file);
		selectedSequences = new HashMap<String,Boolean>();
		
		Scanner s = null;
		try {
			s = new Scanner(selectIds);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(s.hasNextLine()){
			String line = s.nextLine().replace(">", "").replace("<","").trim();
			System.out.println(line);
			selectedSequences.put(line,true);			
		}		
	}

	@Override
	public void generateOutput(Query<Patient> query) {
		if(selectedSequences == null){
			generateOutputForAllSequences(query);
		}else{
			generateOutputForSelectedSequences(query);
		}
	}
	
	private void generateOutputForAllSequences(Query<Patient> query){
		for(Patient p : query.getOutputList()){
			for(ViralIsolate vi : p.getViralIsolates()){
				for(NtSequence seq : vi.getNtSequences()){
					out.println();
					out.println();

					out.println(seq.getLabel() + ":\t"+seq.getViralIsolate().getSampleDate());
					for(Therapy t : QueryUtils.sortTherapies(p.getTherapies())){
						if(!vi.getSampleDate().after(t.getStartDate())){
							break;
						}
						if(t.getStopDate() == null){
							out.print(t.getStartDate()+"\t\t\t");
						}else{
							out.print(t.getStartDate()+"\t"+t.getStopDate()+"\t");
						}
						for(TherapyCommercial tc : t.getTherapyCommercials()) {
							for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
								out.print(dg.getGenericId() + " ");
							}
						}
						for(TherapyGeneric tg : t.getTherapyGenerics()) {
							out.print(tg.getId().getDrugGeneric().getGenericId()+ " ");
						}
						out.print("\t");
						if(QueryUtils.isGoodExperienceTherapy(t,new String[]{"AZT","3TC"})){
							out.print("G.E.T. ");
							//								break;
						}else if(QueryUtils.isGoodPreviousTherapy(t, new String[]{"AZT","3TC"})){
							out.print("G.P.T. ");
						}else {
							out.print("BREAK ");								
							break;
						}
						out.println();
					}
					out.println();
				}
			}
		}
	}
	
	private void generateOutputForSelectedSequences(Query<Patient> query){
		for(Patient p : query.getOutputList()){
			for(ViralIsolate vi : p.getViralIsolates()){
				for(NtSequence seq : vi.getNtSequences()){
					if(!selectedSequences.containsKey((seq.getLabel()))){
						break;
					}
					out.println();
					out.println();

					out.println(seq.getLabel() + ":\t"+seq.getViralIsolate().getSampleDate());
					if(selectedSequences.get(seq.getLabel())){
						out.println("according to old query: EXPERIENCED");
					}else{
						out.println("according to old query: NAIVE");
					}
				
					for(Therapy t : QueryUtils.sortTherapies(p.getTherapies())){
						if(!vi.getSampleDate().after(t.getStartDate())){
							break;
						}
						if(t.getStopDate() == null){
							out.print(t.getStartDate()+"\t\t\t");
						}else{
							out.print(t.getStartDate()+"\t"+t.getStopDate()+"\t");
						}
						for(TherapyCommercial tc : t.getTherapyCommercials()) {
							for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
								out.print(dg.getGenericId() + " ");
							}
						}
						for(TherapyGeneric tg : t.getTherapyGenerics()) {
							out.print(tg.getId().getDrugGeneric().getGenericId()+ " ");
						}
						out.print("\t");
						if(QueryUtils.isGoodExperienceTherapy(t,new String[]{"AZT","3TC"})){
							out.print("G.E.T. ");
						}else if(QueryUtils.isGoodPreviousTherapy(t, new String[]{"AZT","3TC"})){
							out.print("G.P.T. ");
						}else {
							out.print("BREAK ");								
							break;
						}
						out.println();
					}
					out.println();
				}
			}
		}
	}



	public static void main(String[] args) {
		QueryInput qi = new FromSnapshot(new File("/home/gbehey0/queries/database.snapshot"));
		GetSequenceSummary gss = new GetSequenceSummary(new File("/home/gbehey0/queries/summaryseq"));
		gss.generateOutput(qi);
	}

	@Override
	protected void generateOutput(Patient t) {
		// TODO Auto-generated method stub
		
	}

}
