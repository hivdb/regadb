package net.sf.hivgensim.queries.framework.snapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaInsertionId;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaMutationId;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ViralIsolate;

public class ObjectReplicator {
	
	/*
	 * This class is created to remove all hibernate proxies from the object graph. It copies every object it encounters
	 * starting from a patient. Commonly used objects are kept in hashmaps, to be able to create cycles in the graph and
	 * bidirectional links (e.g. patient=> test result => nt_sequence
	 * 									=> viral isolate => nt_sequence) 
	 */
	
	//global
	private HashMap<Integer,Dataset> datasets = new HashMap<Integer,Dataset>();
	private HashMap<Integer,ValueType> valueTypes = new HashMap<Integer,ValueType>();
	private HashMap<Integer,Attribute> attributes = new HashMap<Integer,Attribute>();
	private HashMap<Integer,AttributeNominalValue> attributeNominalValues = new HashMap<Integer, AttributeNominalValue>();
	private HashMap<Integer,Event> events = new HashMap<Integer,Event>();
	private HashMap<Integer,EventNominalValue> eventNominalValues = new HashMap<Integer,EventNominalValue>();
	private HashMap<Integer,Protein> proteins = new HashMap<Integer,Protein>();
	private HashMap<Integer,OpenReadingFrame> openReadingFrames = new HashMap<Integer, OpenReadingFrame>();
	private HashMap<Integer,Genome> genomes = new HashMap<Integer,Genome>();
	private HashMap<Integer,DrugCommercial> drugCommercials = new HashMap<Integer, DrugCommercial>();
	private HashMap<Integer,DrugGeneric> drugGenerics = new HashMap<Integer, DrugGeneric>();
	private HashMap<Integer,DrugClass> drugClasses = new HashMap<Integer,DrugClass>();
	private HashMap<Integer,Test> tests = new HashMap<Integer, Test>();
	private HashMap<Integer,TestType> testTypes = new HashMap<Integer, TestType>();
	private HashMap<Integer,TestObject> testObjects = new HashMap<Integer, TestObject>();
	private HashMap<Integer,TestNominalValue> testNominalValues = new HashMap<Integer, TestNominalValue>();
	
	//per patient
	private HashMap<Integer,TestResult> trs = new HashMap<Integer, TestResult>();
	private HashMap<Integer,ViralIsolate> vis = new HashMap<Integer, ViralIsolate>();
	private HashMap<Integer,NtSequence> seqs = new HashMap<Integer, NtSequence>();
	
	public Patient copy(Patient patient){
		if(patient == null){
			return null;
		}
		//reset per patient hashmaps
		trs.clear();
		vis.clear();
		seqs.clear();
		
		Patient newPatient = new Patient();
				
		newPatient.setPatientId(patient.getPatientId());
		newPatient.setPatientIi(patient.getPatientIi());
		
    	for(PatientAttributeValue pav : patient.getPatientAttributeValues()){
    		newPatient.addPatientAttributeValue(copy(pav));
    	}
    	for(Dataset pd : patient.getDatasets()){
    		newPatient.addDataset(copy(pd));
    	}
    	for(PatientEventValue pev : patient.getPatientEventValues()){
    		newPatient.addPatientEventValue(copy(pev));
    	}
    	for(ViralIsolate vi : patient.getViralIsolates()){
    		newPatient.addViralIsolate(copy(vi));
    	}
    	for(TestResult tr : patient.getTestResults()){
    		newPatient.addTestResult(copy(tr));
    	}
    	for(Therapy t : patient.getTherapies()){
    		newPatient.addTherapy(copy(t));
    	}
    	return newPatient;
	}	
	
	
	private TestResult copy(TestResult tr) {
		//assumes viral isolates and nt sequences are already copied!
		//and are thus available in the hashmaps
		
		if(tr == null){
			return null;
		}
		TestResult ntr = new TestResult();
		ntr.setData(tr.getData());
		ntr.setDrugGeneric(copy(tr.getDrugGeneric()));
		if(tr.getNtSequence() != null){
			NtSequence seq = seqs.get(tr.getNtSequence().getNtSequenceIi());
			assert(seq != null);
			ntr.setNtSequence(seq);
		}
		if(tr.getViralIsolate() != null){
			ViralIsolate vi = vis.get(tr.getViralIsolate().getViralIsolateIi());
			assert(vi != null);
			ntr.setViralIsolate(vi);
		}
		ntr.setTestResultIi(tr.getTestResultIi());
		ntr.setSampleId(tr.getSampleId());
		ntr.setTest(copy(tr.getTest()));
		ntr.setTestDate(copy(tr.getTestDate()));
		ntr.setTestNominalValue(copy(tr.getTestNominalValue()));
		ntr.setTestResultIi(tr.getTestResultIi());
		ntr.setValue(tr.getValue());		
		return ntr;
	}

	
	private TestNominalValue copy(TestNominalValue testNominalValue) {
		if(testNominalValue == null){
			return null;
		}
		TestNominalValue tnv = testNominalValues.get(testNominalValue.getNominalValueIi());
		if(tnv == null){
			tnv = new TestNominalValue();
			tnv.setValue(testNominalValue.getValue());
			tnv.setTestType(copy(testNominalValue.getTestType()));
		}
		return null;
	}


