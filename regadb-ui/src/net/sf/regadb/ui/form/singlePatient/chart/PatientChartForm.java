package net.sf.regadb.ui.form.singlePatient.chart;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import eu.webtoolkit.jwt.Orientation;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDate;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WStandardItemModel;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.chart.Axis;
import eu.webtoolkit.jwt.chart.AxisScale;
import eu.webtoolkit.jwt.chart.ChartType;
import eu.webtoolkit.jwt.chart.MarkerType;
import eu.webtoolkit.jwt.chart.SeriesType;
import eu.webtoolkit.jwt.chart.WCartesianChart;
import eu.webtoolkit.jwt.chart.WDataSeries;

public class PatientChartForm extends WGroupBox implements IForm 
{
	
	public PatientChartForm(Patient p)
	{
		super(tr("form.singlePatient.viewChart"));
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		t.attach(p);
		
		WCartesianChart chart = new WCartesianChart(ChartType.ScatterPlot, this);
		chart.getAxis(Axis.XAxis).setScale(AxisScale.DateScale);
		chart.getAxis(Axis.YAxis).setScale(AxisScale.LogScale);
		chart.getAxis(Axis.Y2Axis).setScale(AxisScale.LinearScale);
		chart.getAxis(Axis.XAxis).setGridLinesEnabled(true);
		chart.getAxis(Axis.XAxis).setLabelAngle(-30);
		chart.getAxis(Axis.Y2Axis).setGridLinesEnabled(true);
		chart.getAxis(Axis.Y2Axis).setVisible(true);
		
		PatientMeasurementsModel model = new PatientMeasurementsModel(p);
		chart.setModel(model);
		chart.setXSeriesColumn(model.COLUMN_DATE);

		WDataSeries ds = new WDataSeries(model.COLUMN_VL, SeriesType.LineSeries, Axis.YAxis);
		ds.setMarker(MarkerType.XCrossMarker);
		ds.setLegendEnabled(true);
		chart.addSeries(ds);

		ds = new WDataSeries(model.COLUMN_LV, SeriesType.PointSeries, Axis.YAxis);
		ds.setMarker(MarkerType.CrossMarker);
		chart.addSeries(ds);

		ds = new WDataSeries(model.COLUMN_CD, SeriesType.LineSeries, Axis.Y2Axis);
		ds.setMarker(MarkerType.TriangleMarker);
		ds.setLegendEnabled(true);
		chart.addSeries(ds);

		chart.resize(1000, 600);
		chart.setPlotAreaPadding(80,Side.Left);
		chart.setPlotAreaPadding(200,Side.Right);
		chart.setPlotAreaPadding(50,Side.Bottom);

		t.commit();
	}
	
	public void addFormField(IFormField field)
	{

	}

	public WContainerWidget getWContainer()
	{
		return this;
	}

    public WString leaveForm() {
        return null;
    }

	public void removeFormField(IFormField field) {

	}
	
	private static class PatientMeasurementsModel extends WStandardItemModel{
		private int COLUMN_DATE = 0;
		private int COLUMN_VL = 1;
		private int COLUMN_LV = 2;
		private int COLUMN_CD = 3;
		
		private interface TestFilter<T>{
			public boolean isOk(TestResult r);
			public T getValue(TestResult r);
			public boolean overwrite(TestResult o, TestResult n);
		}
		
		private static class LimitedValue{
			public Double value;
			public boolean outOfRange;
		}
		
		private static class ViralLoadTestFilter implements TestFilter<LimitedValue>{
			private String genome;
			
			private enum ViralLoadType{LOG,COPIES,NONE};
			
			public ViralLoadTestFilter(String genome){
				this.genome = genome;
			}

			public LimitedValue getValue(TestResult r) {
				ViralLoadType type = getType(r);
				if(type == ViralLoadType.NONE)
					return null;
				
				double d = Double.parseDouble(r.getValue().substring(1));
				LimitedValue lv = new LimitedValue();
				lv.outOfRange = r.getValue().charAt(0) != '=';
				
				if(type == ViralLoadType.COPIES)
					lv.value = d == 0 ? 1 : d;
				else
					lv.value = Math.pow(10, d);
				return lv;
			}

