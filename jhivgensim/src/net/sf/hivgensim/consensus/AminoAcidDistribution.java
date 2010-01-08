package net.sf.hivgensim.consensus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.SampleDateFilter;
import net.sf.hivgensim.queries.SequenceProteinFilter;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.utils.AaSequenceUtils;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Protein;
import net.sf.regadb.util.settings.RegaDBSettings;

public class AminoAcidDistribution implements IQuery<Patient>  {

	private GetDrugClassNaiveSequences preQuery;
	private String reference;

	public AminoAcidDistribution(Date begin, Date end, String drugClass){
		Protein protein = DrugGenericUtils.getProteinForDrugClass(drugClass);
		this.reference = SelectionWindow.getWindow(
				protein.getOpenReadingFrame().getGenome().getOrganismName()
				, protein.getOpenReadingFrame().getName(), protein.getAbbreviation())
				.getReferenceAaSequence();
		this.preQuery = new GetDrugClassNaiveSequences(new String[] {drugClass},
				new SequenceProteinFilter(protein, 
						new SampleDateFilter(begin, end, 
								new AminoAcidDistributionProcessor())));
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
			possibleAA.add(new Character('-'));
		}
		
		public void close() {
			float nSeq = calculator.getAmountOfSequences();
			Map<Short, Map<Character, Float>> counts = calculator.getCountsIncludingReference();
			for (int i = 0; i < possibleAA.size(); i++) {
				if(i!=0) System.out.print(", ");
				System.out.print(possibleAA.get(i));
			}
			System.out.println();
			for (Entry<Short, Map<Character, Float>> position : counts.entrySet()) {
				Map<Character, Float> posMap = position.getValue();
				for (int i = 0; i < possibleAA.size(); i++) {
					if(i!=0){
						System.out.print(", ");
					}
					Character aa = possibleAA.get(i);
					if(posMap.containsKey(aa)){
						System.out.print(posMap.get(aa) / nSeq);
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
				calculator.process(AaSequenceUtils.toCharSequence(input, reference));
			}
		}
	}
	
	public static void main(String[] args) {
		if(args.length != 2){
			System.err.println("Usage: consensus uid passwd");
			System.exit(1);
		}
		Date end = new Date();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -15);
		Date begin = cal.getTime();
		RegaDBSettings.createInstance();
		new FromDatabase(args[0],args[1], new AminoAcidDistribution(begin, end, "PI")).run();
	}

}
