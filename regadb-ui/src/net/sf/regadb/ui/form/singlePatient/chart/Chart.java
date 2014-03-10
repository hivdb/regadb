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
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.AlignmentFlag;
import eu.webtoolkit.jwt.PenStyle;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.WBrush;
import eu.webtoolkit.jwt.WColor;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDate;
import eu.webtoolkit.jwt.WFont;
import eu.webtoolkit.jwt.WFont.GenericFamily;
import eu.webtoolkit.jwt.WFont.Size;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WLength.Unit;
import eu.webtoolkit.jwt.WPaintDevice;
import eu.webtoolkit.jwt.WPainter;
import eu.webtoolkit.jwt.WPainter.RenderHint;
import eu.webtoolkit.jwt.WPen;
import eu.webtoolkit.jwt.WPointF;
import eu.webtoolkit.jwt.WRectF;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.chart.Axis;
import eu.webtoolkit.jwt.chart.AxisScale;
import eu.webtoolkit.jwt.chart.AxisValue;
import eu.webtoolkit.jwt.chart.ChartType;
import eu.webtoolkit.jwt.chart.WAxis;
import eu.webtoolkit.jwt.chart.WCartesianChart;
import eu.webtoolkit.jwt.chart.WDataSeries;

public class Chart extends WCartesianChart{
	private Date cutoffDate;
	private Date minDate = null;
	private Date maxDate = null;
	private boolean fixedInterval = false;
	
	private Date deathDate = null;
	private Date ltfuDate = null;
	
