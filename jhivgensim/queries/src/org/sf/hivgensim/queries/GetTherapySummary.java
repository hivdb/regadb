package org.sf.hivgensim.queries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.sf.hivgensim.queries.framework.Query;
import org.sf.hivgensim.queries.framework.QueryOutput;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;

/**
 * This query returns of summary of the types of therapies used and their frequencies.
 * 
 * @author gbehey0
 *
 */
public class GetTherapySummary extends QueryOutput<Patient> {
	
	
	HashMap<String,Integer> drugcounts = new HashMap<String,Integer>();
	HashMap<Integer,String> drugnames = new HashMap<Integer, String>();
	
	public GetTherapySummary(File file) {
		super(file);
	}
	
	protected void initializeCounts(Patient p) {
		for(Therapy t : p.getTherapies()){
			String therapyString = makeTherapyString(t.getTherapyGenerics());
			if(drugcounts.containsKey(therapyString)){
				Integer count = drugcounts.get(therapyString);
				count++;
				drugcounts.put(therapyString, count);
			}else{
				drugcounts.put(therapyString, 1);
			}
		}
	}


	private String makeTherapyString(Set<TherapyGeneric> therapyGenerics) {
		boolean[] genericPresent = new boolean[50]; //FIXED LENGTH?
		for(TherapyGeneric tg : therapyGenerics){
			genericPresent[tg.getId().getDrugGeneric().getGenericIi()-1] = true;
			drugnames.put(tg.getId().getDrugGeneric().getGenericIi(),tg.getId().getDrugGeneric().getGenericId());			
		}		
		String therapyString = "";
		for(int i = 0; i < genericPresent.length;i++){
			if(genericPresent[i]){
				
				if(drugnames.get(i+1) == null){
					therapyString = therapyString + " + " + (i+1);					
				}else{
					therapyString = therapyString + " + " + drugnames.get(i+1);					
				}
			}
		}
		return therapyString.substring(3);
	}

	

	public void output(){
		try {
			PrintStream out = new PrintStream(new FileOutputStream(file));
			TreeSet<String> ts = new TreeSet<String>();
			for(String therapy : drugcounts.keySet()){
				Integer count = drugcounts.get(therapy);
				String countstring = count.toString();
				while(countstring.length()<5){
					countstring = " " + countstring;
				}
				ts.add(countstring + "\t" + therapy);
			}
			for(String line : ts.descendingSet()){
				out.println(line);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void generateOutput(Query<Patient> query) {
		for(Patient p : query.getOutputList()){
			initializeCounts(p);
		}
		output();		
	}
}
