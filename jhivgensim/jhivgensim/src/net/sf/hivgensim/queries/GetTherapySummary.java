package net.sf.hivgensim.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;

/**
 * This query returns of summary of the types of therapies used and their frequencies.
 * 
 * @author gbehey0
 *
 */
public class GetTherapySummary extends DefaultQueryOutput<Patient> {
	
	
	HashMap<String,Integer> drugcounts = new HashMap<String,Integer>();
	HashMap<Integer,String> drugnames = new HashMap<Integer, String>();
	
	public GetTherapySummary(File file) throws FileNotFoundException {
		super(new PrintStream(file));
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
				getOut().println(line);
			}			
		
	}

	protected void generateOutput(List<Patient> patients) {
		for(Patient p : patients){
			initializeCounts(p);
		}
		output();
	}
}
