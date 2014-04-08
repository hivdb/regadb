package net.sf.regadb.ui.form.singlePatient.chart;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.util.settings.PatientChartConfig;
import eu.webtoolkit.jwt.chart.Axis;

public class CellCountSeries extends TestResultSeries {
	private PatientChartConfig.Item item;
	
	public CellCountSeries(PatientChartConfig.Item item, Axis axis) {
		super(null, axis);
		this.item = item;
		setValueType(ValueTypes.NUMBER);
	}

	@Override
	public String getName(){
		return item.name;
	}

	@Override
	public boolean isOk(TestResult tr){
		if(tr.getTestDate() == null || getResults().containsKey(tr.getTestDate()))
			return false;
		
		TestType tt = tr.getTest().getTestType();
		
		PatientChartConfig.TestType ctt = findTestType(tr.getTest().getTestType().getDescription());
		boolean testTypeMatch = ctt != null;
		
		return testTypeMatch;
	}
	
	private PatientChartConfig.TestType findTestType(String description) {
		for (PatientChartConfig.TestType ctt : item.testTypes) {
			if (description.equals(ctt.type)) {
				return ctt;
			}
		}
		return null;
	}
	
	@Override
	public Object getValue(TestResult tr){
		Double val = (Double)super.getValue(tr);
		
		return val;
	}
}