	private Test copy(Test test) {
		if(test == null){
			return null;
		}
		Test t = tests.get(test.getTestIi());
		if(t == null){
			t = new Test();
			t.setDescription(test.getDescription());
			t.setTestType(copy(test.getTestType()));
//TODO ???	t.setAnalysis(copy(test.getAnalysis()));
			tests.put(test.getTestIi(), t);
		}
		return t;
	}
	
	


	private TestType copy(TestType testType) {
		if(testType == null){
			return null;
		}
		TestType tt = testTypes.get(testType.getTestTypeIi());
		if(tt == null){
			tt = new TestType();
			tt.setDescription(testType.getDescription());
			tt.setGenome(copy(testType.getGenome()));
			tt.setTestObject(copy(testType.getTestObject()));
			tt.setValueType(copy(testType.getValueType()));
			testTypes.put(testType.getTestTypeIi(), tt);
		}
		return tt;
	}


	private TestObject copy(TestObject testObject) {
		if(testObject == null){
			return null;
		}
		TestObject to = testObjects.get(testObject.getTestObjectIi());
		if(to == null){
			to = new TestObject();
			to.setDescription(testObject.getDescription());
			to.setTestObjectId(testObject.getTestObjectId());
			testObjects.put(testObject.getTestObjectIi(),to);
		}
		return to;
	}

	private Therapy copy(Therapy t) {
		if(t == null){
			return null;
		}
		Therapy nt = new Therapy();
		nt.setComment(t.getComment());
		nt.setStartDate(copy(t.getStartDate()));
		nt.setStopDate(copy(t.getStopDate()));
		nt.setTherapyMotivation(t.getTherapyMotivation());
		Set<TherapyCommercial> commercials = new HashSet<TherapyCommercial>();
		for(TherapyCommercial tc : t.getTherapyCommercials()){
			commercials.add(copy(tc));
		}
		nt.setTherapyCommercials(commercials);
		Set<TherapyGeneric> generics = new HashSet<TherapyGeneric>();
		for(TherapyGeneric tg : t.getTherapyGenerics()){
			generics.add(copy(tg));
		}
		nt.setTherapyGenerics(generics);
		return nt;
	}
	
	private TherapyGeneric copy(TherapyGeneric tg) {
		if(tg == null){
			return null;
		}
		TherapyGeneric ntg = new TherapyGeneric();
		ntg.setBlind(tg.isBlind());
		ntg.setPlacebo(tg.isPlacebo());
		ntg.setDayDosageMg(tg.getDayDosageMg());
		ntg.setFrequency(tg.getFrequency());
		ntg.setId(copy(tg.getId()));
		return ntg;
	}


	private TherapyGenericId copy(TherapyGenericId id) {
		if(id == null){
			return null;
		}
		TherapyGenericId tgi = new TherapyGenericId();
		tgi.setDrugGeneric(copy(id.getDrugGeneric()));
		return tgi;
	}


	private TherapyCommercial copy(TherapyCommercial tc) {
		if(tc == null){
			return null;
		}
		TherapyCommercial ntc = new TherapyCommercial();
		ntc.setBlind(tc.isBlind());
		ntc.setDayDosageUnits(tc.getDayDosageUnits());
		ntc.setFrequency(tc.getFrequency());
		ntc.setPlacebo(tc.isPlacebo());
		ntc.setId(copy(tc.getId()));
		return ntc;
	}
	
