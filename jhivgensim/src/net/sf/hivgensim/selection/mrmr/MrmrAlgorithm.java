package net.sf.hivgensim.selection.mrmr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.regadb.csv.Table;

public class MrmrAlgorithm {
	
	public static String[] toArray(List<String> list){
		String[] t = new String[list.size()];
		return list.toArray(t);
	}
	
	private String[] target;
	private HashMap<String,String[]> features = new HashMap<String,String[]>();
	private ArrayList<String> featureNames = new ArrayList<String>();
	private ArrayList<String> selectedFeatureNames = new ArrayList<String>();
	private ArrayList<String> unselectedFeatureNames = new ArrayList<String>();
		
	public MrmrAlgorithm(Table t, String targetName, String[] featureNames){
		ArrayList<String> temp = t.getColumn(t.findColumn(targetName));
		this.target = toArray(temp.subList(1, temp.size()));
		for(String name : featureNames){
			temp = t.getColumn(t.findColumn(name));
			features.put(name, toArray(temp.subList(1, temp.size())));
			unselectedFeatureNames.add(name);
		}
		this.featureNames.addAll(unselectedFeatureNames);
		cache = new double[unselectedFeatureNames.size()][unselectedFeatureNames.size()];
	}
	
	public List<String> selectFeatures(int number){
		while(selectedFeatureNames.size() < number){
			String n = findNext();
			selectedFeatureNames.add(n);
			unselectedFeatureNames.remove(n);
		}
		return selectedFeatureNames.subList(0, number);		
	}
	
	private String findNext(){
		if(unselectedFeatureNames.isEmpty()){
			throw new IllegalStateException();
		}
		String maxName = null;
		double max = -1;
		double phi = 0;
		
		for(String f : unselectedFeatureNames){
			phi = phi(f);
			if(phi > max){
				max = phi;
				maxName = f;
			}
		}		
		return maxName;
	}	
		
	private double phi(String featureName){
		double d = mi(featureName);
		double r = 0;		
		for(String fx : selectedFeatureNames){
			r += mi(featureName,fx);
		}
		if(!selectedFeatureNames.isEmpty()){
			r /= selectedFeatureNames.size();
		}
		return d - r;
	}
	
	/*
	 * CACHE
	 * 	contains mi(f1,f2) at [i][j] when i != j and featureNames.get(i) == f1 and featureNames.get(j) == f2 
	 *  contains mi(f1) at [i][j] when i == j and featureNames.get(i) == f1
	 */
	
	private double[][] cache;
	
	private double getCache(String f1, String f2){
		return cache[featureNames.indexOf(f1)][featureNames.indexOf(f2)];
	}
	
	private void updateCache(String f1, String f2, double d){
		cache[featureNames.indexOf(f1)][featureNames.indexOf(f2)] = d;
	}
	
	private double mi(String f1, String f2){
		double d = getCache(f1, f2); 
		if(d == 0){
			MutualInformation mi = new MutualInformation(features.get(f1),features.get(f2));
			d = mi.getMI();
			updateCache(f1, f2, d);
		}
		return d;
	}
	
	private double mi(String f){
		double d = getCache(f,f);
		if(d == 0){
			MutualInformation mi = new MutualInformation(features.get(f),target);
			d = mi.getMI();
			updateCache(f, f, d);
		}
		return d;
	}
	
}
