package net.sf.hivgensim.queries.framework.datatypes;

import java.util.HashMap;
import java.util.Set;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;

public class SequencePair {
	
	private Patient patient;
	private NtSequence seq1;
	private NtSequence seq2;
	private String therapyRegimen;
	private HashMap<String,String> resistanceNaive = new HashMap<String, String>();
	private HashMap<String,String> resistanceTreated = new HashMap<String, String>();
	
	public Set<String> getDrugs(){
		return resistanceNaive.keySet();
	}
	
	public String getNaiveResistance(String drug){
		return resistanceNaive.get(drug);
	}
	
	public String getTreatedResistance(String drug){
		return resistanceTreated.get(drug);
	}
	
	public SequencePair(Patient patient, NtSequence seq1, NtSequence seq2, String therapyRegimenInBetween){
		this.patient = patient;
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
	
	public Patient getPatient(){
		return patient;
	}
	
	public void addResistance(String drug, String naiveResistance, String treatedResistance){
		resistanceNaive.put(drug,naiveResistance);
		resistanceTreated.put(drug,treatedResistance);
	}
	

}
