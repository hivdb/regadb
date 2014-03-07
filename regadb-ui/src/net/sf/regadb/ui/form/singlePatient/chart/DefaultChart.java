package net.sf.regadb.ui.form.singlePatient.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.settings.PatientChartConfig;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.chart.Axis;

public class DefaultChart {
	public static Chart createDefaultChart(WContainerWidget parent, Patient p, Date min, Date max, int width, int height) {
		PatientChartConfig pcc = RegaDBSettings.getInstance().getInstituteConfig().getPatientChartConfig();
		
		Chart chart = new Chart(parent, min, max);
		
		chart.setDeathDate(p.getDeathDate());
		
		TestResultsModel model = new TestResultsModel();
		
		List<TestResultSeries> y1Series = new LinkedList<TestResultSeries>();
		for (PatientChartConfig.Item i : pcc.getY1().items) {
			y1Series.addAll(createTestResultSeries(i, Axis.YAxis));
		}
		
		List<TestResultSeries> y2Series = new LinkedList<TestResultSeries>();
		for (PatientChartConfig.Item i : pcc.getY2().items) {
			y2Series.addAll(createTestResultSeries(i, Axis.Y2Axis));
		}
		
		for(TestResultSeries s : y1Series)
			model.getSeries().add(s);
		
		for(TestResultSeries s : y2Series)
			model.getSeries().add(s);
		
		model.loadResults(p, min, max);

		chart.setModel(model);
		for(TestResultSeries s : y1Series)
			chart.addSeries(s);
		for(TestResultSeries s : y2Series)
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
	
	private static List<TestResultSeries> createTestResultSeries(PatientChartConfig.Item i, Axis axis) {
		List<TestResultSeries> series = new ArrayList<TestResultSeries>();
		if (i.kind == PatientChartConfig.Item.Kind.CellCount) {
			  series.add(new CellCountSeries(i, axis));
		} else {
			for(Genome genome : StandardObjects.getGenomes())
				series.add(new ViralLoadSeries(genome, i, axis));
		}
		return series;
	}
}