	private TherapyCommercialId copy(TherapyCommercialId id) {
		TherapyCommercialId nid = new TherapyCommercialId();
		nid.setDrugCommercial(copy(id.getDrugCommercial()));
		return nid;
	}
	
	private DrugCommercial copy(DrugCommercial drugCommercial) {
		if(drugCommercial == null){
			return null;
		}
		DrugCommercial dc = drugCommercials.get(drugCommercial.getCommercialIi());
		if(dc == null){
			dc = new DrugCommercial();
			dc.setAtcCode(drugCommercial.getAtcCode());
			dc.setName(drugCommercial.getName());
			Set<DrugGeneric> dgs = new HashSet<DrugGeneric>();
			for(DrugGeneric dg : drugCommercial.getDrugGenerics()){
				dgs.add(copy(dg));
			}
			dc.setDrugGenerics(dgs);
			drugCommercials.put(drugCommercial.getCommercialIi(),dc);
		}		
		return dc;
	}

	private DrugGeneric copy(DrugGeneric dg) {
		if(dg == null){
			return null;
		}
		DrugGeneric ndg = drugGenerics.get(dg.getGenericIi());
		if(ndg == null){
			ndg = new DrugGeneric();
			drugGenerics.put(dg.getGenericIi(), ndg);
			ndg.setAtcCode(dg.getAtcCode());
			ndg.setGenericId(dg.getGenericId());
			ndg.setGenericName(dg.getGenericName());
			ndg.setResistanceTableOrder(dg.getResistanceTableOrder());
			ndg.setDrugClass(copy(dg.getDrugClass()));
			Set<Genome> gens = new HashSet<Genome>();
			for(Genome g : dg.getGenomes()){
				gens.add(copy(g));
			}
			ndg.setGenomes(gens);
			Set<DrugCommercial> commercials = new HashSet<DrugCommercial>();
			for(DrugCommercial dc : dg.getDrugCommercials()){
				commercials.add(copy(dc));
			}
			ndg.setDrugCommercials(commercials);
		}
		return ndg;
	}
	
	private DrugClass copy(DrugClass drugClass) {
		if(drugClass == null){
			return null;
		}
		DrugClass ndc = drugClasses.get(drugClass.getDrugClassIi());
		if(ndc == null){
			ndc = new DrugClass();
			ndc.setClassId(drugClass.getClassId());
			ndc.setClassName(drugClass.getClassName());
			ndc.setResistanceTableOrder(drugClass.getResistanceTableOrder());
			Set<DrugGeneric> generics = new HashSet<DrugGeneric>();
			for(DrugGeneric dg : drugClass.getDrugGenerics()){
				generics.add(copy(dg));
			}
			ndc.setDrugGenerics(generics);
			drugClasses.put(drugClass.getDrugClassIi(), ndc);
		}
		return ndc;
	}


	private ViralIsolate copy(ViralIsolate vi) {
		if(vi == null){
			return null;
		}
		ViralIsolate newViralIsolate = new ViralIsolate();
		vis.put(vi.getViralIsolateIi(),newViralIsolate);
		newViralIsolate.setSampleDate(copy(vi.getSampleDate()));
		newViralIsolate.setSampleId(vi.getSampleId());
		Set<TestResult> temptrs = new HashSet<TestResult>();
		for(TestResult tr : vi.getTestResults()){
			temptrs.add(copy(tr));
		}
		newViralIsolate.setTestResults(temptrs);
		Set<NtSequence> tempseqs = new HashSet<NtSequence>();
		for(NtSequence seq : vi.getNtSequences()){
			tempseqs.add(copy(seq));
		}
		newViralIsolate.setNtSequences(tempseqs);
		return newViralIsolate;
	}
	
	

	private NtSequence copy(NtSequence seq) {
		if(seq == null){
			return null;
		}
		NtSequence nseq = new NtSequence();
		seqs.put(seq.getNtSequenceIi(),nseq);
		nseq.setNtSequenceIi(seq.getNtSequenceIi());
		nseq.setLabel(seq.getLabel());
		nseq.setNucleotides(seq.getNucleotides());
		nseq.setSequenceDate(copy(seq.getSequenceDate()));
		Set<TestResult> temptrs = new HashSet<TestResult>();
		for(TestResult tr : seq.getTestResults()){
			temptrs.add(copy(tr));
		}
		nseq.setTestResults(temptrs);
		Set<AaSequence> tempaaseqs = new HashSet<AaSequence>();
		for(AaSequence aaseq : seq.getAaSequences()){
			tempaaseqs.add(copy(aaseq));
		}
		return nseq;
	}
	
	

	

