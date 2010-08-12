package net.sf.regadb.ui.form.singlePatient.chart;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import eu.webtoolkit.jwt.WBrush;
import eu.webtoolkit.jwt.WColor;
import eu.webtoolkit.jwt.chart.Axis;
import eu.webtoolkit.jwt.chart.MarkerType;
import eu.webtoolkit.jwt.chart.SeriesType;

public class LimitedValueSeries extends TestResultSeries {
	public class CutOffSeries extends TestResultSeries{
		private LimitedValueSeries parent;
		
		protected CutOffSeries(LimitedValueSeries parent) {
			super(parent.getTestType(), SeriesType.PointSeries, parent.getAxis());
			this.parent = parent;

			setMarker(MarkerType.CircleMarker);
			setMarkerBrush(new WBrush(WColor.white));
			
			setLegendEnabled(false);
		}
		
		public String getName(){
			return parent.getName() +" (Cutoff)";
		}
		
		public Object getValue(TestResult tr){
			return parent.getValue(tr);
		}
	}
	
	private CutOffSeries cutOffSeries;

	public LimitedValueSeries(TestType testType, Axis axis) {
		super(testType, axis);
		
		cutOffSeries = new CutOffSeries(this);
	}
	
	protected void addResult(TestResult tr){
		super.addResult(tr);
		if(isCutoffValue(tr))
			cutOffSeries.addResult(tr);
	}
	
	protected boolean isCutoffValue(TestResult tr){
		return tr.getValue().charAt(0) != '=';
	}
	
	public CutOffSeries getCutOffSeries(){
		return cutOffSeries;
	}
}
