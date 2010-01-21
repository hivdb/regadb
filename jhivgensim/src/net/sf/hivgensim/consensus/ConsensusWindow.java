package net.sf.hivgensim.consensus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsensusWindow {

	private List<SimpleSequence> sequences;
	private Map<String, List<SimpleSequence>> sequencesPerSubtype;
	
	public ConsensusWindow(){
		this.sequences = new ArrayList<SimpleSequence>();
		this.sequencesPerSubtype = new HashMap<String, List<SimpleSequence>>();
	}
	
	public void addSequence(SimpleSequence sequence, String subtype) {
		sequences.add(sequence);
		if(!sequencesPerSubtype.containsKey(subtype)){
			sequencesPerSubtype.put(subtype, new ArrayList<SimpleSequence>());
		}
		sequencesPerSubtype.get(subtype).add(sequence);
	}
	
	private void processSequences(ConsensusCalculator calculator,
			List<SimpleSequence> sequences) {
		for (SimpleSequence sequence : sequences) {
			calculator.process(sequence.getMutations(), sequence.getStart(), sequence.getStop());
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
