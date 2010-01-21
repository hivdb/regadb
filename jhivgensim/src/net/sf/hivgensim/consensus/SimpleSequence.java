package net.sf.hivgensim.consensus;

import java.util.Map;

public class SimpleSequence {
	
	private short start;
	private short stop;
	private Map<Short,String> mutations;
	
	public SimpleSequence(short start, short stop, Map<Short, String> mutations){
		this.start = start;
		this.stop = stop;
		this.mutations = mutations;
	}

	public short getStart() {
		return start;
	}

	public void setStart(short start) {
		this.start = start;
	}

	public short getStop() {
		return stop;
	}

	public void setStop(short stop) {
		this.stop = stop;
	}

	public Map<Short, String> getMutations() {
		return mutations;
	}

	public void setMutations(Map<Short, String> mutations) {
		this.mutations = mutations;
	}

}