	public Chart(WContainerWidget widget, Date min, Date max) {
		super(ChartType.ScatterPlot, widget);
		setPreferredMethod(Method.InlineSvgVml);
		
		WAxis axis = getAxis(Axis.XAxis);
		axis.setScale(AxisScale.DateScale);
		axis.setGridLinesEnabled(true);
		axis.setLabelAngle(-30);
		
		axis = getAxis(Axis.Y2Axis);
		axis.setScale(AxisScale.LinearScale);
		axis.setLabelFormat("%.2f");
		axis.setVisible(true);
		axis.setTitle(WString.tr("form.patient.chart.Y2Axis"));
		axis.setMinimum(0);
		axis.setAutoLimits(AxisValue.MaximumValue);
		
		axis = getAxis(Axis.YAxis);
		axis.setScale(AxisScale.LinearScale);
		axis.setLabelFormat("%.0f");
		axis.setGridLinesEnabled(true);
		axis.setVisible(true);
		axis.setTitle(WString.tr("form.patient.chart.YAxis"));
		axis.setMinimum(0);
		axis.setAutoLimits(AxisValue.MaximumValue);

		setLegendEnabled(true);
		
		//don't show values before a certain date, most likely wrong/dummy value and mess up x-axis
		Calendar cal = Calendar.getInstance();
		cal.set(1900, 1, 1);
		cutoffDate = cal.getTime();
		
		if(min == null || max == null){
			//prevent crash when no dates are set
			WDate now = new WDate(new Date());
			getAxis(Axis.XAxis).setRange(
					now.addMonths(-1).toJulianDay(),
					now.toJulianDay());
		}else{
			fixedInterval = true;
			minDate = min;
			maxDate = max;
			setDateRange(min, max);
		}
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
		if(!fixedInterval){
			if(series.getMinDate() != null){
				if(cutoffDate.before(series.getMinDate()) && (minDate == null || series.getMinDate().before(minDate)))
					minDate = series.getMinDate();
				if(cutoffDate.before(series.getMaxDate()) && (maxDate == null || series.getMaxDate().after(maxDate)))
					maxDate = series.getMaxDate();
			}
			setDateRange(minDate, maxDate);
		}
		
		super.addSeries(series);
		
		if (series instanceof LimitedValueSeries) {
			super.addSeries(((LimitedValueSeries)series).getCutOffSeries());
			((LimitedValueSeries)series).getCutOffSeries().setPen(series.getPen());
		}
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
		WPen pen;
		WFont font;
		double sy, y;
		double width;
		
		WPainter painter = new WPainter(paintDevice);
		painter.setRenderHint(RenderHint.Antialiasing,true);
		this.paint(painter);
		
		font = new WFont();
		font.setSize(Size.XXSmall);
		font.setFamily(GenericFamily.SansSerif);
		painter.setFont(font);
		
		sy = getHeight().getValue() - getPlotAreaPadding(Side.Bottom) + therapyOffset;
		width = getWidth().getValue() - getPlotAreaPadding(Side.Right) + 60;
		
		y = sy;
		
		if(drugsUsed.size() > 0){
			WBrush closedTherapyBrush = new WBrush(new WColor(0,200,50));
			WBrush openTherapyBrush = new WBrush(new WColor(50, 255, 50));

			//therapy legend
			double x = width + 10;
			y = sy;
			double w = 20;
			double h = therapyHeight;
			
			EnumSet<AlignmentFlag> flags = EnumSet.of(AlignmentFlag.AlignCenter,AlignmentFlag.AlignLeft);
			
			painter.setBrush(openTherapyBrush);
			painter.drawText(x, y, w, h, flags, WString.tr("form.patient.chart.legend.openTherapy"));
			y += h;
			drawDrugBlock(painter, x, y, w, h, false, false);
			y += h;
			
			painter.setBrush(closedTherapyBrush);
			painter.drawText(x, y, w, h, flags, WString.tr("form.patient.chart.legend.closedTherapy"));
			y += h;
			drawDrugBlock(painter, x, y, w, h, false, false);
			y += h;

			painter.drawText(x, y, w, h, flags, WString.tr("form.patient.chart.legend.blind"));
			y += h;
			drawDrugBlock(painter, x, y, w, h, true, false);
			y += h;

			painter.drawText(x, y, w, h, flags, WString.tr("form.patient.chart.legend.placebo"));
			y += h;
			drawDrugBlock(painter, x, y, w, h, false, true);
			
			y = sy;
			painter.setRenderHint(RenderHint.Antialiasing,false);
			painter.drawLine(0, y, width, y);
			painter.setRenderHint(RenderHint.Antialiasing,true);
	
			for(String drug : drugsUsed.keySet()){
				drugsUsed.put(drug, y);
	
				painter.drawText(new WRectF(0, y+therapySpacing+therapyLineWidth-1, 100, therapyHeight),
						EnumSet.of(AlignmentFlag.AlignCenter,AlignmentFlag.AlignLeft), drug);
				painter.drawText(new WRectF(width-30, y+therapySpacing+therapyLineWidth-1, 100, therapyHeight),
						EnumSet.of(AlignmentFlag.AlignCenter,AlignmentFlag.AlignLeft), drug);
				
				y += therapyHeight+therapyLineWidth+(therapySpacing*2);
	
				painter.setRenderHint(RenderHint.Antialiasing,false);
				painter.drawLine(0, y, width, y);
				painter.setRenderHint(RenderHint.Antialiasing,true);
			}
	
			pen = new WPen(WColor.transparent);
			pen.setWidth(new WLength(therapyLineWidth,Unit.Pixel));
			painter.setPen(pen);
			
			double minX = this.mapToDevice(new WDate(minDate),0).getX();
			double maxX = this.mapToDevice(new WDate(maxDate),0).getX();
			
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
				
				for(TherapyCommercial tc : me.getKey().getTherapyCommercials()){
					for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()){
						drawDrug(painter, dg,
								Math.max(minX, x1),
								Math.min(maxX, x2),
								tc.isBlind(), tc.isPlacebo());
					}
				}
				
				for(TherapyGeneric tg : me.getKey().getTherapyGenerics()){
					drawDrug(painter, tg.getId().getDrugGeneric(),
							Math.max(minX, x1),
							Math.min(maxX, x2),
							tg.isBlind(), tg.isPlacebo());
				}
			}
		}
		
		//draw viral isolates
		pen = new WPen(WColor.black);
		pen.setStyle(PenStyle.DashLine);
		painter.setPen(pen);
		
		for(ViralIsolate vi : viralisolates){
			double x1 = this.mapToDevice(new WDate(vi.getSampleDate()), 0).getX();
			
			painter.drawLine(x1, 0, x1, y+therapyHeight);
			painter.drawText(x1-50, y+therapyHeight+therapySpacing, 100, therapyHeight,
					EnumSet.of(AlignmentFlag.AlignCenter,AlignmentFlag.AlignCenter), vi.getSampleId());
		}

		
		if(getDeathDate() != null){
			pen = new WPen(WColor.red);
			pen.setStyle(PenStyle.DashLine);
			painter.setPen(pen);
			
			double x1 = this.mapToDevice(new WDate(getDeathDate()), 0).getX();
			
			painter.drawLine(x1, 0, x1, y+therapyHeight);
			painter.drawText(x1-50, y+therapyHeight+therapySpacing, 100, therapyHeight,
					EnumSet.of(AlignmentFlag.AlignCenter,AlignmentFlag.AlignCenter),
						"ꝉ "+ DateUtils.format(getDeathDate()));
		}
		
		painter.setRenderHint(RenderHint.Antialiasing,true);
	}
	
	private void drawDrug(WPainter painter, DrugGeneric drug, double x1, double x2, boolean blind, boolean placebo){
		double i = drugsUsed.get(drug.getGenericId());

		double x = x1 + therapyLineWidth;
		double y = i + therapyLineWidth;
		double w = x2 - x1 - therapyLineWidth;
		double h = therapyHeight;
		
		drawDrugBlock(painter, x, y, w, h, blind, placebo);
	}
	
	private void drawDrugBlock(WPainter painter, double x, double y, double w, double h, boolean blind, boolean placebo){
		painter.drawRect(x, y, w, h);
		
		if(blind || placebo){
			WPen oldPen = painter.getPen();
			
			WPen pen = new WPen(WColor.white);
			pen.setWidth(new WLength(therapyLineWidth * 2, Unit.Pixel));
			painter.setPen(pen);
			
			if(blind)
				painter.drawLine(x, y+(h/4), x+w, y+(h/4));
			if(placebo)
				painter.drawLine(x, y+(3*h/4), x+w, y+(3*h/4));
			
			painter.setPen(oldPen);
		}
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
			
			if(!fixedInterval ||
					(!t.getStartDate().after(maxDate)
							&& (t.getStopDate() == null || !t.getStopDate().before(minDate)))){
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
		}
		
		if(!fixedInterval && drugsMap.size() > 0){
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
			if(!fixedInterval){
				if(cutoffDate.before(vi.getSampleDate()) && (minDate == null || vi.getSampleDate().before(minDate)))
					minDate = vi.getSampleDate();
				if(cutoffDate.before(vi.getSampleDate()) && (maxDate == null || vi.getSampleDate().after(maxDate)))
					maxDate = vi.getSampleDate();
			}
			
			if(!fixedInterval || 
					(!vi.getSampleDate().before(minDate) && !vi.getSampleDate().after(maxDate)))
				viralisolates.add(vi);
		}
		
		if(!fixedInterval)
			setDateRange(minDate, maxDate);
	}
	
	public int calculateAddedHeight(){
		return therapyOffset + ((drugsUsed.size()+2) * (therapySpacing*2 + therapyHeight + therapyLineWidth));
	}
	
	public void setDeathDate(Date deathDate){
		this.deathDate = deathDate;
	}
	
	public Date getDeathDate(){
		return deathDate;
	}
	
	public void setLTFUDate(Date ltfuDate){
		this.ltfuDate = ltfuDate;
	}
	
	public Date getLTFUDate(){
		return ltfuDate;
	}
}
