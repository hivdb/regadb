package net.sf.hivgensim.pr;

import java.util.ArrayList;
import java.util.Set;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;

public class SequenceExperience {
	
	private NtSequence sequence;
	private Set<DrugGeneric> experience;
	
	public SequenceExperience(NtSequence seq, Set<DrugGeneric> history) {
		this.sequence = seq;
		this.experience = history; 
	}
	public NtSequence getSequence() {
		return sequence;
	}
	public void setSequence(NtSequence sequence) {
		this.sequence = sequence;
	}
	public Set<DrugGeneric> getExperience() {
		return experience;
	}
	public void setExperience(Set<DrugGeneric> experience) {
		this.experience = experience;
	}
	public ArrayList<String> getInfoCols(){
		ArrayList<String> info = new ArrayList<String>();
		for(DrugGeneric dg : experience){
			info.add(dg.getGenericId());
		}
		return info;
	}

	
	

}
