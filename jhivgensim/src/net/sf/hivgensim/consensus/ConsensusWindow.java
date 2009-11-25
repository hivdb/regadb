package net.sf.hivgensim.consensus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsensusWindow {

	private List<Map<Short, String>> sequences;
	private Map<String, List<Map<Short, String>>> sequencesPerSubtype;
	
	public ConsensusWindow(){
		this.sequences = new ArrayList<Map<Short,String>>();
		this.sequencesPerSubtype = new HashMap<String, List<Map<Short,String>>>();
	}
	
	public void addSequence(Map<Short, String> mutations, String subtype) {
		sequences.add(mutations);
		if(!sequencesPerSubtype.containsKey(subtype)){
			sequencesPerSubtype.put(subtype, new ArrayList<Map<Short,String>>());
		}
		sequencesPerSubtype.get(subtype).add(mutations);
	}
	
	private void processSequences(ConsensusCalculator calculator,
			List<Map<Short, String>> sequences) {
		for (Map<Short, String> mutations : sequences) {
			calculator.process(mutations);
		}
	}
	
	public void beginConsensus(ConsensusCalculator calculator){
		if(this.sequences.isEmpty()){
			return;
		}
		calculator.startAdding();
		processSequences(calculator, sequences);
	}

	public void endConsensus(ConsensusCalculator calculator){
		if(this.sequences.isEmpty()){
			return;
		}
		calculator.startRemoving();
		processSequences(calculator, sequences);
	}
	
	public void beginConsensusFor(String subtype, ConsensusCalculator calculator){
		if(!sequencesPerSubtype.containsKey(subtype)){
			return;
		}
		calculator.startAdding();
		processSequences(calculator, sequencesPerSubtype.get(subtype));
	}

	public void endConsensusFor(String subtype, ConsensusCalculator calculator){
		if(!sequencesPerSubtype.containsKey(subtype)){
			return;
		}
		calculator.startRemoving();
		processSequences(calculator, sequencesPerSubtype.get(subtype));
	}

	public int getSequenceCount() {
		return sequences.size();
	}
	
	public int getSequenceCountFor(String subtype) {
		if(!sequencesPerSubtype.containsKey(subtype)){
			return 0;
		}
		return sequencesPerSubtype.get(subtype).size();
	}
}
