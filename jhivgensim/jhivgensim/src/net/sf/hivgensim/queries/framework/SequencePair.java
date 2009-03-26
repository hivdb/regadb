package net.sf.hivgensim.queries.framework;

import net.sf.regadb.db.NtSequence;

public class SequencePair {
	
	private String patientId;
	private NtSequence seq1;
	private NtSequence seq2;
	private String therapyRegimen;
	
	public SequencePair(String patientId, NtSequence seq1, NtSequence seq2, String therapyRegimenInBetween){
		this.patientId = patientId;
		this.seq1 = seq1;
		this.seq2 = seq2;
		this.therapyRegimen = therapyRegimenInBetween;
	}

	public NtSequence getSeq1() {
		return seq1;
	}

	public NtSequence getSeq2() {
		return seq2;
	}
	
	public String getTherapyRegimen() {
		return therapyRegimen;
	}
	
	public String getPatientId(){
		return patientId;		
	}
	
	public boolean inRegion(String organism, String protein){
		return QueryUtils.isSequenceInRegion(getSeq1(), organism, protein)
			&& QueryUtils.isSequenceInRegion(getSeq2(), organism, protein);
	}

}
