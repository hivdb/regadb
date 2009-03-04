package net.sf.hivgensim.queries;

import java.io.File;
import java.util.HashMap;
import java.util.TreeSet;

import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;

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
			String therapyString = QueryUtils.getDrugsString(t);
			if(drugcounts.containsKey(therapyString)){
				Integer count = drugcounts.get(therapyString);
				count++;
				drugcounts.put(therapyString, count);
			}else{
				drugcounts.put(therapyString, 1);
			}
		}
	}
	
	public void output(){
		
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
		
	}


	@Override
	public void generateOutput(Query<Patient> query) {
		for(Patient p : query.getOutputList()){
			generateOutput(p);
		}
		output();
		out.close();
	}

	@Override
	protected void generateOutput(Patient t) {
		initializeCounts(t);		
	}
}
