package net.sf.hivgensim.mutationlists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.symbol.Alphabet;
import org.biojava.bio.symbol.IllegalSymbolException;

public class ConsensusMutationList implements Iterable<ConsensusMutation>{
	
	public static ConsensusMutationList retrieveListFromURL(String url) throws IOException{
		ArrayList<ConsensusMutation> mutlist = new ArrayList<ConsensusMutation>();
		        
		BufferedReader bfr = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
		String line = null;
		while((line = bfr.readLine()) != null){
			mutlist.add(parseLine(line));
		}
		return new ConsensusMutationList(mutlist);
	}
	
	
	
	private static ConsensusMutation parseLine(String line){
		String[] parts = line.trim().split("\t");
		if(parts.length != 7){
			System.err.println("line doesn't have 7 columns");
		}
		String listName = parts[0];
		String version = parts[1];
		String proteinAbbreviation = parts[2];
		char referenceAa = parts[3].charAt(0);
		int position = Integer.parseInt(parts[4]);
		char mutationAa = parts[5].charAt(0);
		String drugClassId = parts[6];
		return new ConsensusMutation(listName,version,proteinAbbreviation,referenceAa,position,mutationAa,drugClassId);
	}
	
	private ArrayList<ConsensusMutation> mutationList = new ArrayList<ConsensusMutation>();
	
	public ConsensusMutationList(){
		
	}
	
	public ConsensusMutationList(ArrayList<ConsensusMutation> mutations){
		this.mutationList = mutations;
	}
	
	public void add(ConsensusMutation mutation){
		mutationList.add(mutation);
	}
	
	public void addAll(ConsensusMutationList extraMutations){
		mutationList.addAll(extraMutations.mutationList);
	}
	
	public String toString(){
		String result = "";
		for(ConsensusMutation mut : mutationList){
			result += mut.toString()+"\n";
		}
		return result;
	}
	
	public String resistantForDrugClass(String protein, int pos, Alphabet a) throws IndexOutOfBoundsException, IllegalSymbolException{
		for(ConsensusMutation cm : subList(protein)){
			if(cm.getPosition() == pos){
				if(a.contains(ProteinTools.createProtein(String.valueOf(cm.getMutationAminoAcid())).symbolAt(1))){
					return cm.getDrugClassId();
				}
			}
		}
		return null;
	}
	
	public boolean containsMutation(String protein, int pos, String aas) {
		for(ConsensusMutation cm : subList(protein)){
			if(cm.getPosition() == pos){
				if(aas != null && aas.contains(""+cm.getMutationAminoAcid())){
					return true;
				}
			}
		}
		return false;
	}
	
	
	public ConsensusMutationList subList(String protein){
		ArrayList<ConsensusMutation> list = new ArrayList<ConsensusMutation>();
		for(ConsensusMutation mut : mutationList){
			if(mut.getProteinAbbreviation().equals(protein)){
				list.add(mut);
			}
		}
		return new ConsensusMutationList(list);
	}
	
	public Iterator<ConsensusMutation> iterator() {
		return mutationList.iterator();
	}	

}