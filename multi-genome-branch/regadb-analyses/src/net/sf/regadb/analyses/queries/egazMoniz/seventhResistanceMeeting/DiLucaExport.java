package net.sf.regadb.analyses.queries.egazMoniz.seventhResistanceMeeting;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sf.regadb.analyses.queries.egazMoniz.Utils;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;

import org.apache.commons.io.FileUtils;

public class DiLucaExport {
	public static void main(String [] args) throws IOException {
		List<Patient> patients = Utils.getPatients();
		
		File patientsF = new File(args[0]+File.separatorChar+"Patient.csv");
		String patientsS = "patient id, birth date, sex, transmission risk, country of origin, geographic origin \n";
		File cd4F = new File(args[0]+File.separatorChar+"CD4.csv");
		String cd4S = "patient id, date, value\n";
		File vlF = new File(args[0]+File.separatorChar+"VL.csv");
		String vlS = "patient id, date, value\n";
		File seqsF = new File(args[0]+File.separatorChar+"Sequences.csv");
		String seqsS = "patient id, sample id, date, region, sequence\n";
		File therapiesF = new File(args[0]+File.separatorChar+"Therapies.csv");
		String therapiesS = "patient id, start date, stop date, regimen\n";
		
		for(Patient p : patients) {
			boolean positiveTherapyFailure = false;
			for(TestResult tr : p.getTestResults()) {
				if(tr.getTest().getDescription().startsWith("Therapy Failure") && tr.getTestNominalValue().getValue().equals("Positive")) {
					positiveTherapyFailure = true;
					break;
				}
			}
			
			if(positiveTherapyFailure)	{
				patientsS += p.getPatientId() + ", " + p.getBirthDate() + ", " 
								+ getPAV(p, "Gender") + ", " + getPAV(p, "Transmission group") + ", " 
								+  getPAV(p, "Country of origin") + ", " + getPAV(p, "Geographic origin") + "\n";
				
				for(TestResult tr : p.getTestResults()) {
					if(tr.getTest().getDescription().startsWith("Viral Load")) {
						vlS += p.getPatientId() + ", " + tr.getTestDate() + ", " + tr.getValue() + "\n";
					}
					if(tr.getTest().getDescription().startsWith("CD4")) {
						cd4S += p.getPatientId() + ", " + tr.getTestDate() + ", " + tr.getValue() + "\n";
					}
				}
				
				for(ViralIsolate vi : p.getViralIsolates()) {
					for(NtSequence ntseq : vi.getNtSequences()) {
						String regions = "";
						AaSequence[] aaseqs = new AaSequence[ntseq.getAaSequences().size()];
						ntseq.getAaSequences().toArray(aaseqs);
						Arrays.sort(aaseqs, new Comparator<AaSequence>(){
							public int compare(AaSequence aaseq1, AaSequence aaseq2) {
								return aaseq1.getProtein().getAbbreviation().compareTo(aaseq2.getProtein().getAbbreviation());
							}
						});
						for(AaSequence aaseq : aaseqs) {
							regions += aaseq.getProtein().getAbbreviation() + " ";
						}
						seqsS += p.getPatientId() + ", " + vi.getSampleId() + ", " + vi.getSampleDate() + ", " + regions + ", " + ntseq.getNucleotides() + "\n";
					}
				}
				
				for(Therapy t : p.getTherapies()) {
					therapiesS += p.getPatientId() + ", " + t.getStartDate() +  ", " + (t.getStopDate()==null?"":t.getStopDate()) + ", " + getRegimen(t) + "\n";
				}
			}
		}
		
		
		FileUtils.writeStringToFile(patientsF, patientsS);
		FileUtils.writeStringToFile(cd4F, cd4S);
		FileUtils.writeStringToFile(vlF, vlS);
		FileUtils.writeStringToFile(seqsF, seqsS);
		FileUtils.writeStringToFile(therapiesF, therapiesS);
	}
	
	public static String getRegimen(Therapy t) {
		String toReturn = "";
		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
				toReturn += dg.getGenericId() + " ";
			}
		}
		
		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			toReturn += tg.getId().getDrugGeneric().getGenericId() + " ";
		}
		
		return toReturn;
	}
	
	public static String getPAV(Patient p, String attributeName) {
		for(PatientAttributeValue pav : p.getPatientAttributeValues()) {
			if(pav.getAttribute().getName().equals(attributeName)) {
				if(pav.getValue()==null) {
					return pav.getAttributeNominalValue().getValue();
				} else {
					return pav.getValue();
				}
			}
		}
		
		return "";
	}
}
