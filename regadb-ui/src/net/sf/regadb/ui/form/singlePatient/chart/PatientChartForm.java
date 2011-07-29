package net.sf.regadb.ui.form.singlePatient.chart;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.tools.MutationHelper;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDate;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPointF;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.chart.Axis;

public class PatientChartForm extends WGroupBox implements IForm 
{
	private Chart chart = null;
	private TestResultsModel model = null;
	private WTable viTable = null;
	
	private DateField minDate;
	private DateField maxDate;
	private WPushButton show;
	
	public PatientChartForm(Patient p)
	{
		super(tr("form.patient.chart"));

		Transaction t = RegaDBMain.getApp().createTransaction();
		t.attach(p);

		WTable table = new WTable(this);
		table.getElementAt(0, 0).addWidget(new WLabel(tr("form.patient.chart.minDate")));
		minDate = new DateField(InteractionState.Editing, this);
		table.getElementAt(0, 1).addWidget(minDate);
		table.getElementAt(0, 2).addWidget(new WLabel(tr("form.patient.chart.maxDate")));
		maxDate = new DateField(InteractionState.Editing, this);
		table.getElementAt(0, 3).addWidget(maxDate);
		show = new WPushButton(tr("form.patient.chart.show"));
		table.getElementAt(0, 4).addWidget(show);
		table.setStyleClass("chart-date-limit");
		
		Signal.Listener showAction = new Signal.Listener() {
			
			@Override
			public void trigger() {
				if(minDate.getDate() == null
						|| maxDate.getDate() == null
						|| !maxDate.getDate().after(minDate.getDate()))
					showChart(RegaDBMain.getApp().getSelectedPatient(), null, null);
				else
					showChart(RegaDBMain.getApp().getSelectedPatient(),
							minDate.getDate(),
							maxDate.getDate());
			}
		};
		
		show.clicked().addListener(this, showAction);
		minDate.enterPressed().addListener(this, showAction);
		maxDate.enterPressed().addListener(this, showAction);
		
		showChart(p, null,null);
		
		t.commit();
	}
	
	protected void showChart(Patient p, Date min, Date max){
		
		if(chart != null){
			chart.remove();
			viTable.remove();
		}
		
		chart = new Chart(this, min, max);
		
		model = new TestResultsModel();
		
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

		chart.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
            public void trigger(WMouseEvent a) {
            	chartClicked(a);
            }
		});
		
		chart.loadTherapies(p);
		chart.loadViralIsolates(p);
		
		int chartHeight = 500;
		int chartWidth = 700;
		int chartPaddingLeft = 40;
		int chartPaddingRight = 200;
		int chartPaddingBottom = chart.calculateAddedHeight();

		
		chart.resize(chartWidth + chartPaddingLeft + chartPaddingRight,
				chartHeight + chartPaddingBottom);
		chart.setPlotAreaPadding(chartPaddingLeft,Side.Left);
		chart.setPlotAreaPadding(chartPaddingRight,Side.Right);
		chart.setPlotAreaPadding(chartPaddingBottom,Side.Bottom);
		
		viTable = new WTable(this);
	}
	
	private void chartClicked(WMouseEvent a){
		WPointF c = new WPointF(a.getWidget());
		Date d = WDate.fromJulianDay((int)chart.mapFromDevice(c,Axis.XAxis).getX()).getDate();
		
		showClosestViralIsolate(d);
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
	
	private int prevViralIsolateIi = -1;
	public void showClosestViralIsolate(Date date){
		Transaction t = RegaDBMain.getApp().createTransaction();
		Patient p = RegaDBMain.getApp().getSelectedPatient();
		
		List<ViralIsolate> vis = t.getViralIsolatesSortedOnDate(p);
		long diff = date.getTime();
		ViralIsolate v = null;
		for(ViralIsolate vi : vis){
			if(Math.abs(date.getTime() - vi.getSampleDate().getTime()) < diff){
				diff = Math.abs(date.getTime() - vi.getSampleDate().getTime());
				v = vi;
			}
			else
				break;
		}
		
		if(v == null || v.getViralIsolateIi() == prevViralIsolateIi)
			return;
		prevViralIsolateIi = v.getViralIsolateIi();
		
		viTable.clear();
		viTable.getElementAt(0, 0).addWidget(new WText(v.getSampleId(), TextFormat.PlainText));

		int i = 1;
		for(NtSequence nt : v.getNtSequences()){
			for(AaSequence aaseq : nt.getAaSequences()){
				viTable.getElementAt(i, 0).addWidget(new WText(aaseq.getProtein().getAbbreviation(),TextFormat.PlainText));
				viTable.getElementAt(i, 1).addWidget(new WText(MutationHelper.getNonSynonymousMutations(aaseq), TextFormat.PlainText));
				++i;
			}
		}
		
		t.commit();
	}
}
