package net.sf.regadb.ui.form.singlePatient.chart;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.io.util.StandardObjects;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.chart.Axis;

public class DefaultChart {
	public static Chart createDefaultChart(WContainerWidget parent, Patient p, Date min, Date max, int width, int height) {
		Chart chart = new Chart(parent, min, max);
		
		chart.setDeathDate(p.getDeathDate());
		
		TestResultsModel model = new TestResultsModel();
		
		List<ViralLoadSeries> vlSeries = new LinkedList<ViralLoadSeries>();
		for(Genome genome : StandardObjects.getGenomes())
			vlSeries.add(new ViralLoadSeries(genome, Axis.Y2Axis));
		TestResultSeries cd4Series = new TestResultSeries(StandardObjects.getCd4TestType(), Axis.YAxis){
			public String getName(){
				return "CD4";
			}
		};
		
		for(ViralLoadSeries vl : vlSeries)
			model.getSeries().add(vl);
		model.getSeries().add(cd4Series);
		model.loadResults(p, min, max);

		chart.setModel(model);
		for(ViralLoadSeries vl : vlSeries)
			chart.addSeries(vl);
		chart.addSeries(cd4Series);
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
}
