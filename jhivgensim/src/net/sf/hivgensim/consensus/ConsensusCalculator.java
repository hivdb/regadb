package net.sf.hivgensim.consensus;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.SequenceProteinFilter;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.framework.utils.AaSequenceUtils;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.hivgensim.queries.framework.utils.NtSequenceUtils;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Protein;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ConsensusCalculator implements IQuery<AaSequence> {

	private boolean useReference;
	
	private boolean add;
	private String refSequence;

	private Map<String, Map<Integer, Map<Character, Double>>> countsSubtype;
	private HashMap<Character, Double> emptyMap;
	private static List<String> subtypes;
	
	private ConsensusCalculator(boolean useReference){
		this.countsSubtype = new HashMap<String, Map<Integer, Map<Character,Double>>>();
		this.emptyMap = new HashMap<Character, Double>();
		this.useReference = useReference;
		this.add = true;

		subtypes = Arrays.asList(new String[] {"HIV-1 Subtype A", "HIV-1 Subtype B", "HIV-1 Subtype C"
				, "HIV-1 Subtype D", "HIV-1 Subtype F", "HIV-1 Subtype G", "HIV-1 CRF 02_AG", 
				"HIV-1 CRF 06_CPX", "Other"});
		for(String subtype: subtypes){
			countsSubtype.put(subtype, new HashMap<Integer, Map<Character,Double>>());
		}

		emptyMap.put(new Character('A'), 0d);
		emptyMap.put(new Character('C'), 0d);
		emptyMap.put(new Character('D'), 0d);
		emptyMap.put(new Character('E'), 0d);
		emptyMap.put(new Character('F'), 0d);
		emptyMap.put(new Character('G'), 0d);
		emptyMap.put(new Character('H'), 0d);
		emptyMap.put(new Character('I'), 0d);
		emptyMap.put(new Character('K'), 0d);
		emptyMap.put(new Character('L'), 0d);
		emptyMap.put(new Character('M'), 0d);
		emptyMap.put(new Character('N'), 0d);
		emptyMap.put(new Character('P'), 0d);
		emptyMap.put(new Character('Q'), 0d);
		emptyMap.put(new Character('R'), 0d);
		emptyMap.put(new Character('S'), 0d);
		emptyMap.put(new Character('T'), 0d);
		emptyMap.put(new Character('V'), 0d);
		emptyMap.put(new Character('W'), 0d);
		emptyMap.put(new Character('Y'), 0d);
		emptyMap.put(new Character('*'), 0d);
		emptyMap.put(new Character('-'), 0d);
	}
	
	public ConsensusCalculator(String refSequence){
		this(true);
		this.refSequence = refSequence;
	}

	public ConsensusCalculator() {
		this(false);
	}
	
	public void process(AaSequence input) {
		if(!useReference){
			throw new IllegalStateException("ConsensusCalculator has been constructed without reference. " +
					"It can only process AaSequences if constructed with reference.");
		}
		
		String subtype = getSubtypeForConsensus(input);

		System.out.println(AaSequenceUtils.toString(input, refSequence));

		Map<Integer, String> sequence = AaSequenceUtils.toCharSequence(input, refSequence);
		this.process(sequence, subtype);
	}

	public static String getSubtypeForConsensus(AaSequence input) {
		String subtype = NtSequenceUtils.getSubtype(input.getNtSequence());
		if(!subtypes.contains(subtype)){
			subtype = "Other";
		}
		return subtype;
	}

	public void process(Map<Integer, String> sequence, String subtype) {
		for(Entry<Integer, String> atPosition : sequence.entrySet()){
			Map<Integer, Map<Character, Double>> subMap = countsSubtype.get(subtype);

			int position = atPosition.getKey();
			if(!subMap.containsKey(position)){
				subMap.put(position, (Map<Character, Double>) emptyMap.clone());
			}
			Map<Character, Double> map = subMap.get(position);

			double increment = 1d / atPosition.getValue().length();
			for(char m : atPosition.getValue().toCharArray()){
				double oldScore = map.get(m);
				map.put(m, this.add ? oldScore + increment : oldScore - increment);
			}
		}
	}
	
	public void startAdding(){
		this.add = true;
	}
	
	public void startRemoving(){
		this.add = false;
	}

	public String getConsensusSequence(){
		String result = "";
		Map<Integer, Map<Character, Double>> merged = new HashMap<Integer, Map<Character, Double>>();
		for(Entry<String, Map<Integer, Map<Character, Double>>> subtype: countsSubtype.entrySet()){
			for(Entry<Integer, Map<Character, Double>> countsForPosition: subtype.getValue().entrySet()){
				if(!merged.containsKey(countsForPosition.getKey())){
					merged.put(countsForPosition.getKey(), (Map<Character, Double>) emptyMap.clone());
				}
				Map<Character, Double> positionMerged = merged.get(countsForPosition.getKey());

				for(Entry<Character, Double> aaCount: countsForPosition.getValue().entrySet()){
					positionMerged.put(aaCount.getKey(), positionMerged.get(aaCount.getKey()) + aaCount.getValue());
				}
			}
		}

		for(Entry<Integer, Map<Character, Double>> countsForPosition: merged.entrySet()){
			result += getConsensusAA(countsForPosition.getValue());
		}
		return result + "\n";
	}

	public String getConsensusSequenceFor(String subtype){
		String result = "";
		for(Entry<Integer, Map<Character, Double>> countsForPosition: countsSubtype.get(subtype).entrySet()){
			result += getConsensusAA(countsForPosition.getValue());
		}
		return result + "\n";
	}

	private Character getConsensusAA(Map<Character, Double> positionCounts) {
		double max = -1;
		Character maxChar = null;
		for(Entry<Character, Double> entry : positionCounts.entrySet()){
			if(entry.getValue() > max){
				max = entry.getValue();
				maxChar = entry.getKey();
			}
		}
		if(max==-1){
			throw new IllegalArgumentException();
		}
		return maxChar;
	}

	public void close() {}

	public static void main(String[] args) {
		RegaDBSettings.createInstance();
		Protein protein = DrugGenericUtils.getProteinForDrugClass("PI");
		String ref = SelectionWindow.getWindow(
				protein.getOpenReadingFrame().getGenome().getOrganismName()
				, protein.getOpenReadingFrame().getName(), protein.getAbbreviation())
				.getReferenceAaSequence();
		ConsensusCalculator consensus = new ConsensusCalculator(ref);
		new FromSnapshot(new File("/home/tm/labo/small_snapshot"),
				new GetDrugClassNaiveSequences(new String[] {"PI"},
				new SequenceProteinFilter(protein, consensus))).run();
		consensus.printAllConsensusses();
	}

	public void printAllConsensusses() {
		System.out.println("Consensus:");
		System.out.print(getConsensusSequence());
		for(String subtype : subtypes){
			System.out.println("Consensus for "+subtype);
			System.out.print(getConsensusSequenceFor(subtype));
		}
	}
}
