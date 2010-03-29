package net.sf.hivgensim.consensus;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.SequenceProteinFilter;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Protein;
import net.sf.regadb.util.settings.RegaDBSettings;

public class DatasetCounts implements IQuery<Patient>{

	private String drugClass = "PI";
	private String[] classes;
	private Protein protein;
	private AaSequenceCounter internalCounter;
	private IQuery<Patient> next;
	
	public DatasetCounts(){
		if (drugClass.equals("RTI")) {
			classes = new String[] { "NRTI", "NNRTI" };
		} else {
			classes = new String[] { drugClass };
		}
		protein = DrugGenericUtils.getProteinForDrugClass(classes[0]);
		internalCounter = new AaSequenceCounter();
		next = new GetDrugClassNaiveSequences(classes, new SubtypeFilter(new SequenceProteinFilter(protein, new SdrmChecker(internalCounter))));
	}

	public void process(Patient input) {
		internalCounter.currentDataset = input.getDatasets().iterator().next().getDescription();
		next.process(input);
	}
	
	public void close() {
		next.close();		
	}
	
	private class AaSequenceCounter implements IQuery<AaSequence> {

		private String currentDataset = "";
		private Map<Integer, Map<String, Integer>> counts = new TreeMap<Integer, Map<String,Integer>>();
		
		public void process(AaSequence input) {
			Calendar c = Calendar.getInstance();
			c.setTime(input.getNtSequence().getViralIsolate().getSampleDate());
			int year = c.get(Calendar.YEAR);
			if(year < 1998 || year > 2008){
				return;
			}
			if(counts.containsKey(year)){
				int i = 0;
				try{
					i = counts.get(year).get(currentDataset);
				}catch(NullPointerException e){
					
				}
				i++;
				counts.get(year).put(currentDataset, i++);
			} else {
				HashMap<String, Integer> yc = new HashMap<String, Integer>();
				yc.put(currentDataset, 1);
				counts.put(year,yc);
			}
		}
		
		public void close() {
			for(Entry<Integer, Map<String, Integer>> e : counts.entrySet()){
				System.out.println("==="+e.getKey()+"===");
				for(Entry<String, Integer> ee : e.getValue().entrySet()){
					System.out.println(ee.getKey()+": "+ee.getValue());
				}
			}
		}		
	}
	
	public static void main(String[] args) {
		RegaDBSettings.createInstance();
		QueryInput qi = new FromSnapshot(new File("/home/gbehey0/temp/old-snapshot"), new DatasetCounts());
		qi.run();
	}
	
}
