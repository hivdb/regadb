package net.sf.regadb.ui.form.singlePatient.chart;

import java.util.LinkedList;
import java.util.List;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.PositionScheme;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDate;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPointF;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WMouseEvent.Coordinates;
import eu.webtoolkit.jwt.chart.Axis;

public class PatientChartForm extends WGroupBox implements IForm 
{
	private Chart chart;
	private TestResultsModel model;
	private WText label;
	
	public PatientChartForm(Patient p)
	{
		super(tr("form.singlePatient.viewChart"));
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		t.attach(p);
		
		chart = new Chart(this);
		
		model = new TestResultsModel();
		
		List<ViralLoadSeries> vlSeries = new LinkedList<ViralLoadSeries>();
		for(Genome genome : StandardObjects.getGenomes())
			vlSeries.add(new ViralLoadSeries(genome, Axis.YAxis));
		TestResultSeries cd4Series = new TestResultSeries(StandardObjects.getCd4TestType(), Axis.Y2Axis);
		
		for(ViralLoadSeries vl : vlSeries)
			model.getSeries().add(vl);
		model.getSeries().add(cd4Series);
		model.loadResults(p);

		chart.setModel(model);
		for(ViralLoadSeries vl : vlSeries)
			chart.addSeries(vl);
		chart.addSeries(cd4Series);
		chart.setXSeriesColumn(model.getXSeriesColumn());

		chart.resize(1000, 1000);
		chart.setPlotAreaPadding(80,Side.Left);
		chart.setPlotAreaPadding(200,Side.Right);
		chart.setPlotAreaPadding(500,Side.Bottom);
		
		chart.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
            public void trigger(WMouseEvent a) {
            	chartClicked(a);
            }
		});
		
		chart.loadTherapies(p);
		
		t.commit();
		
		label = new WText("n/a");
		label.setStyleClass("chart-popup");
		label.setPopup(true);
		label.hide();
		label.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
            public void trigger(WMouseEvent a) {
            	label.hide();
            }
		});
		addWidget(label);
	}
	
	private void chartClicked(WMouseEvent a){
		WPointF c = new WPointF(a.getWidget());
		String x = DateUtils.format(WDate.fromJulianDay((int)chart.mapFromDevice(c,Axis.XAxis).getX()).getDate());
		double y = Math.round(chart.mapFromDevice(c,Axis.YAxis).getY()*100d)/100d;
		double y2 = Math.round(chart.mapFromDevice(c,Axis.Y2Axis).getY()*100d)/100d;
		label.setText("("+ x +", "+ y +", "+ y2 +")");
		label.setPositionScheme(PositionScheme.Absolute);
		label.setOffsets(a.getDocument().x,Side.Left);
		label.setOffsets(a.getDocument().y,Side.Top);
		label.show();
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
