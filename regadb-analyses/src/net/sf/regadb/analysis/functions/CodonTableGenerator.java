package net.sf.regadb.analysis.functions;

import java.util.Arrays;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.SymbolList;


public class CodonTableGenerator {

	public static void main(String[] args) throws Exception {
//		String aminoacids = "ARNDCEQGHILKMFPSTWYV*";
//		SymbolList aas = ProteinTools.createProtein(aminoacids);
//		for(int i = 1; i < 21; ++i){
//			System.err.println("name = name.replace(\""+aas.symbolAt(i).getName()+"\",\""+aminoacids.charAt(i-1)+"\");");
//		}
//		System.err.println("name = name.replace(\"TER\",\"*\");");
		
		char[] nucleotides = "ACGTMRWSYKVHDBN".toCharArray();
		for(char n1 : nucleotides){
			for(char n2 : nucleotides){
				for(char n3 : nucleotides){
					String codon = ""+n1+n2+n3;
					SymbolList sl = RNATools.translate(DNATools.toRNA(DNATools.createDNA(codon)));
					System.out.println("codonTable.put(\""+codon+"\",\""+toAAString(sl.symbolAt(1).getName())+"\");");					
				}
			}
		}		
	}

	private static String toAAString(String name) {
		name = name.replace("[","");
		name = name.replace("]","");
		name = name.replace("ALA","A");
		name = name.replace("ARG","R");
		name = name.replace("ASN","N");
		name = name.replace("ASP","D");
		name = name.replace("CYS","C");
		name = name.replace("GLU","E");
		name = name.replace("GLN","Q");
		name = name.replace("GLY","G");
		name = name.replace("HIS","H");
		name = name.replace("ILE","I");
		name = name.replace("LEU","L");
		name = name.replace("LYS","K");
		name = name.replace("MET","M");
		name = name.replace("PHE","F");
		name = name.replace("PRO","P");
		name = name.replace("SER","S");
		name = name.replace("THR","T");
		name = name.replace("TRP","W");
		name = name.replace("TYR","Y");
		name = name.replace("VAL","V");
		name = name.replace("TER","*");
		name = name.replace(" ","");
		char[] array = name.toCharArray();
		Arrays.sort(array);
		return new String(array);
	}

}
