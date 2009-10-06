package net.sf.hivgensim.selection.mrmr;

import java.util.ArrayList;

public class MutualInformation {
	
	public static ArrayList<String> getValues(String[] list){
		ArrayList<String> names = new ArrayList<String>();
		for(String s : list){
			if(check(s) && !names.contains(s)){
				names.add(s);
			}
		}
		return names;
	}
	
	public static boolean check(String s){
		return s != null && !"".equals(s);
	}
	
	public static double log2(double i){
		return Math.log(i) / Math.log(2);		
	}
	
	private String[] first;
	private String[] second;
	private ArrayList<String> fValues;
	private ArrayList<String> sValues;
	private double[][] probs;
	private double result;
	
	
	public MutualInformation(String[] first, String[] second){
		setFirst(first);
		setSecond(second);
		setValues();
		setProbabilities();
		setResult();
	}
	
	public String[] getFirst() {
		return first;
	}

	private void setFirst(String[] first) {
		this.first = first;
	}

	public String[] getSecond() {
		return second;
	}

	private void setSecond(String[] second) {
		this.second = second;
	}
	
	private void setValues(){
		this.fValues = getValues(getFirst());
		this.sValues = getValues(getSecond());
	}

	private void setProbabilities() {	
		probs = new double[fValues.size()][sValues.size()];
		for(int i = 0; i < first.length; i++){
			if(check(first[i]) && check(second[i])){
				probs[fValues.indexOf(first[i])][sValues.indexOf(second[i])]++;
			}
		}
		int nb = first.length;		
		for(int i = 0; i < probs.length; i++){
			for (int j = 0; j < probs[0].length; j++) {
				probs[i][j] /= nb;
			}
		}		
	}
	
	private void setResult(){
		result = 0;
		double joint,pf,ps = 0;
		for(String fs : fValues){
			for(String ss : sValues){
				joint = probs[fValues.indexOf(fs)][sValues.indexOf(ss)];
				pf = getProbability(fs, true);
				ps = getProbability(ss, false);
				if(joint != 0 && pf != 0 && ps != 0){
					result += (joint * log2(joint/(pf*ps)));
				}
			}
		}
	}
	
	private double getProbability(String s, boolean first){
		double r = 0;
		int index = 0; 
		if(first){
			index = fValues.indexOf(s);
			for(int i = 0; i < probs[index].length; i++){
				r += probs[index][i];
			}
		}else{
			index = sValues.indexOf(s);
			for(int i = 0; i < probs.length; i++){
				r += probs[i][index];
			}
		}
		return r;
	}
	
	public double getMI(){
		return result;
	}
	
	public static void main(String[] args) {
		String[] a = {"a","b","b","a"};
		String[] b = {"a","b","b","b"};

		MutualInformation mi = new MutualInformation(a, b);
		System.out.println(mi.getMI());
		//0.3112781244591328
	}

}
