package net.sf.regadb.io.db.util.mapping;

import java.util.HashMap;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestType;
import net.sf.regadb.io.util.StandardObjects;

public class OfflineObjectStore extends ObjectStore{
	HashMap<String, Dataset> datasets = new HashMap<String, Dataset>();
	HashMap<String, Patient> patients = new HashMap<String, Patient>();
	HashMap<String, Attribute> attributes = new HashMap<String, Attribute>();
	HashMap<String, AttributeNominalValue> attributeNominalValues = new HashMap<String, AttributeNominalValue>();
	HashMap<String, Genome> genomes = new HashMap<String, Genome>();
	HashMap<String, TestType> testTypes = new HashMap<String, TestType>();
	HashMap<String, Test> tests = new HashMap<String, Test>();
	HashMap<String, TestNominalValue> testNominalValues = new HashMap<String, TestNominalValue>();
	
	public OfflineObjectStore(){
		loadStandardObjects();
	}
	
	private void loadStandardObjects(){
		for(Attribute a : StandardObjects.getAttributes()){
			attributes.put(key(a), a);
			for(AttributeNominalValue anv : a.getAttributeNominalValues())
				attributeNominalValues.put(key(anv), anv);
		}
		
		for(Genome g : StandardObjects.getGenomes())
			genomes.put(key(g), g);
		
		for(TestType tt : StandardObjects.getTestTypes()){
			testTypes.put(key(tt), tt);
			for(TestNominalValue tnv : tt.getTestNominalValues())
				testNominalValues.put(key(tnv), tnv);
		}
		
		for(Test t : StandardObjects.getTests())
			tests.put(key(t), t);
	}

	@Override
	public Patient createPatient(Dataset dataset, String id) {
		Patient p = new Patient();
		p.addDataset(dataset);
		p.setPatientId(id);
		patients.put(key(p),p);
		return p;
	}

	@Override
	public Attribute getAttribute(String name, String group) {
		return attributes.get(implode(group,name));
	}

	@Override
	public AttributeNominalValue getAttributeNominalValue(Attribute attribute,
			String value) {
		return attributeNominalValues.get(implode(key(attribute),value));
	}

	@Override
	public Dataset getDataset(String description) {
		return datasets.get(description);
	}

	@Override
	public Patient getPatient(Dataset dataset, String id) {
		return patients.get(id);
	}

	@Override
	public Test getTest(String description, String testTypeDescription,
			String organismName) {
		return tests.get(implode(organismName,testTypeDescription,description));
	}

	@Override
	public TestNominalValue getTestNominalValue(TestType testType, String value) {
		return testNominalValues.get(implode(key(testType),value));
	}

	@Override
	public TestType getTestType(String description, String organismName) {
		return testTypes.get(implode(organismName,description));
	}
	
	public Test createTest(TestType testType, String description){
		Test t = super.createTest(testType, description);
		tests.put(key(t), t);
		return t;
	}
	
	public AttributeNominalValue createAttributeNominalValue(Attribute attribute, String value){
		AttributeNominalValue anv = super.createAttributeNominalValue(attribute, value);
		attributeNominalValues.put(key(anv), anv);
		return anv;
	}
	
	public TestNominalValue createTestNominalValue(TestType testType, String value){
		TestNominalValue tnv = super.createTestNominalValue(testType, value);
		testNominalValues.put(key(tnv), tnv);
		return tnv;
	}
	
	private String implode(String... strings){
		StringBuilder sb = new StringBuilder();
		for(String s : strings)
			sb.append(s == null ? "" : s).append('\t');
		return sb.toString();
	}
	
	private String key(Patient p){
		return p.getPatientId();
	}
	private String key(Attribute a){
		return implode(a.getAttributeGroup().getGroupName(),a.getName());
	}
	private String key(AttributeNominalValue anv){
		return implode(key(anv.getAttribute()),anv.getValue());
	}
	private String key(TestType tt){
		return implode(key(tt.getGenome()),tt.getDescription());
	}
	private String key(Test t){
		return implode(key(t.getTestType()),t.getDescription());
	}
	private String key(TestNominalValue tnv){
		return implode(key(tnv.getTestType()),tnv.getValue());
	}
	private String key(Genome g){
		return g == null ? "":g.getOrganismName();
	}
}
