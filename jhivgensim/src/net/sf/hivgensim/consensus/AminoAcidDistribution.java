package net.sf.hivgensim.consensus;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.SampleDateFilter;
import net.sf.hivgensim.queries.SequenceProteinFilter;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.framework.utils.AaSequenceUtils;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Protein;
import net.sf.regadb.util.settings.RegaDBSettings;

public class AminoAcidDistribution implements IQuery<Patient>  {

	private GetDrugClassNaiveSequences preQuery;
	private String reference;

	public AminoAcidDistribution(Date begin, Date end, String drugClass){
		String[] classes;
		if(drugClass.equals("RTI")){
			classes = new String[]{"NRTI","NNRTI"};
		}else{
			classes = new String[]{drugClass};
		}
		Protein protein = DrugGenericUtils.getProteinForDrugClass(classes[0]);
		this.reference = new SelectionWindow(
				protein.getOpenReadingFrame().getGenome().getOrganismName()
				, protein.getOpenReadingFrame().getName(), protein.getAbbreviation())
				.getReferenceAaSequence();
		this.preQuery = new GetDrugClassNaiveSequences(classes,
				new SequenceProteinFilter(protein, 
						new SampleDateFilter(begin, end, 
								new SdrmChecker(
								new AminoAcidDistributionProcessor()))));
	}
	
	public void close() {
		this.preQuery.close();
	}

	public void process(Patient input) {
		this.preQuery.process(input);
	}
	
	private class AminoAcidDistributionProcessor implements IQuery<AaSequence> {

		private ConsensusCalculator calculator;
		private ArrayList<Character> possibleAA;
		
		public AminoAcidDistributionProcessor() {
			this.calculator = new ConsensusCalculator(reference);
			this.possibleAA = new ArrayList<Character>();
			possibleAA.add(new Character('A'));
			possibleAA.add(new Character('C'));
			possibleAA.add(new Character('D'));
			possibleAA.add(new Character('E'));
			possibleAA.add(new Character('F'));
			possibleAA.add(new Character('G'));
			possibleAA.add(new Character('H'));
			possibleAA.add(new Character('I'));
			possibleAA.add(new Character('K'));
			possibleAA.add(new Character('L'));
			possibleAA.add(new Character('M'));
			possibleAA.add(new Character('N'));
			possibleAA.add(new Character('P'));
			possibleAA.add(new Character('Q'));
			possibleAA.add(new Character('R'));
			possibleAA.add(new Character('S'));
			possibleAA.add(new Character('T'));
			possibleAA.add(new Character('V'));
			possibleAA.add(new Character('W'));
			possibleAA.add(new Character('Y'));
			possibleAA.add(new Character('*'));			
		}
		
		public void close() {
			Map<Short, Map<Character, Float>> counts = calculator.getCountsIncludingReference();
			for (int i = 0; i < possibleAA.size(); i++) {
				if(i!=0){
					System.out.print(", ");
				} else {
					System.out.print("position, support, ");
				}
				System.out.print(possibleAA.get(i));
			}
			System.out.println();
			SortedSet<Short> sortedCounts = new TreeSet<Short>();
			sortedCounts.addAll(counts.keySet());
			for (Short position : sortedCounts) {
				Map<Character, Float> posMap = counts.get(position);
				for (short i = 0; i < possibleAA.size(); i++) {
					if(i!=0){
						System.out.print(", ");
					} else {
						System.out.print(""+position+","+calculator.getSupport(position)+",");
					}
					Character aa = possibleAA.get(i);
					if(posMap.containsKey(aa)){
						System.out.print(posMap.get(aa) / calculator.getSupport(position));
					} else {
						System.out.print("0");
					}
				}
				System.out.println();
			}
		}
		
		public void process(AaSequence input) {
			String subtype = ConsensusCalculator.getSubtypeForConsensus(input);
			if(subtype.equals("HIV-1 Subtype B")){
				calculator.process(AaSequenceUtils.toCharSequence(input, reference),input.getFirstAaPos(),input.getLastAaPos());
			}
		}
	}
	
	public static void main(String[] args) throws ParseException {
		if(args.length != 3){
			System.err.println("Usage: consensus snapshot year drugclass");
			System.exit(1);
		}
		String year = args[1];
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date begin = sdf.parse("01-01-"+year);
		Date end = sdf.parse("31-12-"+year);
		RegaDBSettings.createInstance();
		new FromSnapshot(new File(args[0]),
				new AminoAcidDistribution(begin, end, args[2])).run();
	}

}
