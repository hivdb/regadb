package net.sf.hivgensim.consensus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.hivgensim.queries.framework.utils.NtSequenceUtils;
import net.sf.regadb.db.AaSequence;

public class ConsensusCalculator {
	
	private Map<String, Integer> supportPerDataset = new HashMap<String,Integer>();
	private boolean add;
	private String refSequence;
	private Map<Short, Integer> support;
	private Map<Short, Map<Character, Float>> counts;

	public ConsensusCalculator(String ref){
		this.refSequence = ref;
		this.add = true;
		this.support = new HashMap<Short, Integer>();
		this.counts = new HashMap<Short, Map<Character,Float>>();
	}

	private static final List<String> subtypes = Arrays.asList(new String[] {"HIV-1 Subtype A", "HIV-1 Subtype B", "HIV-1 Subtype C"
			, "HIV-1 Subtype D", "HIV-1 Subtype F", "HIV-1 Subtype G", "HIV-1 CRF 02_AG", 
			"HIV-1 CRF 06_CPX", "Other"});

	public static String getSubtypeForConsensus(AaSequence input) {
		String subtype = NtSequenceUtils.getSubtype(input.getNtSequence());
		if(!subtypes.contains(subtype)){
			subtype = "Other";
		}
		return subtype;
	}

	public int getSupport(short position){
		return support.containsKey(position) ? support.get(position) : 0;
	}

	private void adjustSupport(short position){
		int newSupport = this.add ? getSupport(position) + 1 : getSupport(position) - 1;  
		support.put(position, newSupport);			
	}

	private void adjustSupport(short start, short end){
		for(short i = start; i <= end; ++i){
			adjustSupport(i);
		}
	}

	public void process(Map<Short, String> sequence, short start, short end) {
		adjustSupport(start,end);
		for(Entry<Short, String> atPosition : sequence.entrySet()){
			short position = atPosition.getKey();

			if(!counts.containsKey(position) && !"-".equals(atPosition.getValue())){
				counts.put(position, new HashMap<Character, Float>());
			}
			Map<Character, Float> map = counts.get(position);

			float increment = 1f / atPosition.getValue().length();
			for(char m : atPosition.getValue().toCharArray()){
				if(m == '-'){
					assert(atPosition.getValue().length() == 1);
					reverseAdding();
					adjustSupport(position);	
					reverseAdding();
					continue;
				}
				if(refSequence.charAt(position-1)==m){
					continue;
				}

				if(!map.containsKey(m)){
					map.put(m, 0f);
				}

				float oldScore = map.get(m);
				map.put(m, this.add ? oldScore + increment : oldScore - increment);
			}
		}
	}

	public void reverseAdding(){
		this.add = this.add ? false : true;
	}

	public void startAdding(){
		this.add = true;
	}

	public void startRemoving(){
		supportPerDataset.clear();
		this.add = false;
	}

	public String getCurrentConsensusSequence(){
		String result = "";
		for(short pos = 1; pos <= refSequence.length(); pos++){
			if(!counts.containsKey(pos)){
				System.out.print("A");
				result += refSequence.charAt(pos-1);
			} else {
				result += getConsensusAA(pos, counts.get(pos));
			}
		}
		return result;
	}

	private Character getConsensusAA(short position, Map<Character, Float> positionCounts) {
		float max = -1;
		Character maxChar = null;
		float totalCount = 0;
		Character refAA = refSequence.charAt(position-1);
		for(Entry<Character, Float> entry : positionCounts.entrySet()){
			if(entry.getKey().equals(refAA)){
				throw new IllegalStateException();
			}
			if(entry.getValue() > max){
				max = entry.getValue();
				maxChar = entry.getKey();
			}
			totalCount += entry.getValue();
		}
		float voteForReference = ((float) getSupport(position)) - totalCount;
		if(max <= voteForReference){
			max = voteForReference;
			maxChar = refAA; 
		}

		int support = Math.round(max*10 / getSupport(position));
		System.out.print(support == 10 ? "A" : support);

		return maxChar;
	}

	public Map<Short, Map<Character, Float>> getCountsIncludingReference() {
		Map<Short, Map<Character, Float>> result = new HashMap<Short, Map<Character,Float>>();

		for (short i = 1; i <= refSequence.length(); i++) {
			HashMap<Character, Float> resultPos = new HashMap<Character, Float>();
			result.put(i, resultPos);
			Character refAA = refSequence.charAt(i-1);

			if(!counts.containsKey(i)){
				resultPos.put(refAA, (float) getSupport(i));
				continue;
			}

			if(counts.get(i).containsKey(refAA)){
				throw new IllegalStateException("refAA should have been filtered out!");
			}

			resultPos.putAll(counts.get(i));

			float total = 0;
			for(Entry<Character, Float> aa : counts.get(i).entrySet()){
				total += aa.getValue();
			}
			resultPos.put(refAA, getSupport(i) - total);
		}

		return result;
	}

	public int getMaxSupport(){
		int max = 0;
		for(Entry<Short,Integer> e : support.entrySet()){
			if(e.getValue() > max){
				max = e.getValue();
			}
		}
		return max;
	}

	public void process(SimpleSequence sequence) {
		if(supportPerDataset.containsKey(sequence.getDataset())){
			supportPerDataset.put(sequence.getDataset(),supportPerDataset.get(sequence.getDataset()) + 1);
		} else {
			supportPerDataset.put(sequence.getDataset(), 1);
		}
		process(sequence.getMutations(),sequence.getStart(),sequence.getStop());		
	}

	public void printDatasetCounts() {
		System.out.println();
		for(Entry<String, Integer> e : supportPerDataset.entrySet()){
			System.out.println(e.getKey()+" "+e.getValue());
		}
		System.out.println();
	}
	
}
