package net.sf.hivgensim.consensus;

import java.io.File;
import java.io.FileNotFoundException;
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
import net.sf.regadb.util.settings.RegaDBSettings;

public class ConsensusCalculator implements IQuery<NtSequence> {

	private String organism;
	private String protein;
	private int[][] counts;
	private String refSequence;

	public ConsensusCalculator(String organism, String orf, String protein){
		refSequence = SelectionWindow.getWindow(organism, orf, protein).getReferenceAaSequence();
		counts = new int[refSequence.length()]['Z'-'A'];
		this.organism = organism;
		this.protein = protein;
	}
	
	public void process(NtSequence input) {
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
						int col = refSequence.charAt(pos-1)-'A';
						counts[pos-1][col]++;						
					}else if(mut.getAaMutation() == null){
						//deletion
					}else{
						for(char m : mut.getAaMutation().toCharArray()){
							int col = m-'A';									
							if(m != '*')
								counts[pos-1][col]++;
						}
					}
				}
			}
		}
	}
	
	public void close() {
		for(int i = 0; i < counts.length;i++){
			int max = 0;
			int jmax = 0;
			for(int j = 0; j < counts[0].length;j++){
				if(counts[i][j] > max){
					max = counts[i][j];
					jmax = j;
				}
			}
			if(max > 0){
				char M = (char) ('A'+jmax);
				System.out.print(M);
			}else{
				System.out.print('?');
			}
		}
		System.out.println();
		System.out.println(refSequence);
		for(int i = 0; i < counts.length;i++){
			for(int j = 0; j < counts[0].length;j++){
				if(j == 1 || j == 9 || j == 14 || j == 20 || j == 23 || j == 25){ //skip BJOUXZ
					continue;
				}
				System.out.print(counts[i][j]+"\t");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		if(args.length != 5){
			System.out.println("Calculator snapshot drugclass organism orf protein");
			System.exit(0);
		}
		RegaDBSettings.createInstance();
		QueryInput qi = new FromSnapshot(new File(args[0]),
						new GetDrugClassNaiveSequences(new String[]{args[1]},
						new ConsensusCalculator(args[2],args[3],args[4])));						
		qi.run();
	}
	
}
