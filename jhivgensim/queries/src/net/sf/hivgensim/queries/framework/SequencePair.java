package net.sf.hivgensim.queries.framework;

import net.sf.regadb.db.NtSequence;

public class SequencePair {
	
	private NtSequence seq1;
	private NtSequence seq2;
	
	public SequencePair(NtSequence seq1, NtSequence seq2){
		this.seq1 = seq1;
		this.seq2 = seq2;
	}

	public NtSequence getSeq1() {
		return seq1;
	}

	public NtSequence getSeq2() {
		return seq2;
	}

}
