package net.sf.regadb.ui.form.singlePatient.chart;

import java.awt.Color;
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
import eu.webtoolkit.jwt.AlignmentFlag;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.WBrush;
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
	private WColor colors[];

	public Chart(WContainerWidget widget) {
		super(ChartType.ScatterPlot, widget);
		
		//TODO, just a tmp workaround
		setPreferredMethod(Method.InlineSvgVml);
		
		WAxis axis = getAxis(Axis.XAxis);
		axis.setScale(AxisScale.DateScale);
		axis.setGridLinesEnabled(true);
		axis.setLabelAngle(-30);
		
		axis = getAxis(Axis.YAxis);
		axis.setScale(AxisScale.LogScale);
		axis.setGridLinesEnabled(true);
		axis.setTitle("log10");
		
		axis = getAxis(Axis.Y2Axis);
		axis.setScale(AxisScale.LinearScale);
		axis.setGridLinesEnabled(true);
		axis.setVisible(true);
		axis.setTitle("cells/ul");

		setLegendEnabled(true);
		
		colors = generateColors(10);
	}

//	@Override
//	public void drawMarker(WDataSeries series, WPainterPath result) {
//		if(series instanceof CutOffSeries){
//			int r = 3;
//			series.setBrush(new WBrush(WColor.white));
//			result.addEllipse(-r, -r, 2*r, 2*r);
//			result.lineTo(-r, -r);
//			result.lineTo(0, 0);
//			result.lineTo(r, r);
//			result.lineTo(0, 0);
//			result.lineTo(r, -r);
//			result.lineTo(-r, r);
//			result.closeSubPath();
//		} else
//			super.drawMarker(series, result);
//	}
	
	protected WColor[] generateColors(int n){
		WColor colors[] = new WColor[n];
		
		float j = 0f;
		for(int i=0; i<colors.length; ++i){
			Color c = Color.getHSBColor(j, 1f, 0.9f);
			j += 1f / (n+2);
			colors[i] = new WColor(c.getRed(),c.getGreen(),c.getBlue()); 
		}
		
		return colors;
	}
	
	public WColor getColor(int i){
		return colors[i % colors.length];
	}
	
	public void addSeries(TestResultSeries series){
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
	
	@Override
	protected void paintEvent(WPaintDevice paintDevice) {
		WPainter painter = new WPainter(paintDevice);
		painter.setRenderHint(RenderHint.Antialiasing,true);
		this.paint(painter);
		
		double sy = getHeight().getValue() - getPlotAreaPadding(Side.Bottom) + 50;
		double spacing = 1;
		double height = 15;
		
		WDate maxDate = new WDate(new Date());
		
		double i = sy;
		
		painter.setRenderHint(RenderHint.Antialiasing,false);
		painter.drawLine(0, i, getWidth().getValue(), i);
		painter.setRenderHint(RenderHint.Antialiasing,true);

		for(String drug : drugsUsed.keySet()){
			drugsUsed.put(drug, i);

			painter.drawText(new WRectF(0, i, 100, i+height), EnumSet.of(AlignmentFlag.AlignCenter,AlignmentFlag.AlignLeft), drug);
			painter.drawText(new WRectF(getWidth().getValue() - 100, i, 100, i+height), EnumSet.of(AlignmentFlag.AlignCenter,AlignmentFlag.AlignRight), drug);
			
			i += height+1;

			painter.setRenderHint(RenderHint.Antialiasing,false);
			painter.drawLine(0, i, getWidth().getValue(), i);
			painter.setRenderHint(RenderHint.Antialiasing,true);
		}

		double linewidth = 1;
		WPen pen = new WPen(WColor.transparent);
		pen.setWidth(new WLength(linewidth,Unit.Pixel));
		painter.setPen(pen);
		
		WBrush closedTherapyBrush = new WBrush(WColor.green);
		WBrush openTherapyBrush = new WBrush(WColor.darkGreen);
		
		painter.setRenderHint(RenderHint.Antialiasing,false);
		for(Map.Entry<Therapy, TreeSet<String>> me : drugsMap.entrySet()){
			double x1 = this.mapToDevice(new WDate(me.getKey().getStartDate()), 0).getX();
			WDate stopDate;
			if(me.getKey().getStopDate() == null){
				stopDate = maxDate;
				painter.setBrush(openTherapyBrush);
			} else {
				stopDate = new WDate(me.getKey().getStopDate());
				painter.setBrush(closedTherapyBrush);
			}
		
			double x2 = this.mapToDevice(stopDate,0).getX();
			
			for(String drug : me.getValue()){
				i = drugsUsed.get(drug);

				double y1 = i+1 + spacing;
				double y2 = i + height+1 - spacing;

				painter.drawRect(x1 + linewidth, y1, x2-x1 - linewidth, y2-y1);
			}
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
	
	private WDate minTherapyDate;
	private WDate maxTherapyDate;
	
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
			minTherapyDate = new WDate(drugsMap.firstKey().getStartDate());
			maxTherapyDate = new WDate(drugsMap.lastKey().getStopDate() == null ? new Date() : drugsMap.lastKey().getStopDate());
			
			int i = getModel().getRowCount();
			getModel().insertRow(i);
			getModel().setData(i, 0, minTherapyDate);
			getModel().insertRow(++i);
			getModel().setData(i, 0, maxTherapyDate);
		}
	}
}
