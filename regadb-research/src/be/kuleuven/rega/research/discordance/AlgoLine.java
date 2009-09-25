package be.kuleuven.rega.research.discordance;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AlgoLine implements Comparable<AlgoLine> {

	private double error;
	private Collection<SimpleMutation> muts;
	private double weight;
	private AlgorithmData[] algorithmData;
	private int rule;
	private Algorithm algorithm;
	private Map<String, Double> conditionalWeights;
	
	public AlgoLine(Collection<SimpleMutation> mutlist, AlgorithmData[] algoData,
			Algorithm algorithm, int rule, double error) {
		this.muts = mutlist;
		this.error = error;
		this.weight = 0;
		this.algorithmData = algoData;
		this.rule = rule;
		this.algorithm = algorithm;
		this.conditionalWeights = new HashMap<String, Double>();
	}
	
	public void addConditionalWeight(String condition, double d){
		if(!conditionalWeights.containsKey(condition)){
			conditionalWeights.put(condition, d);
		} else {
			double old = conditionalWeights.get(condition);
			conditionalWeights.put(condition, old+d);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlgoLine other = (AlgoLine) obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		if (!Arrays.equals(algorithmData, other.algorithmData))
			return false;
		if (Double.doubleToLongBits(error) != Double
				.doubleToLongBits(other.error))
			return false;
		if (muts == null) {
			if (other.muts != null)
				return false;
		} else if (!muts.equals(other.muts))
			return false;
		if (rule != other.rule)
			return false;
		if (Double.doubleToLongBits(weight) != Double
				.doubleToLongBits(other.weight))
			return false;
		return true;
	}

	public AlgorithmData[] getAlgorithmData() {
		return algorithmData;
	}

	public int getRule() {
		return rule;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public double getError() {
		return error;
	}

	public Collection<SimpleMutation> getMuts() {
		return muts;
	}
	
	public void setWeight(double weight){
		this.weight = weight;
	}
	
	public double getWeight(){
		return this.weight;
	}

	public double getConditionalWeight(String str){
		try{
			return this.conditionalWeights.get(str);
		} catch(NullPointerException e){
			return -1;
		}
	}
	
	@Override
	public String toString() {
		return muts+" "+algorithm+" rule " + rule + "; "
				+ Arrays.toString(algorithmData) + "; algoDiff=" + error
				+ "; totalWeight=" + weight + " - "+conditionalWeights;
	}

	@Override
	public int compareTo(AlgoLine o) {
		if(getMuts().size() != o.getMuts().size()){
			int result = new Integer(getMuts().size()).compareTo(o.getMuts().size());
			return -result;
		}
		
		if(getError()!=o.getError()){
			int result = new Double(getError()).compareTo(o.getError());
			return -result;
		}
		
		Iterator<SimpleMutation> thisIt = getMuts().iterator();
		Iterator<SimpleMutation> oIt = o.getMuts().iterator();
		
		while(thisIt.hasNext()){
			SimpleMutation mut = thisIt.next();
			SimpleMutation oMut = oIt.next();
			int mutcmp = mut.compareTo(oMut);
			if(mutcmp != 0){
				return mutcmp;
			}
		}
		
		return 0;
	}

}
