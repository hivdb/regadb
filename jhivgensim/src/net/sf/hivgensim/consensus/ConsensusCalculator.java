package net.sf.hivgensim.consensus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.framework.utils.AaSequenceUtils;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ConsensusCalculator implements IQuery<NtSequence> {

	private String organism;
	private String protein;
	private int[][][] counts;
	private String refSequence;

	public static String getNameForIndex(int i){
		switch(i){
		case 0 : return "HIV-1 Subtype A";
		case 1 : return "HIV-1 Subtype B";
		case 2 : return "HIV-1 Subtype C";
		case 3 : return "HIV-1 Subtype D";
		case 4 : return "HIV-1 Subtype F";
		case 5 : return "HIV-1 Subtype G";
		case 6 : return "HIV-1 CRF 02_AG";
		case 7 : return "HIV-1 CRF 06_CPX";
		default : throw new IllegalArgumentException();
		}
	}

	public static int getIndexFor(String s){
		if(s.startsWith("HIV-1 Subtype A")){
			return 0;
		}else if(s.startsWith("HIV-1 Subtype B")){
			return 1;
		}else if(s.startsWith("HIV-1 Subtype C")){
			return 2;
		}else if(s.startsWith("HIV-1 Subtype D")){
			return 3;
		}else if(s.startsWith("HIV-1 Subtype F")){
			return 4;
		}else if(s.startsWith("HIV-1 Subtype G")){
			return 5;
		}else if(s.startsWith("HIV-1 CRF 02_AG")){
			return 6;
		}else if(s.startsWith("HIV-1 CRF 06_CPX")){
			return 7;
		}
		return -1;		
	}

	public static char getCharForIndex(int i){
		switch(i){
		case 0: return 'A';
		case 1: return 'C';
		case 2: return 'D';
		case 3: return 'E';
		case 4: return 'F';
		case 5: return 'G';
		case 6: return 'H';
		case 7: return 'I';
		case 8: return 'K';
		case 9: return 'L';
		case 10: return 'M';
		case 11: return 'N';
		case 12: return 'P';
		case 13: return 'Q';
		case 14: return 'R';
		case 15: return 'S';
		case 16: return 'T';
		case 17: return 'V';
		case 18: return 'W';
		case 19: return 'Y';
		default: throw new IllegalArgumentException();
		}
	}

	public static int getIndexFor(char c){
		switch(c){
		case 'A': return 0;
		case 'C': return 1;
		case 'D': return 2;
		case 'E': return 3;
		case 'F': return 4;
		case 'G': return 5;
		case 'H': return 6;
		case 'I': return 7;
		case 'K': return 8;
		case 'L': return 9;
		case 'M': return 10;
		case 'N': return 11;
		case 'P': return 12;
		case 'Q': return 13;
		case 'R': return 14;
		case 'S': return 15;
		case 'T': return 16;
		case 'V': return 17;
		case 'W': return 18;
		case 'Y': return 19;
		default : throw new IllegalArgumentException();
		}
	}

	public ConsensusCalculator(String organism, String orf, String protein){
		refSequence = SelectionWindow.getWindow(organism, orf, protein).getReferenceAaSequence();
		counts = new int[refSequence.length()][20][8];
		this.organism = organism;
		this.protein = protein;
	}

	public String getSubtype(NtSequence input){
		for(TestResult tr : input.getTestResults()){
			if(StandardObjects.getSubtypeTestDescription().equals(tr.getTest().getDescription())){
				return tr.getValue();
			}
		}
		return "";
	}

	public void process(NtSequence input) {
		int subtypeIndex = -1;
		if((subtypeIndex = getIndexFor(getSubtype(input))) == -1){
			return;
		}
		for(AaSequence aaseq : input.getAaSequences()){
			if(AaSequenceUtils.coversRegion(aaseq, organism, protein)){
				HashMap<Short,AaMutation> aamuts = new HashMap<Short,AaMutation>();
				for(AaMutation mut : aaseq.getAaMutations()){
					assert(aamuts.get(mut.getId().getMutationPosition()) == null);
					aamuts.put(mut.getId().getMutationPosition(), mut);
				}				
				AaMutation mut = null;
				for(short pos = aaseq.getFirstAaPos(); pos <= aaseq.getLastAaPos(); pos++){
					mut = aamuts.get(pos);
					if(mut == null){
						//reference
						counts[pos-1][getIndexFor(refSequence.charAt(pos-1))][subtypeIndex]++;						
					}else if(mut.getAaMutation() == null){
						//deletion
					}else{
						for(char m : mut.getAaMutation().toCharArray()){
							if(m != '*')
								counts[pos-1][getIndexFor(m)][subtypeIndex]++;
						}
					}
				}
			}
		}
	}

	public void printConsensusSequence(PrintStream out){
		out.print("consensus sequence:\t");
		for(int i = 0; i < counts.length;i++){
			int max = 0;
			int jmax = 0;
			for(int j = 0; j < counts[0].length;j++){
				int current = 0;
				for(int k = 0; k < counts[0][0].length; k++){
					current += counts[i][j][k];
				}
				if(current > max){
					max = current;
					jmax = j;
				}
			}
			if(max > 0){
				char M = getCharForIndex(jmax);
				out.print(M);
			}else{
				out.print('?');
			}
		}
		System.out.println();		
	}

	public void printConsensusSequencePerSubtype(PrintStream out){
		for(int k = 0; k < counts[0][0].length; k++){
			out.print(getNameForIndex(k)+":\t");
			for(int i = 0; i < counts.length;i++){
				int max = 0;
				int jmax = 0;
				for(int j = 0; j < counts[0].length;j++){
					int current = 0;

					current += counts[i][j][k];

					if(current > max){
						max = current;
						jmax = j;
					}
				}
				if(max > 0){
					char M = getCharForIndex(jmax);
					out.print(M);
				}else{
					out.print('?');
				}
			}
			System.out.println();
		}
	}

	public void printReference(PrintStream out){
		out.println("reference sequence:\t"+refSequence);
	}

	public void printCounts(PrintStream out){
		for(int i = 0; i < counts.length;i++){
			for(int j = 0; j < counts[0].length;j++){				
				out.print(counts[i][j]+"\t");
			}
			out.println();
		}
	}

	public void close() {		
		printConsensusSequence(System.out);
		printConsensusSequencePerSubtype(System.out);
		printReference(System.out);
	}

	public static void main(String[] args) throws FileNotFoundException {
		if(args.length < 5){
			System.out.println("Calculator snapshot organism orf protein drugclasses...");
			System.exit(0);
		}
		RegaDBSettings.createInstance();
		String[] classes = new String[args.length-4];
		for(int i = 4; i < args.length;i++){
			classes[i-4] = args[i];
		}
		QueryInput qi = new FromSnapshot(new File(args[0]),
				new GetDrugClassNaiveSequences(classes,
						new ConsensusCalculator(args[1],args[2],args[3])));						
		qi.run();
	}

}
