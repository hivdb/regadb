package net.sf.hivgensim.fastatool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.hivgensim.mutationlists.ConsensusMutationList;
import net.sf.regadb.csv.Table;
import net.sf.regadb.util.pair.Pair;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;

public class FastaResistance extends FastaTool {

	private ConsensusMutationList mutations;
	private HashMap<String,Pair<Integer,Integer>> proteinMap = new HashMap<String, Pair <Integer,Integer>>();
	private Table resultTable = new Table();
	private ArrayList<String> header = new ArrayList<String>();
	
	public FastaResistance(String input, String output) throws IOException{
		super(input,output);
		mutations = ConsensusMutationList.retrieveListFromURL("http://cpr.stanford.edu/cpr/components/hiv_prrt/lists/sdrm_2009");
		proteinMap.put("PR",new Pair<Integer,Integer>(1,99));
		proteinMap.put("RT",new Pair<Integer,Integer>(100,330));
		header.add("id");
		header.add("PI");
		header.add("NRTI");
		header.add("NNRTI");
		header.add("any");
		resultTable.addRow(header);
	}

	protected void afterProcessing() {
		resultTable.exportAsCsv(getOut());
	}

	protected void beforeProcessing() {

	}

	protected void processSequence(FastaSequence fs) {
		try {
			ArrayList<String> row = new ArrayList<String>();
			String id = fs.getId().substring(1).trim();
			System.err.print(id);
			row.add(id);
			for(int i = 1; i < header.size(); ++i){
				row.add("n");
			}
			String drugclass = null;
			SymbolList seq = RNATools.translate(DNATools.toRNA(DNATools.createDNA(fs.getSequence())));
			String sequence = seq.seqString();
			for(String protein : proteinMap.keySet()){
				int start = proteinMap.get(protein).getKey();
				int stop = proteinMap.get(protein).getValue();
				for(int i = start; i <= stop; ++i){
					drugclass = mutations.resistantForDrugClass(protein, i - start + 1, seq.symbolAt(i).getMatches());
					if(drugclass != null){
						System.err.print(" "+(i - start + 1)+sequence.charAt(i - 1));
						row.set(header.indexOf(drugclass),"y");
						row.set(header.size() - 1, "y");
					}
				}
			}
			System.err.println();
			resultTable.addRow(row);
		} catch (IllegalAlphabetException e) {			
			e.printStackTrace();
		} catch (IllegalSymbolException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		if(args.length != 2){
			System.err.println("Usage: fastaResistance in.fasta out.csv");
			System.exit(1);
		}
		FastaResistance frr = new FastaResistance(args[0],args[1]);
		frr.processFastaFile();
	}



}
