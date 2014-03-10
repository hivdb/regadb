package net.sf.regadb.ui.form.singlePatient.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.util.settings.PatientChartConfig;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.WBrush;
import eu.webtoolkit.jwt.WColor;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WPen;
import eu.webtoolkit.jwt.chart.Axis;

public class DefaultChart {
	public static Chart createDefaultChart(WContainerWidget parent, Patient p, Date min, Date max, int width, int height) {
		PatientChartConfig pcc = RegaDBSettings.getInstance().getInstituteConfig().getPatientChartConfig();
		
		Chart chart = new Chart(parent, min, max);
		
		chart.setDeathDate(p.getDeathDate());
		
		TestResultsModel model = new TestResultsModel();
		
		List<TestResultSeries> cellCountSeries = new LinkedList<TestResultSeries>();
		List<TestResultSeries> viralLoadSeries = new LinkedList<TestResultSeries>();
		
		for (PatientChartConfig.Item i : pcc.getY1().items) {
			if (i.kind == PatientChartConfig.Item.Kind.CellCount) {
				createCCSeries(i, Axis.YAxis, p, cellCountSeries);
			} else {
				createVLSeries(i, Axis.YAxis, p, viralLoadSeries);
			}
		}
		
		for (PatientChartConfig.Item i : pcc.getY2().items) {
			if (i.kind == PatientChartConfig.Item.Kind.CellCount) {
				createCCSeries(i, Axis.Y2Axis, p, cellCountSeries);
			} else {
				createVLSeries(i, Axis.Y2Axis, p, viralLoadSeries);
			}
		}
		
		for(TestResultSeries s : cellCountSeries)
			model.getSeries().add(s);
		
		for(TestResultSeries s : viralLoadSeries)
			model.getSeries().add(s);
		
		model.loadResults(p, min, max);

		chart.setModel(model);
		for(TestResultSeries s : cellCountSeries)
			chart.addSeries(s);
		for(TestResultSeries s : viralLoadSeries)
			chart.addSeries(s);
		chart.setXSeriesColumn(model.getXSeriesColumn());
		
		chart.loadTherapies(p);
		chart.loadViralIsolates(p);

		int chartPaddingLeft = 40;
		int chartPaddingRight = 200;
		int chartPaddingBottom = chart.calculateAddedHeight();

		chart.resize(width + chartPaddingLeft + chartPaddingRight,
				height + chartPaddingBottom);
		chart.setPlotAreaPadding(chartPaddingLeft,Side.Left);
		chart.setPlotAreaPadding(chartPaddingRight,Side.Right);
		chart.setPlotAreaPadding(chartPaddingBottom,Side.Bottom);
		
		return chart;
	}
	
	private static Set<Genome> genomes(Patient p, List<String> testTypes) {
		Set<Genome> genomes = new HashSet<Genome>();
		for (TestResult tr : p.getTestResults()) {
			TestType tt = tr.getTest().getTestType();
			if (testTypes.contains(tt.getDescription()))
				genomes.add(tt.getGenome());
		}
		return genomes;
	}
	
	private static WPen createPen(WColor color) {
		WPen p = new WPen(color);
		p.setWidth(new WLength(2));
		return p;
	}
	
	private static WBrush createBrush(WColor color) {
		return new WBrush(color);
	}
	
	private static List<String> testTypeDescriptions(PatientChartConfig.Item i) {
		List<String> descriptions = new ArrayList<String>();
		for (PatientChartConfig.TestType tt : i.testTypes)
			descriptions.add(tt.type);
		return descriptions;
	}
	
	private static void createVLSeries(PatientChartConfig.Item i, Axis axis, Patient p, List<TestResultSeries> series) {
		for(Genome genome : genomes(p, testTypeDescriptions(i))) {
			ViralLoadSeries vls = new ViralLoadSeries(genome, i, axis);
			WColor c = getViralLoadColor(series.size());
			vls.setPen(createPen(c));
			vls.setBrush(createBrush(c));
			series.add(vls);
		}
	}
	
	private static void createCCSeries(PatientChartConfig.Item i, Axis axis, Patient p, List<TestResultSeries> series) {
		CellCountSeries ccs = new CellCountSeries(i, axis);
		WColor c = getCellCountColor(series.size());
		ccs.setPen(createPen(c));
		ccs.setBrush(createBrush(c));
		series.add(ccs);
	}
	
	private static WColor getCellCountColor(int i){
		if (i == 0) {
			return new WColor(255, 0, 0);
		} else if(i == 1) {
			return new WColor(0, 0, 255);
		} else if(i == 2) {
			return new WColor(0, 255, 255);
		} else {
			throw new RuntimeException("Out of cell count colors");
		}
	}
	
	private static WColor getViralLoadColor(int i){
		if (i == 0) {
			return new WColor(0, 255, 0);
		} else if(i == 1) {
			return new WColor(255, 255, 0);
		} else if(i == 2) {
			return new WColor(255, 0, 255);
		} else {
			throw new RuntimeException("Out of viral load colors");
		}
	}
}