	private AaSequence copy(AaSequence aaseq) {
		if(aaseq == null){
			return null;
		}
		AaSequence newseq = new AaSequence();
		newseq.setFirstAaPos(aaseq.getFirstAaPos());
		newseq.setLastAaPos(aaseq.getLastAaPos());
		newseq.setProtein(copy(aaseq.getProtein()));
		HashSet<AaInsertion> insertions = new HashSet<AaInsertion>();
		for(AaInsertion ins : aaseq.getAaInsertions()){
			insertions.add(copy(ins));
		}
		newseq.setAaInsertions(insertions);
		HashSet<AaMutation> mutations = new HashSet<AaMutation>();
		for(AaMutation mut : aaseq.getAaMutations()){
			mutations.add(copy(mut));
		}
		newseq.setAaMutations(mutations);
		return newseq;
	}

	private AaMutation copy(AaMutation mut) {
		if(mut == null){
			return null;
		}
		AaMutation nmut = new AaMutation();
		nmut.setAaMutation(mut.getAaMutation());
		nmut.setAaReference(mut.getAaReference());
		nmut.setNtMutationCodon(mut.getNtMutationCodon());
		nmut.setNtReferenceCodon(mut.getNtReferenceCodon());
		nmut.setId(copy(mut.getId()));
		return nmut;
	}
	
	

	private AaMutationId copy(AaMutationId id) {
		if(id == null){
			return null;
		}
		AaMutationId nid = new AaMutationId();
		nid.setMutationPosition(id.getMutationPosition());
		return nid;
	}

	private AaInsertion copy(AaInsertion ins) {
		if(ins == null){
			return null;
		}
		AaInsertion nins = new AaInsertion();
		nins.setAaInsertion(ins.getAaInsertion());
		nins.setNtInsertionCodon(ins.getNtInsertionCodon());
		nins.setId(copy(ins.getId()));
		return nins;
	}	

	private AaInsertionId copy(AaInsertionId id) {
		if(id == null){
			return null;
		}
		AaInsertionId nid = new AaInsertionId();
		nid.setInsertionOrder(id.getInsertionOrder());
		nid.setInsertionPosition(id.getInsertionPosition());
		return nid;
	}

	private Protein copy(Protein protein) {
		if(protein == null){
			return null;
		}
		Protein nprot = proteins.get(protein.getProteinIi());
		if(nprot == null){
			nprot = new Protein();
			nprot.setAbbreviation(protein.getAbbreviation());
			nprot.setFullName(protein.getFullName());
			nprot.setOpenReadingFrame(copy(protein.getOpenReadingFrame()));
			nprot.setStartPosition(protein.getStartPosition());
			nprot.setStopPosition(protein.getStopPosition());
			proteins.put(protein.getProteinIi(), nprot);
		}
		return nprot;
	}

	private OpenReadingFrame copy(OpenReadingFrame openReadingFrame) {
		if(openReadingFrame == null){
			return null;
		}
		OpenReadingFrame norf = openReadingFrames.get(openReadingFrame.getOpenReadingFrameIi());
		if(norf == null){
			norf = new OpenReadingFrame();
			norf.setDescription(openReadingFrame.getDescription());
			norf.setName(openReadingFrame.getName());
			norf.setGenome(copy(openReadingFrame.getGenome()));
			norf.setReferenceSequence(openReadingFrame.getReferenceSequence());
			openReadingFrames.put(openReadingFrame.getOpenReadingFrameIi(), norf);
		}
		return norf;
	}

	private Genome copy(Genome genome) {
		if(genome == null){
			return null;
		}
		Genome ngen = genomes.get(genome.getGenomeIi());
		if(ngen == null){
			ngen = new Genome();
			ngen.setOrganismDescription(genome.getOrganismDescription());
			ngen.setOrganismName(genome.getOrganismName());
			ngen.setGenbankNumber(genome.getGenbankNumber());
			genomes.put(genome.getGenomeIi(),ngen);
		}
		return ngen;
	}

