package org.sf.hivgensim.queries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;

public class GetNaiveSequences extends QueryInfra {
	Set<Patient> naivePatients = new HashSet<Patient>(0);

	public GetNaiveSequences() {

	}

	@Override
	protected void performQuery(Patient p) {
		for(Therapy t : p.getTherapies()) {
			if(!hasClassExperience("PI", t)) {
				naivePatients.add(p);
			}
		}		
	}
	
	/*
	 * args[0] xmlfile
	 * args[1] loginname
	 * args[2] passwd
	 * args[3] snapshotfile
	 * args[4] outputfile
	 */

	public static void main(String [] args) {
		GetNaiveSequences gns = new GetNaiveSequences();
		long start = System.currentTimeMillis();
		gns.runOnSnapshot(new File(args[0]),args[1],args[2]);
		gns.createSnapshot(args[3], gns.naivePatients);
		gns.createOutput(args[4]);
		long stop = System.currentTimeMillis();
		System.err.println("done" + (stop - start));
	}

	public boolean hasClassExperience(String drugClass, Therapy t) {
		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
				if(dg.getDrugClass().getClassName().equals(drugClass)) {
					return true;
				}
			}
		}

		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			if(tg.getId().getDrugGeneric().getDrugClass().getClassName().equals(drugClass)) {
				return true;
			}
		}

		return false;
	}

	public void createOutput(String filename){
		try {
			PrintStream out = new PrintStream(new FileOutputStream(new File(filename)));
			for(Patient p : naivePatients){
				for(ViralIsolate vi : p.getViralIsolates()){
					for(NtSequence seq : vi.getNtSequences()){
						out.println(p.getPatientId()+","+seq.getNucleotides());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
