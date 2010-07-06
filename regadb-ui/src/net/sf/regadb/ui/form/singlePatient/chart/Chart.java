package net.sf.regadb.ui.form.singlePatient.chart;

import java.awt.Color;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;
import eu.webtoolkit.jwt.AlignmentFlag;
import eu.webtoolkit.jwt.PenStyle;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.WBrush;
import eu.webtoolkit.jwt.WBrushStyle;
import eu.webtoolkit.jwt.WColor;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDate;
import eu.webtoolkit.jwt.WFont;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WPaintDevice;
import eu.webtoolkit.jwt.WPainter;
import eu.webtoolkit.jwt.WPen;
import eu.webtoolkit.jwt.WPointF;
import eu.webtoolkit.jwt.WRectF;
import eu.webtoolkit.jwt.WLength.Unit;
import eu.webtoolkit.jwt.WPainter.RenderHint;
import eu.webtoolkit.jwt.chart.Axis;
import eu.webtoolkit.jwt.chart.AxisScale;
import eu.webtoolkit.jwt.chart.ChartType;
import eu.webtoolkit.jwt.chart.WAxis;
import eu.webtoolkit.jwt.chart.WCartesianChart;
import eu.webtoolkit.jwt.chart.WDataSeries;

public class Chart extends WCartesianChart{
	private static WColor colors[] = null;
	
	private Date cutoffDate;
	private Date minDate = null;
	private Date maxDate = null;
	
