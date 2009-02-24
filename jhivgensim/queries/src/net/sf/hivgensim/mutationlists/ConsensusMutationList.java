package net.sf.hivgensim.mutationlists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;

public class ConsensusMutationList {
	
	public static final ConsensusMutationList ALL;
	
	static{
		ConsensusMutationList temp = new ConsensusMutationList(new ArrayList<ConsensusMutation>());
		try {
			String url = "http://cpr.stanford.edu/cpr/components/hiv_prrt/lists/";
			ArrayList<String> urls = retrieveURLs(url);
			for(String tail : urls){
				temp.addAll(retrieveListFromURL(url+tail));
			}			
		} catch (MalformedURLException e) {
			e.printStackTrace();			
		} catch (IOException e) {
			e.printStackTrace();			
		}
		ALL = temp;		
	}
	
	public static ArrayList<String> retrieveURLs(String url) throws IOException{
		ArrayList<String> urls = new ArrayList<String>();
		//proxy settings
		InetSocketAddress ISA=new InetSocketAddress("www-proxy",3128);
        Proxy proxy=new Proxy(java.net.Proxy.Type.HTTP,ISA);
        
		BufferedReader bfr = new BufferedReader(new InputStreamReader(new URL(url).openConnection(proxy).getInputStream()));
		String line = null;
		while((line = bfr.readLine()) != null){
			if(line.contains("alt=\"[   ]\"")){
				String link = line.substring(line.indexOf("<a href="), line.indexOf("</a>")+4);
				urls.add(link.substring(link.indexOf("\"")+1,link.lastIndexOf("\"")));						
			}			
		}
		return urls;
	}
	
	public static ConsensusMutationList retrieveListFromURL(String url) throws IOException{
		ArrayList<ConsensusMutation> mutlist = new ArrayList<ConsensusMutation>();
		//proxy settings
		InetSocketAddress ISA=new InetSocketAddress("www-proxy",3128);
        Proxy proxy=new Proxy(java.net.Proxy.Type.HTTP,ISA);
        
		BufferedReader bfr = new BufferedReader(new InputStreamReader(new URL(url).openConnection(proxy).getInputStream()));
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
	
	public ConsensusMutationList(ArrayList<ConsensusMutation> mutations){
		this.mutationList = mutations;
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
	
	public ConsensusMutationList subList(String protein){
		ArrayList<ConsensusMutation> list = new ArrayList<ConsensusMutation>();
		for(ConsensusMutation mut : mutationList){
			if(mut.getProteinAbbreviation().equals(protein)){
				list.add(mut);
			}
		}
		return new ConsensusMutationList(list);
	}
	
	public static void main(String[] args){
		System.out.println(ALL);
	}
	
	

}