			public boolean isOk(TestResult r) {
				return getType(r) != ViralLoadType.NONE;
			}
			
			private ViralLoadType getType(TestResult r){
				TestType tt = r.getTest().getTestType();
				if(tt.getGenome() == null || !tt.getGenome().getOrganismName().equals(genome))
					return ViralLoadType.NONE;
					
				if(tt.getDescription().equals(StandardObjects.getViralLoadDescription()))
					return ViralLoadType.COPIES;
				
				if(tt.getDescription().equals(StandardObjects.getViralLoadLog10Description()))
					return ViralLoadType.LOG;

				return ViralLoadType.NONE;
			}

			public boolean overwrite(TestResult o, TestResult n) {
				return getType(o) != ViralLoadType.COPIES;
			}
		}
		
		private static class SimpleTestFilter implements TestFilter<Double>{
			private String testType;
			
			public SimpleTestFilter(String testType){
				this.testType = testType;
			}
			
			public Double getValue(TestResult r) {
				return Double.parseDouble(r.getValue());
			}

			public boolean isOk(TestResult r) {
				return r.getTest().getTestType().getDescription().equals(testType);
			}

			public boolean overwrite(TestResult o, TestResult n) {
				return true;
			}
		}
		
		private ViralLoadTestFilter vlFilter = new ViralLoadTestFilter(StandardObjects.getHiv1Genome().getOrganismName());
		private SimpleTestFilter cdFilter = new SimpleTestFilter(StandardObjects.getCd4TestType().getDescription());
		
		public PatientMeasurementsModel(Patient p){
			super();
			init(p);
		}
		
		private void init(Patient p){
			TreeMap<Date,TestResult> vlMap = getMap(p, vlFilter);
			TreeMap<Date,TestResult> cdMap = getMap(p, cdFilter);
			
			Iterator<Entry<Date, TestResult>> vli = vlMap.entrySet().iterator();
			Iterator<Entry<Date, TestResult>> cdi = cdMap.entrySet().iterator();
			Entry<Date, TestResult> vl;
			Entry<Date, TestResult> cd;
			
			insertColumns(0, 4);
			System.out.println("date,vl,limited-vl,cd4");
			setHeaderData(COLUMN_DATE, Orientation.Horizontal, "Date");
			setHeaderData(COLUMN_VL, Orientation.Horizontal, "Viral Load");
			setHeaderData(COLUMN_LV, Orientation.Horizontal, "Limited Viral Load");
			setHeaderData(COLUMN_CD, Orientation.Horizontal, "CD4");
			
			while(vli.hasNext()){
				vl = vli.next();
				cd = null;
				while(cdi.hasNext() && (cd = cdi.next()).getKey().before(vl.getKey()))
					insertRow(null, cd.getValue());
				
				insertRow(vl.getValue(), cd == null ? null : cd.getValue());
			}
			
			while(cdi.hasNext()){
				cd = cdi.next();
				insertRow(null, cd.getValue());
			}
		}
		
		private void insertRow(TestResult vl, TestResult cd){
			int row = getRowCount();
			LimitedValue lv = vl == null ? null : vlFilter.getValue(vl);

			Date date = vl == null ? cd.getTestDate() : vl.getTestDate();
			Double cdv = cd == null ? null : cdFilter.getValue(cd);
			Double vlv = lv == null ? null : lv.value;
			Double lvv = lv == null || !lv.outOfRange ? null : lv.value;
			
			insertRow(row);
			setData(row, COLUMN_DATE, new WDate(date));
			setData(row, COLUMN_VL, vlv);
			setData(row, COLUMN_LV, lvv);
			setData(row, COLUMN_CD, cdv);
			
			System.out.println(date +","+ vlv +","+ lvv +","+ cdv);
		}
		
		@SuppressWarnings("unchecked")
		private TreeMap<Date,TestResult> getMap(Patient p, TestFilter f){
			TreeMap<Date,TestResult> map = new TreeMap<Date,TestResult>();
			for(TestResult r : p.getTestResults()){
				if(f.isOk(r)){
					TestResult o = map.get(r.getTestDate());
					if(o == null || f.overwrite(o, r))
						map.put(r.getTestDate(), r);
				}
			}
			return map;
		}
	}
}
