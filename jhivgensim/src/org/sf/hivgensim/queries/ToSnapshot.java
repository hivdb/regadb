package org.sf.hivgensim.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.hibernate.Hibernate;

import net.sf.regadb.db.*;

public class ToSnapshot extends QueryOutput<Patient> {

	public ToSnapshot(File file){
		super(file);
	}

	@Override
	public void generateOutput(Query<Patient> query) {
		try 
		{
			ObjectOutputStream snapshotstream = new ObjectOutputStream(new FileOutputStream(file));
			for(Patient p : query.getOutputList()){
				write(p);
				snapshotstream.writeObject(p);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private void write(Patient p){
		Hibernate.initialize(p.getPrivileges());
		Hibernate.initialize(p.getPatientEventValues());
		for(PatientEventValue pev : p.getPatientEventValues())
			write(pev);
		Hibernate.initialize(p.getDatasets());
		for(Dataset d : p.getDatasets())
			write(d);
		Hibernate.initialize(p.getTestResults());
		for(TestResult t : p.getTestResults())
			write(t);
		Hibernate.initialize(p.getPatientAttributeValues());
		for(PatientAttributeValue pav : p.getPatientAttributeValues())
			write(pav);
		Hibernate.initialize(p.getViralIsolates());
		for(ViralIsolate vi : p.getViralIsolates())
			write(vi);
		Hibernate.initialize(p.getTherapies());
		for(Therapy th : p.getTherapies())
			write(th);
	}

	private void write(AaInsertion a){
		Hibernate.initialize(a.getId());
		write(a.getId());
	}

	private void write(AaInsertionId a){
		Hibernate.initialize(a.getAaSequence());
	}

	private void write(AaMutation a){
		Hibernate.initialize(a.getId());
		write(a.getId());
	}

	private void write(AaMutationId a){
		Hibernate.initialize(a.getAaSequence());
	}

	private void write(AaSequence a){
		Hibernate.initialize(a.getNtSequence());
    	Hibernate.initialize(a.getProtein());
    	Hibernate.initialize(a.getAaInsertions());
    	for(AaInsertion i : a.getAaInsertions())
    		write(i);    	
    	Hibernate.initialize(a.getAaMutations());
    	for(AaMutation m : a.getAaMutations())
    		write(m);
	}

	private void write(Analysis a){
		Hibernate.initialize(a.getAnalysisType());
    	Hibernate.initialize(a.getTests());
    	Hibernate.initialize(a.getAnalysisDatas());
    	for(AnalysisData ad : a.getAnalysisDatas())
    		write(ad);
	}

	private void write(AnalysisData a){
		Hibernate.initialize(a.getAnalysis());
	}

	private void write(Attribute a){
		Hibernate.initialize(a.getAttributeNominalValues());
    	Hibernate.initialize(a.getValueType());
    	Hibernate.initialize(a.getAttributeGroup());
	}

	private void write(AttributeNominalValue a){
		Hibernate.initialize(a.getAttribute());		
	}

	private void write(Dataset d){
		Hibernate.initialize(d.getDatasetAccesses());
		for(DatasetAccess da : d.getDatasetAccesses())
			write(da);
    	Hibernate.initialize(d.getSettingsUser());
    	write(d.getSettingsUser());
	}

	private void write(DatasetAccess d){
		Hibernate.initialize(d.getId());
		write(d.getId());
	}

	private void write(DatasetAccessId d){
		Hibernate.initialize(d.getDataset());
    	Hibernate.initialize(d.getSettingsUser());
	}

	private void write(DrugClass d){
		Hibernate.initialize(d.getDrugGenerics());
	}

	private void write(DrugCommercial d){
		Hibernate.initialize(d.getDrugGenerics());
		for(DrugGeneric dg : d.getDrugGenerics())
			write(dg);
	}

	private void write(DrugGeneric d){
		Hibernate.initialize(d.getDrugClass());
		write(d.getDrugClass());
    	Hibernate.initialize(d.getDrugCommercials());
	}

	private void write(Event e){
		Hibernate.initialize(e.getEventNominalValues());
		Hibernate.initialize(e.getValueType());    	
	}

	private void write(NtSequence n){
		Hibernate.initialize(n.getViralIsolate());
    	Hibernate.initialize(n.getAaSequences());
    	for(AaSequence a : n.getAaSequences())
    		write(a);
    	Hibernate.initialize(n.getTestResults());
    	for(TestResult t : n.getTestResults())
    		write(t);
	}
	private void write(PatientAttributeValue p){
		Hibernate.initialize(p.getAttribute());
		write(p.getAttribute());
    	Hibernate.initialize(p.getPatient());
    	Hibernate.initialize(p.getAttributeNominalValue());
    	write(p.getAttributeNominalValue());
	}
	
	private void write(PatientEventValue p){
		Hibernate.initialize(p.getEventNominalValue());
		Hibernate.initialize(p.getEvent());
		write(p.getEvent());

	}
	private void write(SettingsUser s){
		Hibernate.initialize(s.getTest());
		if(s.getTest() != null)
			write(s.getTest());
    	Hibernate.initialize(s.getDataset());
    	if(s.getDataset() != null)
    		write(s.getDataset());
    	Hibernate.initialize(s.getDatasetAccesses());
    	for(DatasetAccess da : s.getDatasetAccesses())
    		write(da);
    	Hibernate.initialize(s.getUserAttributes());
    	for(UserAttribute ua : s.getUserAttributes())
    		write(ua);
	}
	
	private void write(Test t){
		if(t.getAnalysis() != null){
		Hibernate.initialize(t.getAnalysis());
		write(t.getAnalysis());
		}
    	Hibernate.initialize(t.getTestType());
    	write(t.getTestType());
	}
	private void write(TestNominalValue t){
		Hibernate.initialize(t.getTestType());
	}

	private void write(TestResult t){
		Hibernate.initialize(t.getTest());
    	Hibernate.initialize(t.getDrugGeneric());
    	Hibernate.initialize(t.getViralIsolate());
    	Hibernate.initialize(t.getTestNominalValue());
    	Hibernate.initialize(t.getPatient());
    	Hibernate.initialize(t.getNtSequence());
	}

	private void write(TestType t){
		Hibernate.initialize(t.getValueType());
    	Hibernate.initialize(t.getTestNominalValues());
    	for(TestNominalValue tnv : t.getTestNominalValues())
    		write(tnv);
    	Hibernate.initialize(t.getTestObject()); 
	}

	private void write(Therapy t){
		Hibernate.initialize(t.getTherapyCommercials());
    	for(TherapyCommercial tc : t.getTherapyCommercials())
    		write(tc);
    	Hibernate.initialize(t.getTherapyGenerics());
    	for(TherapyGeneric tg : t.getTherapyGenerics())
    		write(tg);
    	Hibernate.initialize(t.getTherapyMotivation());
    	Hibernate.initialize(t.getPatient());
	}

	private void write(TherapyCommercial t){
		Hibernate.initialize(t.getId());
		write(t.getId());
	}

	private void write(TherapyCommercialId t){
		Hibernate.initialize(t.getTherapy());
    	Hibernate.initialize(t.getDrugCommercial());
    	write(t.getDrugCommercial());
	}

	private void write(TherapyGeneric t){
		Hibernate.initialize(t.getId());
		write(t.getId());
	}

	private void write(TherapyGenericId t){
		Hibernate.initialize(t.getDrugGeneric());
		write(t.getDrugGeneric());
    	Hibernate.initialize(t.getTherapy());
	}

	private void write(UserAttribute u){
		Hibernate.initialize(u.getValueType());
    	Hibernate.initialize(u.getSettingsUser());
	}

	private void write(ViralIsolate v){
		Hibernate.initialize(v.getNtSequences());
		for(NtSequence n : v.getNtSequences())
			write(n);
    	Hibernate.initialize(v.getTestResults());
    	Hibernate.initialize(v.getPatient());    	
	}		
}
