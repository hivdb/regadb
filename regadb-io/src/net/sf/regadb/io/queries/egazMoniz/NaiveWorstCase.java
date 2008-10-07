package net.sf.regadb.io.queries.egazMoniz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;

public class NaiveWorstCase {

	public static void main(String[] args){
		
		List<Patient> pts = Utils.getPatients();
		List<DrugClass> dcs = Utils.getDrugClasses();
		
		System.out.print("patient id,squences,");
		for(DrugClass dc : dcs){
			System.out.print(","+dc.getClassId());
		}
		System.out.println();
		
		for(Patient p : pts){
			List<ViralIsolate> vis = Utils.getNaiveViralIsolates(p);
			if(vis.size() < 1)
				continue;
			
			ViralIsolate vi = vis.get(0);
			Map<String, Double> dm = new HashMap<String, Double>();
			
			for(TestResult tr : vi.getTestResults()){
				if(tr.getTest().getTestType().getDescription().equals(StandardObjects.getGssId())) {
					DrugClass dc = tr.getDrugGeneric().getDrugClass();
					
					double d = Double.parseDouble(tr.getValue());
					if(!dm.containsKey(dc.getClassId()) || dm.get(dc) < d)
						dm.put(dc.getClassId(), d);
				}
			}
			
			System.out.print(p.getPatientId() +",");
			for(NtSequence seq : vi.getNtSequences()){
				System.out.print(seq.getLabel()+": "+ seq.getNucleotides() +" ");
			}			
			for(DrugClass dc : dcs){
				System.out.print(","+Utils.getSIR(dm.get(dc.getClassId())+""));
			}
			System.out.println();
		}
	}
}
