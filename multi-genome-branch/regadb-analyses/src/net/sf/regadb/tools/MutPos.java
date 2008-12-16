/*
 * Created on Jul 6, 2005
 */
package net.sf.regadb.tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.regadb.csv.Table;

/**
 * @author kdforc0
 */
public class MutPos {
	public static void main(String[] args) throws FileNotFoundException {
		if(args.length != 4){
			System.err.println("Usage: mutpos mutationtable.input.csv mutations.out positions.out wildtypes.out");
		}
		Table t = new Table(new BufferedInputStream(new FileInputStream(args[0])), false);
		
		ArrayList<String> selectedPositions = new ArrayList<String>();
		ArrayList<String> mutations = new ArrayList<String>();
		ArrayList<String> wildtypes = new ArrayList<String>();
		
		ArrayList<Map<String,Integer>> histogram = t.histogram();
		Map<String,Integer> prevalences = new HashMap<String,Integer>();
		Set<String> allPositions = new HashSet<String>();
		Pattern p = Pattern.compile("([A-Z]+)([0-9]+)([A-Z*])");
		
		//get all prevalences and put them in prevalences
		for (int i = 0; i < histogram.size(); ++i) {
			String colName = t.valueAt(i, 0);
			Matcher m = p.matcher(colName);
			if(!m.matches()){
				System.out.println(("column name ("+colName+") not in form: "+p.pattern()));				
			}else{
				String region = m.group(1);
				String position = m.group(2);
				allPositions.add(region+position);
				prevalences.put(colName, histogram.get(i).get("y"));
			}
		}
		
		//for all the positions
		// put wild type (max prevalence) in wildTypes
		// put mutations (other aas) in mutations
		// put positionstring in selectedPositions
		for(String position : allPositions){
			Map<String,Integer> prevalencesOnThisPosition = new HashMap<String,Integer>();
			for(String mutation : prevalences.keySet()){
				if(mutation.matches(position+"[A-Z*]")){
					prevalencesOnThisPosition.put(mutation,prevalences.get(mutation));
				}
			}
			if(prevalencesOnThisPosition.size() != 1){
				Entry<String,Integer> wildType = prevalencesOnThisPosition.entrySet().iterator().next();
				for(Entry<String,Integer> e : prevalencesOnThisPosition.entrySet()){
					if(e.getValue() > wildType.getValue()){
						wildType = e;
					}
				}
				for(Entry<String,Integer> e : prevalencesOnThisPosition.entrySet()){
					if(e != wildType){
						mutations.add(e.getKey());
					}
				}
				wildtypes.add(wildType.getKey());
				selectedPositions.add("("+position+"_.)");
			}			
		}
		
		Comparator<String> c = new MutationStringComparator();
		
		PrintStream mutFile = new PrintStream(new FileOutputStream(args[1]));
		Collections.sort(mutations, c);
		mutFile.println(mutations.toString().replace("[", "").replace("]", "").replace(" ",""));
		mutFile.close();
		
		PrintStream wtFile = new PrintStream(new FileOutputStream(args[3]));
		Collections.sort(wildtypes, c);
		wtFile.println(wildtypes.toString().replace("[", "").replace("]", "").replace(" ",""));
		wtFile.close();
		
		PrintStream posFile = new PrintStream(new FileOutputStream(args[2]));
		posFile.println(selectedPositions.toString().replace("[", "").replace("]", "").replace(",", "|").replace(" ",""));
		posFile.close();
	}
}
