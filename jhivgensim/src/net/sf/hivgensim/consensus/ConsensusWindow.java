package net.sf.hivgensim.consensus;

import java.util.Map;

public class ConsensusWindow {

	private ConsensusCalculator consensus;
	
	public ConsensusWindow(){
		this.consensus = new ConsensusCalculator();
	}
	
	public void addSequence(Map<Integer, String> map, String subtype) {
		this.consensus.process(map, subtype);
	}

	public String getConsensus() {
		return this.consensus.getConsensusSequence();
	}
	
}
