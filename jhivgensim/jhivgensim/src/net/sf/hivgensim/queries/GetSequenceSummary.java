package net.sf.hivgensim.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.hivgensim.queries.input.FromSnapshot;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;


public class GetSequenceSummary extends DefaultQueryOutput<Patient> {

	private HashMap<String,Boolean> selectedSequences = null; 
	
	public GetSequenceSummary(File file) throws FileNotFoundException {
		super(new PrintStream(file));		
	}
	
	/*
	 * Constructor used to output information about sequence ids from a file
	 * this file was a diff between results from an old and new query
	 */
	public GetSequenceSummary(File file, File selectIds) throws FileNotFoundException{
		super(new PrintStream(file));
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

	protected void generateOutput(List<Patient> patients) {
		if(selectedSequences == null){
			generateOutputForAllSequences(patients);
		}else{
			generateOutputForSelectedSequences(patients);
		}
	}
	
	private void generateOutputForAllSequences(List<Patient> patients){
		for(Patient p : patients){
			for(ViralIsolate vi : p.getViralIsolates()){
				for(NtSequence seq : vi.getNtSequences()){
					getOut().println();
					getOut().println();

					getOut().println(seq.getLabel() + ":\t"+seq.getViralIsolate().getSampleDate());
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
	}
	
	private void generateOutputForSelectedSequences(List<Patient> patients){
		for(Patient p : patients){
			for(ViralIsolate vi : p.getViralIsolates()){
				for(NtSequence seq : vi.getNtSequences()){
					if(!selectedSequences.containsKey((seq.getLabel()))){
						break;
					}
					getOut().println();
					getOut().println();

					getOut().println(seq.getLabel() + ":\t"+seq.getViralIsolate().getSampleDate());
					if(selectedSequences.get(seq.getLabel())){
						getOut().println("according to old query: EXPERIENCED");
					}else{
						getOut().println("according to old query: NAIVE");
					}
				
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
	}



	public static void main(String[] args) throws FileNotFoundException {
		QueryInput qi = new FromSnapshot(new File("/home/gbehey0/queries/database.snapshot"));
		GetSequenceSummary gss = new GetSequenceSummary(new File("/home/gbehey0/queries/summaryseq"));
		gss.generateOutput(qi.getOutputList());
	}
}
