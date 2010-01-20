package net.sf.hivgensim.consensus;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

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
				if(i!=0){
					System.out.print(", ");
				} else {
					System.out.print("position, ");
				}
				System.out.print(possibleAA.get(i));
			}
			System.out.println();
			for (Entry<Short, Map<Character, Float>> position : counts.entrySet()) {
				Map<Character, Float> posMap = position.getValue();
				for (int i = 0; i < possibleAA.size(); i++) {
					if(i!=0){
						System.out.print(", ");
					} else {
						System.out.print(""+position.getKey()+", ");
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
	
	public static void main(String[] args) throws ParseException {
		if(args.length != 1){
			System.err.println("Usage: consensus snapshot");
			System.exit(1);
		}
		String year = "2008";
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date begin = sdf.parse("01-01-"+year);
		Date end = sdf.parse("31-12-"+year);
		RegaDBSettings.createInstance();
		new FromSnapshot(new File(args[0]),
				new AminoAcidDistribution(begin, end, "RTI")).run();
	}

}