	private PatientEventValue copy(PatientEventValue pev) {
		if(pev == null){
			return null;
		}
		PatientEventValue newPatientEventValue = new PatientEventValue();
		newPatientEventValue.setStartDate(copy(pev.getStartDate()));
		newPatientEventValue.setEndDate(copy(pev.getEndDate()));
		newPatientEventValue.setValue(pev.getValue());
		newPatientEventValue.setEvent(copy(pev.getEvent()));
		newPatientEventValue.setEventNominalValue(copy(pev.getEventNominalValue()));
		return newPatientEventValue;
	}

	private EventNominalValue copy(EventNominalValue eventNominalValue) {
		if(eventNominalValue == null){
			return null;
		}
		EventNominalValue newEventNominalValue = eventNominalValues.get(eventNominalValue.getNominalValueIi());
		if(newEventNominalValue == null){
			newEventNominalValue = new EventNominalValue();
			newEventNominalValue.setValue(eventNominalValue.getValue());
			eventNominalValues.put(eventNominalValue.getNominalValueIi(), newEventNominalValue);
		}
		return newEventNominalValue;
	}

	private Event copy(Event event) {
		if(event == null){
			return null;
		}
		Event newEvent = events.get(event.getEventIi());
		if(newEvent == null){
			newEvent = new Event();
			newEvent.setValueType(copy(event.getValueType()));
			newEvent.setName(event.getName());
			events.put(event.getEventIi(),newEvent);
		}
		return newEvent;
	}

	private Date copy(Date date) {
		if(date == null){
			return null;
		}
		return new Date(date.getTime());
	}

	private Dataset copy(Dataset dataset){
		if(dataset == null){
			return null;
		}
		Dataset newDataset = datasets.get(dataset.getDatasetIi());
		if(newDataset == null){
			newDataset = new Dataset();
			newDataset.setDescription(dataset.getDescription());
			datasets.put(newDataset.getDatasetIi(),newDataset);
		}
		return newDataset;
	}
	
	private PatientAttributeValue copy(PatientAttributeValue patientAttributeValue){
		if(patientAttributeValue == null){
			return null;
		}
		PatientAttributeValue newPatientAttributeValue = new PatientAttributeValue();
		newPatientAttributeValue.setValue(patientAttributeValue.getValue());
		newPatientAttributeValue.setAttribute(copy(patientAttributeValue.getAttribute()));
		newPatientAttributeValue.setAttributeNominalValue(copy(patientAttributeValue.getAttributeNominalValue()));
		return newPatientAttributeValue;
	}
	
	private AttributeNominalValue copy(AttributeNominalValue attributeNominalValue) {
		if(attributeNominalValue == null){
			return null;
		}
		AttributeNominalValue newAttributeNominalValue = attributeNominalValues.get(attributeNominalValue.getNominalValueIi());
		if(newAttributeNominalValue == null){
			newAttributeNominalValue = new AttributeNominalValue();
			newAttributeNominalValue.setValue(attributeNominalValue.getValue());
			attributeNominalValues.put(newAttributeNominalValue.getNominalValueIi(), newAttributeNominalValue);
		}
		return newAttributeNominalValue;
	}

	private Attribute copy(Attribute attribute){
		if(attribute == null){
			return null;
		}
		Attribute newAttribute = attributes.get(attribute.getAttributeIi());
		if(newAttribute == null){
			newAttribute = new Attribute();
			newAttribute.setName(attribute.getName());
			newAttribute.setValueType(copy(attribute.getValueType()));
			attributes.put(newAttribute.getAttributeIi(), newAttribute);			
		}
		return newAttribute;
	}

	private ValueType copy(ValueType valueType) {
		if(valueType == null){
			return null;
		}
		ValueType newValueType = valueTypes.get(valueType.getValueTypeIi());
		if(newValueType == null){
			newValueType = new ValueType();
			newValueType.setDescription(valueType.getDescription());
			newValueType.setMaximum(valueType.getMaximum());
			newValueType.setMinimum(valueType.getMinimum());
			newValueType.setMultiple(valueType.getMultiple());
			valueTypes.put(newValueType.getValueTypeIi(),newValueType);
		}		
		return newValueType;
	}	
	
	
	

}
