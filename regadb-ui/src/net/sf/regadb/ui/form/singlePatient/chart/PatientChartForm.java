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
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDate;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WStandardItemModel;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.chart.Axis;
import eu.webtoolkit.jwt.chart.AxisScale;
import eu.webtoolkit.jwt.chart.ChartType;
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
//		final PatientChart chartDrawer = new PatientChart(p, t.getSettingsUser());
//
//		//TODO 
//		//how to set mimetype????
//        WImage chartImage = new WImage(new WResource() {
//        	protected void handleRequest(WebRequest request, WebResponse response) {
//        		try {
//        			chartDrawer.writePngChart(800, response.getOutputStream());
//        		} catch (IOException e) {
//        			e.printStackTrace();
//        		}
//        	}
//            
//        }, "Patient Chart", this);
		
		WCartesianChart chart = new WCartesianChart(ChartType.ScatterPlot, this);
		chart.getAxis(Axis.XAxis).setScale(AxisScale.DateScale);
		chart.getAxis(Axis.YAxis).setScale(AxisScale.LogScale);
		
		PatientMeasurementsModel model = new PatientMeasurementsModel(p);
		chart.setModel(model);
		chart.setXSeriesColumn(0);
		chart.addSeries(new WDataSeries(1));
//		chart.getSeries(1).setLabelsEnabled(Axis.XAxis, true);
//		chart.getSeries(1).setLabelsEnabled(Axis.YAxis, true);
		chart.getSeries(1).setType(SeriesType.LineSeries);
		

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
		private interface TestFilter{
			public boolean isOk(TestResult r);
			public Object getValue(TestResult r);
		}
		
		private static class ViralLoadTestFilter implements TestFilter{
			private String genome;
			
			private enum ViralLoadType{LOG,COPIES,NONE};
			
			public ViralLoadTestFilter(String genome){
				this.genome = genome;
			}

			public Object getValue(TestResult r) {
				ViralLoadType type = getType(r);
				if(type == ViralLoadType.COPIES){
					double d = Double.parseDouble(r.getValue().substring(1));
					return Math.pow(10, d);
				}
				if(type == ViralLoadType.LOG){
					double d = Double.parseDouble(r.getValue().substring(1));
					return d == 0 ? 1 : d;
				}
				return null;
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
		}
		
		private static class SimpleTestFilter implements TestFilter{
			private String testType;
			
			public SimpleTestFilter(String testType){
				this.testType = testType;
			}
			
			public Object getValue(TestResult r) {
				return Double.parseDouble(r.getValue());
			}

			public boolean isOk(TestResult r) {
				return r.getTest().getTestType().getDescription().equals(testType);
			}
		}
		
		public PatientMeasurementsModel(Patient p){
			super();
			init(p);
		}
		
		private void init(Patient p){
			TreeMap<Date,Object> vlMap = getMap(p, new ViralLoadTestFilter(StandardObjects.getHiv1Genome().getOrganismName()));
			TreeMap<Date,Object> cdMap = getMap(p, new SimpleTestFilter(StandardObjects.getCd4TestType().getDescription()));
			
			Iterator<Entry<Date, Object>> vli = vlMap.entrySet().iterator();
			Iterator<Entry<Date, Object>> cdi = cdMap.entrySet().iterator();
			Entry<Date, Object> vl;
			Entry<Date, Object> cd;
			
			int row = 0;
			int dtCol = 0;
			int vlCol = 1;
			int cdCol = 2;
			insertColumns(0, 3);
			
			while(vli.hasNext()){
				vl = vli.next();
				cd = null;
				while(cdi.hasNext() && (cd = cdi.next()).getKey().before(vl.getKey())){
					insertRow(row);
					setData(row, dtCol, new WDate(cd.getKey()));
					setData(row, vlCol, null);
					setData(row, cdCol, cd.getValue());
					++row;
				}
				
				insertRow(row);
				setData(row, dtCol, new WDate(vl.getKey()));
				setData(row, vlCol, vl.getValue());
				setData(row, cdCol, cd == null ? null:cd.getValue());
				++row;
			}
			
			while(cdi.hasNext()){
				cd = cdi.next();
				insertRow(row);
				setData(row, dtCol, new WDate(cd.getKey()));
				setData(row, vlCol, null);
				setData(row, cdCol, cd.getValue());
				++row;
			}
		}
		
		private TreeMap<Date,Object> getMap(Patient p, TestFilter f){
			TreeMap<Date,Object> map = new TreeMap<Date,Object>();
			for(TestResult r : p.getTestResults()){
				if(f.isOk(r))
					map.put(r.getTestDate(), f.getValue(r));
			}
			return map;
		}
	}
}
