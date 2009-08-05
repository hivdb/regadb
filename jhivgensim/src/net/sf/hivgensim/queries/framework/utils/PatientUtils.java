package net.sf.hivgensim.queries.framework.utils;

import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;

public class PatientUtils {
	
	public static Set<NtSequence> getSequences(Patient p){
		Set<NtSequence> set = new HashSet<NtSequence>();
		for(ViralIsolate vi : p.getViralIsolates()){
			set.addAll(vi.getNtSequences());
		}
		return set;
	}

}
