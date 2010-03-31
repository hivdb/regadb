package net.sf.regadb.ui.form.singlePatient.chart;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.chart.Axis;
import eu.webtoolkit.jwt.chart.AxisScale;
import eu.webtoolkit.jwt.chart.ChartType;
import eu.webtoolkit.jwt.chart.WCartesianChart;

public class PatientChartForm extends WGroupBox implements IForm 
{
	private WCartesianChart chart;
	
	public PatientChartForm(Patient p)
	{
		super(tr("form.singlePatient.viewChart"));
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		t.attach(p);
		
		chart = new WCartesianChart(ChartType.ScatterPlot, this);
		chart.getAxis(Axis.XAxis).setScale(AxisScale.DateScale);
		chart.getAxis(Axis.YAxis).setScale(AxisScale.LogScale);
		chart.getAxis(Axis.Y2Axis).setScale(AxisScale.LinearScale);
		chart.getAxis(Axis.XAxis).setGridLinesEnabled(true);
		chart.getAxis(Axis.XAxis).setLabelAngle(-30);
		chart.getAxis(Axis.Y2Axis).setGridLinesEnabled(true);
		chart.getAxis(Axis.Y2Axis).setVisible(true);
		chart.setLegendEnabled(true);
		
		TestResultsModel model = new TestResultsModel();
		TestResultSeries vlSeries = new ViralLoadSeries(StandardObjects.getHiv1Genome(), Axis.YAxis);
		TestResultSeries vl2Series = new ViralLoadSeries(StandardObjects.getHiv2AGenome(), Axis.YAxis);
		TestResultSeries cd4Series = new TestResultSeries(StandardObjects.getCd4TestType(), Axis.Y2Axis);
		
		model.getSeries().add(vlSeries);
		model.getSeries().add(vl2Series);
		model.getSeries().add(cd4Series);
		model.loadResults(p);

		chart.setModel(model);
		chart.addSeries(vlSeries);
		chart.addSeries(vl2Series);
		chart.addSeries(cd4Series);
		chart.setXSeriesColumn(model.getXSeriesColumn());

		chart.resize(1000, 600);
		chart.setPlotAreaPadding(80,Side.Left);
		chart.setPlotAreaPadding(200,Side.Right);
		chart.setPlotAreaPadding(50,Side.Bottom);
		
//		chart.clicked().addListener(slot);
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
}
