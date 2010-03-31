package net.sf.regadb.ui.form.singlePatient.chart;

import java.util.Comparator;
import java.util.Date;
import java.util.TreeMap;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.db.meta.Equals;
import eu.webtoolkit.jwt.WDate;
import eu.webtoolkit.jwt.chart.Axis;
import eu.webtoolkit.jwt.chart.SeriesType;
import eu.webtoolkit.jwt.chart.WDataSeries;

public class TestResultSeries extends WDataSeries {
	
	@SuppressWarnings("unused")
	private static final Comparator<TestResult> comparator = new Comparator<TestResult>(){
		public int compare(TestResult o1, TestResult o2) {
			return o1.getTestDate().compareTo(o2.getTestDate());
		}
	};
	
	private TestType testType;
	private TreeMap<Date, TestResult> results;
	private ValueTypes valueType;

	public TestResultSeries(TestType testType, Axis axis){
		this(testType, -1, axis);
	}

	private TestResultSeries(TestType testType, int modelColumn, Axis axis){
		super(modelColumn, SeriesType.LineSeries, axis);
		setLegendEnabled(true);
		
		setTestType(testType);
		results = new TreeMap<Date, TestResult>();
	}
	
	public void setTestType(TestType testType) {
		this.testType = testType;
		if(testType != null)
			valueType = ValueTypes.getValueType(testType.getValueType());
	}

	public TestType getTestType() {
		return testType;
	}
	
	public ValueTypes getValueType(){
		return valueType;
	}
	protected void setValueType(ValueTypes valueType){
		this.valueType = valueType;
	}

	public TreeMap<Date, TestResult> loadResults(Patient p){
		results.clear();
		
		for(TestResult tr : p.getTestResults()){
			if(isOk(tr))
				results.put(tr.getTestDate(), tr);
		}
		
		return results;
	}
	
	public boolean isOk(TestResult tr){
		return Equals.isSameTestType(tr.getTest().getTestType(), getTestType());
	}
	
	public TreeMap<Date, TestResult> getResults(){
		return results;
	}
	
	public Object getValue(TestResult tr){
		if(valueType == ValueTypes.NUMBER)
			return Double.parseDouble(tr.getValue());
		if(valueType == ValueTypes.LIMITED_NUMBER)
			return Double.parseDouble(tr.getValue().substring(1));
		else if(valueType == ValueTypes.DATE){
			long l = Long.parseLong(tr.getValue());
			return new WDate(new Date(l));
		}
		else if(valueType == ValueTypes.NOMINAL_VALUE)
			return tr.getTestNominalValue().getValue();
		else
			return tr.getValue();
	}
	
	public String getName(){
		Genome g = getTestType().getGenome();
		return g == null ? getTestType().getDescription() : getTestType().getDescription() + " ("+ g.getOrganismDescription() +")";
	}
}
