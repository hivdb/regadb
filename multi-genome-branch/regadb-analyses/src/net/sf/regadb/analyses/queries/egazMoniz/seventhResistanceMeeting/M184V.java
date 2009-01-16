package net.sf.regadb.analyses.queries.egazMoniz.seventhResistanceMeeting;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.regadb.analyses.queries.egazMoniz.Utils;
import net.sf.regadb.db.AaMutation;
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
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;

public class M184V {
	public static void main(String [] args) {
		M184V q = new M184V();
		q.run();
	}
	
	public void run() {
		System.out.println("patient id, regimen, therapy stop date, viral load date, viral load, AZT mutations, "+
							"TFV mutations, M184 mutation, # EFV mutations REGAv71, birthdate, sex");
		
		List<Patient> patients = Utils.getPatients();
		for(Patient p : patients) {
			if(p.getTherapies().size()>0) {
				Therapy t = getFirstTherapy(p);
				String regimen = null ;
				if(therapyContains(t, "AZT") && therapyContains(t, "3TC") && therapyContains(t, "EFV")) {
					regimen = "AZT+3TC+EFV";
				} else if(therapyContains(t, "TDF") && therapyContains(t, "FTC") && therapyContains(t, "EFV")) {
					regimen = "TDF+FTC+EFV";
				}

				ViralIsolate vi = null;
				if(t.getStopDate()!=null)
					vi = getFirstViralIsolateAfter(p, t.getStopDate());
				
				if(vi==null && t.getStopDate()!=null) {
					//System.err.println("No viral isolate avialable for patient " + p.getPatientId());
				}

				if(regimen!=null && t.getStopDate()!=null && vi!=null) {
					TestResult failure = getFirstTestAfter(p, t.getStopDate(), "Therapy Failure");
					TestResult vl = getFirstTestAfter(p, t.getStopDate(), "Viral Load");

					if(failure!=null && failure.getTestNominalValue().getValue().equals("Positive")) {
						System.out.print(p.getPatientId() + ", ");
						System.out.print(regimen + ", ");
						System.out.print(t.getStopDate() + ", ");
						if(vl!=null) {
							System.out.print(vl.getTestDate() + ", ");
							System.out.print(vl.getValue() + ", ");
						} else {
							System.out.print(", ");
							System.out.print(", ");
						}
						
						StringBuilder aztMutations = new StringBuilder();
						containtsMutation(vi, "RT", 41, "L", aztMutations);
						containtsMutation(vi, "RT", 67, "N", aztMutations);
						containtsMutation(vi, "RT", 70, "R", aztMutations);
						containtsMutation(vi, "RT", 210, "W", aztMutations);
						containtsMutation(vi, "RT", 215, "Y", aztMutations);
						containtsMutation(vi, "RT", 215, "F", aztMutations);
						containtsMutation(vi, "RT", 219, "E", aztMutations);
						containtsMutation(vi, "RT", 219, "Q", aztMutations);
						System.out.print(aztMutations.toString() + ", ");
						
						StringBuilder tfvMutations = new StringBuilder();
						containtsMutation(vi, "RT", 65, "R", tfvMutations);
						containtsMutation(vi, "RT", 70, "E", tfvMutations);
						System.out.print(tfvMutations.toString() + ", ");
						
						StringBuilder m184Mutations = new StringBuilder();
						containtsMutation(vi, "RT", 184, "V", m184Mutations);
						containtsMutation(vi, "RT", 184, "I", m184Mutations);
						System.out.print(m184Mutations.toString() + ", ");
						
						System.out.print(numberOfNNRTIMutations(vi).toString() + ", ");
						
						System.out.print(p.getBirthDate() + ", ");
						System.out.println(getPAV(p, "Gender"));
					}
				}
			}
		}
	}
	