	public Chart(WContainerWidget widget) {
		super(ChartType.ScatterPlot, widget);
		
		//TODO, just a tmp workaround
		setPreferredMethod(Method.InlineSvgVml);
		
		WAxis axis = getAxis(Axis.XAxis);
		axis.setScale(AxisScale.DateScale);
		axis.setGridLinesEnabled(true);
		axis.setLabelAngle(-30);
		
		axis = getAxis(Axis.Y2Axis);
		axis.setScale(AxisScale.LinearScale);
		axis.setLabelFormat("%.2f");
		axis.setVisible(true);
		axis.setTitle("log10");
		
		axis = getAxis(Axis.YAxis);
		axis.setScale(AxisScale.LinearScale);
		axis.setLabelFormat("%.0f");
		axis.setGridLinesEnabled(true);
		axis.setVisible(true);
		axis.setTitle("cells/ul");

		setLegendEnabled(true);
		
		colors = generateColors(11);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1900, 1, 1);
		cutoffDate = cal.getTime();
	}
	
	public static WColor[] generateColors(int n){
		WColor colors[] = new WColor[n];
		
		float j = 0f;
		for(int i=0; i<colors.length; ++i){
			Color c = Color.getHSBColor(j, 1f, 0.9f);
			j += 1f / (n+2);
			colors[i] = new WColor(c.getRed(),c.getGreen(),c.getBlue()); 
		}
		
		return colors;
	}
	
	public static WColor getColor(int i){
		if(colors == null)
			colors = generateColors(10);
		
		return colors[i % colors.length];
	}
	
	public void setDateRange(Date minDate, Date maxDate){
		if(minDate == null || maxDate == null)
			return;
		
		WDate wMinDate = new WDate(minDate);
		WDate wMaxDate = new WDate(maxDate);
		
		if (Math.abs(wMinDate.getDaysTo(wMaxDate)) < 31) {
			wMinDate = wMinDate.addMonths(-1);
			wMaxDate = wMaxDate.addMonths(1);
		}
		
		getAxis(Axis.XAxis).setRange(
				wMinDate.toJulianDay(),
				wMaxDate.toJulianDay());
	}
	
	public void addSeries(TestResultSeries series){
		if(series.getMinDate() != null){
			if(cutoffDate.before(series.getMinDate()) && (minDate == null || series.getMinDate().before(minDate)))
				minDate = series.getMinDate();
			if(cutoffDate.before(series.getMaxDate()) && (maxDate == null || series.getMaxDate().after(maxDate)))
				maxDate = series.getMaxDate();
		}
		setDateRange(minDate, maxDate);
		
		WColor c = getColor(getSeries().size());
		WPen p = new WPen(c);
		p.setWidth(new WLength(2));
		series.setPen(p);
		series.setBrush(new WBrush(c));
		super.addSeries(series);
	}
	public void addSeries(LimitedValueSeries series){
		addSeries((TestResultSeries)series);
		super.addSeries(series.getCutOffSeries());
		series.getCutOffSeries().setPen(series.getPen());
	}
	
	@Override
	public void renderLegendItem(WPainter painter, WPointF pos,
			WDataSeries series) {
		painter.save();
		
		WFont font = painter.getFont();
		font.setSize(WFont.Size.XXSmall);
		painter.setFont(font);
		
		super.renderLegendItem(painter, pos, series);
		
		painter.restore();
	}

	private int therapyOffset = 50;
	private int therapySpacing = 1;
	private int therapyHeight = 15;
	private int therapyLineWidth = 1;
	
	@Override
	protected void paintEvent(WPaintDevice paintDevice) {
		WPainter painter = new WPainter(paintDevice);
		painter.setRenderHint(RenderHint.Antialiasing,true);
		this.paint(painter);
		
		double sy = getHeight().getValue() - getPlotAreaPadding(Side.Bottom) + therapyOffset;
		
		painter.setRenderHint(RenderHint.Antialiasing,false);
		painter.drawLine(0, sy, getWidth().getValue(), sy);
		painter.setRenderHint(RenderHint.Antialiasing,true);

		for(String drug : drugsUsed.keySet()){
			drugsUsed.put(drug, sy);

			painter.drawText(new WRectF(0, sy+therapySpacing+therapyLineWidth-1, 100, therapyHeight),
					EnumSet.of(AlignmentFlag.AlignCenter,AlignmentFlag.AlignLeft), drug);
			painter.drawText(new WRectF(getWidth().getValue() - 100, sy+therapySpacing+therapyLineWidth-1, 100, therapyHeight),
					EnumSet.of(AlignmentFlag.AlignCenter,AlignmentFlag.AlignRight), drug);
			
			sy += therapyHeight+therapyLineWidth+(therapySpacing*2);

			painter.setRenderHint(RenderHint.Antialiasing,false);
			painter.drawLine(0, sy, getWidth().getValue(), sy);
			painter.setRenderHint(RenderHint.Antialiasing,true);
		}

		WPen pen = new WPen(WColor.transparent);
		pen.setWidth(new WLength(therapyLineWidth,Unit.Pixel));
		painter.setPen(pen);
		
		WBrush closedTherapyBrush = new WBrush(new WColor(0,200,50));
		WBrush openTherapyBrush = new WBrush(new WColor(50, 255, 50));
		
		painter.setRenderHint(RenderHint.Antialiasing,false);
		for(Map.Entry<Therapy, TreeSet<String>> me : drugsMap.entrySet()){
			double x1 = this.mapToDevice(new WDate(me.getKey().getStartDate()), 0).getX();
			WDate stopDate;
			if(me.getKey().getStopDate() == null){
				stopDate = new WDate(maxDate);
				painter.setBrush(openTherapyBrush);
			} else {
				stopDate = new WDate(me.getKey().getStopDate());
				painter.setBrush(closedTherapyBrush);
			}
		
			double x2 = this.mapToDevice(stopDate,0).getX();
			
			for(String drug : me.getValue()){
				double i = drugsUsed.get(drug);

				painter.drawRect(x1 + therapyLineWidth, i + therapyLineWidth + therapySpacing, 
						x2-x1 - therapyLineWidth, therapyHeight);
			}
		}
		
		//draw viral isolates
		pen = new WPen(WColor.black);
		pen.setStyle(PenStyle.DashLine);
		paintDevice.getPainter().setPen(pen);
		
		for(ViralIsolate vi : viralisolates){
			double x1 = this.mapToDevice(new WDate(vi.getSampleDate()), 0).getX();
			
			painter.drawLine(x1, 0, x1, sy+therapyHeight);
			painter.drawText(x1-50, sy+therapyHeight+therapySpacing, 100, therapyHeight,
					EnumSet.of(AlignmentFlag.AlignCenter,AlignmentFlag.AlignCenter), vi.getSampleId());
		}

		painter.setRenderHint(RenderHint.Antialiasing,true);
	}
	
	private TreeMap<Therapy,TreeSet<String>> drugsMap = new TreeMap<Therapy,TreeSet<String>>(
			new Comparator<Therapy>() {
				public int compare(Therapy o1, Therapy o2) {
					return o1.getStartDate().compareTo(o2.getStartDate());
				}
			});
	private TreeMap<String,Double> drugsUsed = new TreeMap<String,Double>();
	
	public void loadTherapies(Patient p){
		Map<String,List<String>> commercialGeneric = new TreeMap<String,List<String>>();
		drugsMap.clear();
		drugsUsed.clear();
		
		for(Therapy t : p.getTherapies()){
			TreeSet<String> drugs = new TreeSet<String>();
			
			for(TherapyCommercial tc : t.getTherapyCommercials()){
				String name = tc.getId().getDrugCommercial().getName();
				List<String> ids = commercialGeneric.get(name);
				if(ids == null){
					ids = new LinkedList<String>();
					
					for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()){
						ids.add(dg.getGenericId());
						drugsUsed.put(dg.getGenericId(), 0d);
					}
					
					commercialGeneric.put(name, ids);
				}
				
				drugs.addAll(ids);
			}
			for(TherapyGeneric tg : t.getTherapyGenerics()){
				drugs.add(tg.getId().getDrugGeneric().getGenericId());
				drugsUsed.put(tg.getId().getDrugGeneric().getGenericId(), 0d);
			}
			
			drugsMap.put(t, drugs);
		}
		
		if(drugsMap.size() > 0){
			Date d = drugsMap.firstKey().getStartDate();
			if(cutoffDate.before(d) && (minDate == null || d.before(minDate)))
				minDate = d;
			
			d = drugsMap.lastKey().getStopDate() == null ? new Date() : drugsMap.lastKey().getStopDate();
			if(cutoffDate.before(d) && (maxDate == null || d.after(maxDate)))
				maxDate = d;
			
			setDateRange(minDate, maxDate);
		}
	}
	
	
	private TreeSet<ViralIsolate> viralisolates = new TreeSet<ViralIsolate>(
			new Comparator<ViralIsolate>() {
				public int compare(ViralIsolate o1, ViralIsolate o2) {
					return o1.getSampleDate().compareTo(o2.getSampleDate());
				}
			});
	
	public void loadViralIsolates(Patient p){
		if(p.getViralIsolates().size() == 0)
			return;
		
		for(ViralIsolate vi : p.getViralIsolates()){
			if(cutoffDate.before(vi.getSampleDate()) && (minDate == null || vi.getSampleDate().before(minDate)))
				minDate = vi.getSampleDate();
			if(cutoffDate.before(vi.getSampleDate()) && (maxDate == null || vi.getSampleDate().after(maxDate)))
				maxDate = vi.getSampleDate();
			
			viralisolates.add(vi);
		}
		
		setDateRange(minDate, maxDate);
	}
	
	public int calculateAddedHeight(){
		return therapyOffset + ((drugsUsed.size()+2) * (therapySpacing*2 + therapyHeight + therapyLineWidth));
	}
}