	public String getPAV(Patient p, String attributeName) {
		for(PatientAttributeValue pav : p.getPatientAttributeValues()) {
			if(pav.getAttribute().getName().equals(attributeName)) {
				if(pav.getValue()==null) {
					return pav.getAttributeNominalValue().getValue();
				} else {
					return pav.getValue();
				}
			}
		}
		
		return null;
	}

	public StringBuilder numberOfNNRTIMutations(ViralIsolate vi) {
		final StringBuilder amountMutations = new StringBuilder();
		
		for(TestResult tr : vi.getTestResults()) {
			if(tr.getTest().getDescription().equals("REGA v7.1") 
					&& tr.getDrugGeneric().getGenericId().equals("EFV")) {
		        ResistanceInterpretationParser inp = new ResistanceInterpretationParser()
		        {
		            @Override
		            public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) 
		            {
		            	amountMutations.append(mutations.size());
		            }
		        };
		        try 
		        {
		            inp.parse(new InputSource(new ByteArrayInputStream(tr.getData())));
		        } 
		        catch (SAXException e) 
		        {
		            e.printStackTrace();
		        } 
		        catch (IOException e) 
		        {
		            e.printStackTrace();
		        }
			}
		}
		
		return amountMutations;
	}
	
	public void containtsMutation(ViralIsolate vi, String protein, int position, String mutation, StringBuilder sb) {
		for(NtSequence ntseq : vi.getNtSequences()) {
			for(AaSequence aaseq : ntseq.getAaSequences()) {
				if(aaseq.getProtein().getAbbreviation().equals(protein)) {
					for(AaMutation aamut : aaseq.getAaMutations()) {
						if(aamut.getId().getMutationPosition()==position && aamut.getAaMutation().contains(mutation)) { 
							sb.append(aamut.getAaReference() + position + mutation + " ");
							break;
						}
					}
				}
			}
		}
	}
	
	public ViralIsolate getFirstViralIsolateAfter(Patient p, Date d) {
		ViralIsolate[] viralIsolates = new ViralIsolate[p.getViralIsolates().size()];
		p.getViralIsolates().toArray(viralIsolates);
		Arrays.sort(viralIsolates, new Comparator<ViralIsolate>(){
			public int compare(ViralIsolate vi1, ViralIsolate vi2) {
				return vi1.getSampleDate().compareTo(vi2.getSampleDate());
			}
		});
		
		for(ViralIsolate vi : viralIsolates) {
			if(vi.getSampleDate().after(d)) 
				return vi;
		}
		
		return null;
	}
	
	public TestResult getFirstTestAfter(Patient p, Date d, String testTypeName) {
		TestResult[] results = new TestResult[p.getTestResults().size()];
		p.getTestResults().toArray(results);
		Arrays.sort(results, new Comparator<TestResult>(){
			public int compare(TestResult tr1, TestResult tr2) {
				return tr1.getTestDate().compareTo(tr2.getTestDate());
			}
		});
		
		for(TestResult tr : results) {
			if(tr.getTest().getTestType().getDescription().startsWith(testTypeName) && tr.getTestDate().after(d)) {
				return tr;
			}
		}
		
		return null;
	}
	
	public boolean therapyContains(Therapy t, String genericAbbrev) {
		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
				if(dg.getGenericId().equals(genericAbbrev)) {
					return true;
				}
			}
		}
		
		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			if(tg.getId().getDrugGeneric().getGenericId().equals(genericAbbrev)) {
				return true;
			}
		}
		
		return false;
	}
	
	public Therapy getFirstTherapy(Patient p) {
		List<Therapy> therapies = new ArrayList<Therapy>();
		
		for(Therapy t : p.getTherapies()) {
			therapies.add(t);
		}
		
		Collections.sort(therapies, new Comparator<Therapy>(){
			@Override
			public int compare(Therapy t1, Therapy t2) {
				return t1.getStartDate().compareTo(t2.getStartDate());
			}
		});
		
		return therapies.get(0);
	}
}
