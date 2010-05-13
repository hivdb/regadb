/*
 * Created on Oct 13, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.ui.form.singlePatient.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.AaMutInsertion;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.UserAttribute;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.compare.DrugGenericComparator;
import net.sf.regadb.db.compare.TestResultComparator;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateFormUtils;

public class PatientChart
{
	private static final Color COLOR_VL = new Color(0, 187, 0);

	private static final Color COLOR_CD4 = new Color(0, 0, 187);

	private static final Color COLOR_DRUG_USED = Color.LIGHT_GRAY;

	private static final long serialVersionUID = 2226355652589666347L;

	private static final int ALIGNMENT_LEFT = 1;

	private static final int ALIGNMENT_RIGHT = 2;

	private static final int ALIGNMENT_CENTER = 3;

	private static final int ALIGNMENT_BOTTOM = 4;

	private static final int ALIGNMENT_TOP = 5;

	private int IMAGE_WIDTH = 1000;

	private int CHART_HEIGHT = 400;

	private static final int BORDER_H = 80;

	private static final int BORDER_V = 10;

	private static final int BORDER_CHART_DRUGS = 20;

	private static final int DRUG_HEIGHT = 20;

	private static final int BORDER_DRUGS_MUTATIONS = 20;

	private static final int MUTATION_HEIGHT = 15;

	private static final int MUTATION_WIDTH = 50;

	private static final int SYMBOL_SIZE = 10;

	private Patient data;

	private Set<DrugGeneric> drugList = new TreeSet<DrugGeneric>(new DrugGenericComparator());

	private int maxMutations;

	private Date minDate;

	private Date maxDate;

	private double minVL, maxVL, minCD4, maxCD4;

	private double scaleCD4, scaleLogVL, scaleTime;

	private BasicStroke dashed, bold;

	private int width_;

	private int height_;
    
    private String positionAlgorithm_ = null;
    
    private static DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    private static Date before1900;
    
    static {
		try {
			before1900 = formatter.parse("01-01-1900");
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }
    
    private boolean includeAsViralLoad(TestType tt){
    	return StandardObjects.isViralLoad(tt) || StandardObjects.isViralLoadLog10(tt);
    }

	public MutationBlock getMutationBlock(AaSequence a)
	{
		MutationBlock mb = new MutationBlock(a.getProtein().getAbbreviation(), a.getFirstAaPos(), a.getLastAaPos());
		
		if (positionAlgorithm_ == null ) {
			for (AaMutInsertion m : AaMutInsertion.getSortedMutInsertionList(a)) {
				if (m.isInsertion())
					mb.mutations.add(m.getPosition() + "i" + m.getAaMutationString());
				else if (!m.isSilent())
				    mb.mutations.add(m.getPosition() + m.getAaMutationString());
			}
		} else {
			Collection<String> classes = ViralIsolateFormUtils.getRelevantDrugClasses(a.getProtein().getAbbreviation());
			final Set<String> mutations = new TreeSet<String>();
			
			for (TestResult tr : a.getNtSequence().getViralIsolate()
					.getTestResults()) {
				if (tr.getTest().getDescription().equals(positionAlgorithm_)
						&& tr.getDrugGeneric() != null
						&& classes.contains(tr.getDrugGeneric().getDrugClass()
								.getClassId())) {
					ResistanceInterpretationParser parser = new ResistanceInterpretationParser() {
						public void completeScore(String drug, int level,
								double gss, String description, char sir,
								ArrayList<String> mutationsP, String remarks) {
							mutations.addAll(mutationsP);
						}
					};
					try {
						parser.parse(new InputSource(new ByteArrayInputStream(
								tr.getData())));
					} catch (SAXException e) {
						System.err.println("Parsing of resistance test failed");
					} catch (IOException e) {
						System.err.println("Parsing of resistance test failed");
					}
				}
			}
			
			for (String mut : mutations) {
				mb.mutations.add(mut);
			}
		}
		
		return mb;
	}

	public PatientChart(Patient patientData, SettingsUser us)
	{
		this.data = patientData;

		for (Therapy therapy : filterTherapies(patientData.getTherapies()))
		{
			for (TherapyGeneric tg : therapy.getTherapyGenerics())
			{
				drugList.add(tg.getId().getDrugGeneric());
			}
            for (TherapyCommercial tc : therapy.getTherapyCommercials())
            {
                for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics())
                {
                    drugList.add(dg);
                }
            }
		}
        
        for(UserAttribute ua : us.getUserAttributes())
        {
            if("chart.width".equals(ua.getName()) && ua.getValue()!=null && !ua.getValue().equals(""))
            {
                IMAGE_WIDTH = Integer.parseInt(ua.getValue());
            }
            else if("chart.height".equals(ua.getName()) && ua.getValue()!=null && !ua.getValue().equals(""))
            {
                CHART_HEIGHT = Integer.parseInt(ua.getValue());
            }
            else if("chart.mutation".equals(ua.getName()) && ua.getValue()!=null && !ua.getValue().equals(""))
            {
                positionAlgorithm_ = ua.getValue();
            }
        }

		maxMutations = 0;
		for (ViralIsolate vi : data.getViralIsolates())
		{
			for (NtSequence s : vi.getNtSequences())
			{
				for (AaSequence a : s.getAaSequences())
				{
					MutationBlock mb = getMutationBlock(a);
					if (mb.mutations.size() > 0) {
						int l = mb.numLines() + 2;
						if (l > maxMutations)
							maxMutations = l;
					}
				}
			}
		}

		dashed = new BasicStroke();
		dashed = new BasicStroke(dashed.getLineWidth(), dashed.getEndCap(), dashed.getLineJoin(), dashed
				.getMiterLimit(), new float[] { 10, 5 }, 0);
		bold = new BasicStroke();
		bold = new BasicStroke(2, bold.getEndCap(), bold.getLineJoin(), bold.getMiterLimit(), bold.getDashArray(), 0);
	}
    
    public void setSettings(int width, int height, Test algorithm) {
        IMAGE_WIDTH = width;
        CHART_HEIGHT = height;
        
        if (algorithm == null)
        	return;
        	
        if(!algorithm.getDescription().equals(positionAlgorithm_)) {
            positionAlgorithm_ = algorithm.getDescription();
        }
    }
    
    private HashMap<String, String> createPositionMap(byte [] data) {
        if(data!=null) {
            Table csv = new Table(new ByteArrayInputStream(data), false);
            if("protein".equals(csv.valueAt(0, 0)) && "position".equals(csv.valueAt(1, 0))) {
                HashMap<String, String> map = new HashMap<String, String>();
                for(int i = 1; i<csv.numRows(); i++) {
                    map.put(csv.valueAt(0, i).toLowerCase()+csv.valueAt(1, i), null);
                }
                return map;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    private List<TestResult> getSortedTestResults()
    {
        List<TestResult> list = new ArrayList<TestResult>();
        
        for (TestResult r : data.getTestResults())
        {
            list.add(r);
        }
        
        Collections.sort(list, new TestResultComparator());
        
        return list;
    }
    
	public void writePngChart(int dimx, OutputStream result) throws IOException
	{
		width_ = IMAGE_WIDTH;
		height_ = CHART_HEIGHT + BORDER_CHART_DRUGS + DRUG_HEIGHT * drugList.size() + BORDER_DRUGS_MUTATIONS
				+ ((MUTATION_HEIGHT + 1) * maxMutations);

		BufferedImage imBig = new BufferedImage(width_, height_, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = (Graphics2D) imBig.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		try
		{
			paintPic(graphics);
		}
		catch (Exception e)
		{
			System.err.println("PAINT_EXCEPTION");
			e.printStackTrace();
		}

		ImageIO.write(imBig, "png", result);
	}
	
	public void writePngChartToFile(int dmx, File outputFile)
	{
		try
		{
			FileOutputStream out = new FileOutputStream(outputFile);
			writePngChart(dmx, out);
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void paintPic(Graphics2D vg)
	{
		vg.setColor(Color.white);
		vg.fillRect(0, 0, width_, height_);
		determineTimeLimits();
		determineCD4Limits();
		determineVLLimits();
		drawAxes(vg);
		drawValues(vg);
		drawDrugUsage(vg);
		drawSequenceInformation(vg);

		vg.setColor(Color.BLACK);
	}

	private void drawSequenceInformation(Graphics2D vg)
	{
		int yTop = CHART_HEIGHT + BORDER_CHART_DRUGS + DRUG_HEIGHT * drugList.size() + BORDER_DRUGS_MUTATIONS;

		for (ViralIsolate vi : data.getViralIsolates())
		{
			if (vi.getSampleDate() == null)
				continue;
			
			int yStart = yTop;
			
			int x = computeX(vi.getSampleDate());
			vg.setColor(Color.BLACK);
			vg.setStroke(dashed);
			vg.drawLine(x, BORDER_V * 2, x, yTop);

			vg.setStroke(new BasicStroke());

			List<MutationBlock> mutationBlocks = new ArrayList<MutationBlock>();
			Collections.sort(mutationBlocks);
			
			for (NtSequence s : vi.getNtSequences()) {
				for (AaSequence a : s.getAaSequences()) {
					MutationBlock mb = getMutationBlock(a);
					mutationBlocks.add(mb);
				}
			}
			
			for (MutationBlock mb : mutationBlocks) {
					if (mb.mutations.size() == 0)
						continue;
					
					int lines = mb.numLines();

					vg.setColor(Color.LIGHT_GRAY);
					vg.drawRect(x - MUTATION_WIDTH / 2, yStart, MUTATION_WIDTH, lines * MUTATION_HEIGHT - 10);

					vg.setColor(Color.BLACK);
					drawString(vg, mb.proteinName, ALIGNMENT_RIGHT, ALIGNMENT_BOTTOM, x - MUTATION_WIDTH / 2 - 6,
							yStart + lines * MUTATION_HEIGHT / 2);
					drawString(vg, mb.minBound + "-" + mb.maxBound, ALIGNMENT_RIGHT, ALIGNMENT_TOP, x - MUTATION_WIDTH
							/ 2 - 6, yStart + lines * MUTATION_HEIGHT / 2);

					for (int l = 0; l < mb.mutations.size(); ++l)
					{
						drawString(vg, mb.mutations.get(l), ALIGNMENT_LEFT, ALIGNMENT_TOP, x - MUTATION_WIDTH / 2 + 3,
								yStart + l * MUTATION_HEIGHT + 2);
					}

					yStart += lines * MUTATION_HEIGHT;
			}
		}
	}

	private void drawDrugUsage(Graphics2D vg)
	{
		int n = 0;

		for (DrugGeneric drug : drugList)
		{
			int ytop = CHART_HEIGHT + BORDER_CHART_DRUGS + (n * DRUG_HEIGHT);

			for (Therapy f : filterTherapies(data.getTherapies()))
			{
				for(TherapyGeneric tg : f.getTherapyGenerics())
                {
                    if(drug.getGenericName().equals(tg.getId().getDrugGeneric().getGenericName()))
                    {
                        drawTherapy(f, vg, ytop);
                    }
                }
                
                for(TherapyCommercial tc : f.getTherapyCommercials())
                {
                    for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics())
                    {
                        if(dg.getGenericName().equals(drug.getGenericName()))
                        {
                            drawTherapy(f, vg, ytop);
                        }
                    }
                }
			}
            n++;
		}
	}
    
    private void drawTherapy(Therapy f, Graphics2D vg, int ytop)
    {
            int x1 = computeX(f.getStartDate());
            int x2 = computeX(f.getStopDate() == null ? maxDate : f.getStopDate());
            x2 = Math.max(x2, x1 + 1);

            vg.setColor(COLOR_DRUG_USED);
            vg.fillRect(x1, ytop + 3, x2 - x1, DRUG_HEIGHT - 5);
    }

	private double getNumberValue(TestResult result)
	{
		double d;
		if (hasLimitedNumberValue(result))
			d = Double.parseDouble(result.getValue().substring(1));
		else
			d = Double.parseDouble(result.getValue());
		
		if(StandardObjects.isViralLoad(result.getTest().getTestType()))
			return d == 0 ? 1 : d;
		if(StandardObjects.isViralLoadLog10(result.getTest().getTestType()))
			return Math.pow(10, d);
		return d;
	}

	private boolean isClipped(TestResult result)
	{
		if (hasLimitedNumberValue(result))
			return result.getValue().charAt(0) != '=';
		else
			return false;
	}
	
	private boolean hasLimitedNumberValue(TestResult tr) {
		return tr.getTest().getTestType().getValueType().getDescription().equals(StandardObjects.getLimitedNumberValueType().getDescription());
	}

	private void drawValues(Graphics2D vg)
	{
		double lastValue = 0;
		Date lastDate = null;

		vg.setStroke(bold);

		for (TestResult r : getSortedTestResults())
		{
			if (includeAsViralLoad(r.getTest().getTestType()))
			{
				double v = getNumberValue(r);
				boolean clipped = isClipped(r);
				Date d = r.getTestDate();

				if (v != 0)
				{
					drawValue(vg, r.getTest().getTestType(), lastValue, lastDate, v, d, clipped);

					lastValue = v;
					lastDate = d;
				}
			}
		}
        
        lastDate = null;
        for (TestResult r : getSortedTestResults())
        {
            if (StandardObjects.isCD4(r.getTest().getTestType()))
            {
                double v = getNumberValue(r);
                boolean clipped = isClipped(r);
                Date d = r.getTestDate();

                if (v != 0)
                {
                    drawValue(vg, r.getTest().getTestType(), lastValue, lastDate, v, d, clipped);

                    lastValue = v;
                    lastDate = d;
                }
            }
        }

		vg.setStroke(new BasicStroke());
	}

	private void drawValue(Graphics2D vg, TestType testType, double lastValue, Date lastDate, double v, Date d,
			boolean clipped)
	{
		int x1, y1;
		int x2 = 0, y2 = 0;
		Color c;
        int symbol;

		if (StandardObjects.isCD4(testType))
		{
			y1 = computeCD4Y(v);
			if (lastDate != null)
				y2 = computeCD4Y(lastValue);
			c = COLOR_CD4;
            symbol = VectorGraphicsConstants.SYMBOL_STAR;
		}
		else
		{
			y1 = computeVLY(v);
			if (lastDate != null)
				y2 = computeVLY(lastValue);
			c = COLOR_VL;
            symbol = VectorGraphicsConstants.SYMBOL_UP_TRIANGLE;
		}

		x1 = computeX(d);
		if (lastDate != null)
			x2 = computeX(lastDate);

		vg.setColor(c);

		SymbolDrawer.drawSymbol(x1 + 1, y1 + 1, SYMBOL_SIZE, clipped ? VectorGraphicsConstants.SYMBOL_CIRCLE
				: symbol, vg);
        
		if (lastDate != null)
		{
		    vg.drawLine(x1, y1, x2, y2);
		}
	}

	private void determineVLLimits()
	{
		minVL = 10;
		maxVL = 10000;

		for (TestResult r : getSortedTestResults())
		{
			if (includeAsViralLoad(r.getTest().getTestType()))
			{
				double v = getNumberValue(r);
				if (v != 0)
				{
					minVL = Math.min(minVL, v);
					maxVL = Math.max(maxVL, v);
				}
			}
		}

		scaleLogVL = ((double) (CHART_HEIGHT - 2 * BORDER_V - 50)) / (Math.log10(maxVL) - Math.log10(minVL));
	}

	private void determineCD4Limits()
	{
		minCD4 = 0;
		maxCD4 = 500;

		for (TestResult r : getSortedTestResults())
		{
			if (StandardObjects.isCD4(r.getTest().getTestType()))
			{
				double v = getNumberValue(r);
				minCD4 = Math.min(minCD4, v);
				maxCD4 = Math.max(maxCD4, v);
			}
		}

		scaleCD4 = ((double) (CHART_HEIGHT - 2 * BORDER_V - 50)) / (maxCD4 - minCD4);
	}

	private void drawAxes(Graphics2D vg)
	{
		vg.setColor(Color.BLACK);

		vg.drawRect(BORDER_H, BORDER_V, IMAGE_WIDTH - 2 * BORDER_H, CHART_HEIGHT - 2 * BORDER_V);

		/* CD4 */
		vg.setColor(COLOR_CD4);
		drawString(vg, "CD4", ALIGNMENT_RIGHT, ALIGNMENT_TOP, BORDER_H - 3, BORDER_V + 2);
		drawString(vg, "(cells/ul)", ALIGNMENT_RIGHT, ALIGNMENT_TOP, BORDER_H - 3, BORDER_V + 15);
		SymbolDrawer.drawSymbol(BORDER_H + 3 + SYMBOL_SIZE / 2, BORDER_V + 20, SYMBOL_SIZE,
                VectorGraphicsConstants.SYMBOL_STAR, vg);
		drawCD4Label(vg, 0);
		int step = Math.max(200, (((int) maxCD4 / 6) / 200) * 200);
		for (int i = 200; i < maxCD4; i += 200)
			drawCD4Label(vg, i);

		/* VL */
		vg.setColor(COLOR_VL);
		drawString(vg, "VL", ALIGNMENT_LEFT, ALIGNMENT_TOP, IMAGE_WIDTH - BORDER_H + 3, BORDER_V + 2);
		drawString(vg, "(copies/ml)", ALIGNMENT_LEFT, ALIGNMENT_TOP, IMAGE_WIDTH - BORDER_H + 3, BORDER_V + 15);
		SymbolDrawer.drawSymbol(IMAGE_WIDTH - BORDER_H - 3 - SYMBOL_SIZE / 2, BORDER_V + 20, SYMBOL_SIZE,
                VectorGraphicsConstants.SYMBOL_UP_TRIANGLE, vg);
		drawVLLabel(vg, 10, "10");
		drawVLLabel(vg, 50, "50");
		drawVLLabel(vg, 100, "100");
		drawVLLabel(vg, 500, "500");
		drawVLLabel(vg, 1E3, "1E3");
		drawVLLabel(vg, 5E3, "5E3");
		drawVLLabel(vg, 1E4, "1E4");
		drawVLLabel(vg, 5E4, "5E4");
		drawVLLabel(vg, 1E5, "1E5");
		drawVLLabel(vg, 5E5, "5E5");
		drawVLLabel(vg, 1E6, "1E6");
		drawVLLabel(vg, 5E6, "5E6");
		drawVLLabel(vg, 1E7, "1E7");

		/* Dates */
		vg.setColor(Color.BLACK);

		double yearSpan = ((double) (maxDate.getTime() - minDate.getTime())) / ((double) 1000 * 60 * 60 * 24 * 365);
		int halfYearSteps = Math.max(1, ((int) (yearSpan / 0.5)) / 5);
		Calendar cal = Calendar.getInstance();
		if (halfYearSteps > 4)
		{
			halfYearSteps = (halfYearSteps / 2) * 2; // make to step in
														// multiple of years
			cal.setTime(minDate);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, 0);
			cal.add(Calendar.YEAR, 1);
		}
		else
		{
			cal.setTime(minDate);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			if (cal.get(Calendar.MONTH) < 6)
				cal.set(Calendar.MONTH, 6);
			else
			{
				cal.set(Calendar.MONTH, 0);
				cal.add(Calendar.YEAR, 1);
			}
		}

		for (; cal.getTime().before(maxDate);)
		{
			drawDateLabel(vg, cal);
			cal.add(Calendar.MONTH, 6 * halfYearSteps);
		}

		/* Drugs */
		vg.setColor(Color.BLACK);

		int n = 0;
		for (DrugGeneric drug : drugList)
		{
			int ytop = CHART_HEIGHT + BORDER_CHART_DRUGS + (n * DRUG_HEIGHT);
			drawString(vg, drug.getGenericId(), ALIGNMENT_LEFT, ALIGNMENT_CENTER, IMAGE_WIDTH - BORDER_H + 6, ytop
					+ DRUG_HEIGHT / 2);
			drawString(vg, drug.getGenericId(), ALIGNMENT_RIGHT, ALIGNMENT_CENTER, BORDER_H - 6, ytop + DRUG_HEIGHT
					/ 2);
			vg.drawRect(BORDER_H, ytop + 2, IMAGE_WIDTH - 2 * BORDER_H, DRUG_HEIGHT - 4);
			++n;
		}
	}

	private void drawDateLabel(Graphics2D vg, Calendar cal)
	{
		int x = computeX(cal.getTime());

		vg.drawLine(x, CHART_HEIGHT - BORDER_V - 2, x, CHART_HEIGHT - BORDER_V + 2);
		drawString(vg, (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), ALIGNMENT_CENTER, ALIGNMENT_TOP,
				x, CHART_HEIGHT - BORDER_V + 6);
	}

	private int computeX(Date d)
	{
		return BORDER_H + (int) (((d.getTime() - minDate.getTime())) * scaleTime);
	}

	private void drawVLLabel(Graphics2D vg, double i, String label)
	{
		if (i > maxVL)
			return;

		int y = computeVLY(i);

		vg.drawLine(IMAGE_WIDTH - BORDER_H - 2, y, IMAGE_WIDTH - BORDER_H + 2, y);
		drawString(vg, label, ALIGNMENT_LEFT, ALIGNMENT_CENTER, IMAGE_WIDTH - BORDER_H + 6, y);
	}

	private int computeVLY(double i)
	{
		return CHART_HEIGHT - BORDER_V - (int) ((Math.log10(i) - Math.log10(minVL)) * scaleLogVL);
	}

	private void drawCD4Label(Graphics2D vg, int i)
	{
		int y = computeCD4Y(i);

		vg.drawLine(BORDER_H - 2, y, BORDER_H + 2, y);
		drawString(vg, new Integer(i).toString(), ALIGNMENT_RIGHT, ALIGNMENT_CENTER, BORDER_H - 6, y);
	}

	private int computeCD4Y(double i)
	{
		return CHART_HEIGHT - BORDER_V - (int) ((i - minCD4) * scaleCD4);
	}

	private void drawString(Graphics2D vg, String s, int alignmentX, int alignmentY, int x, int y)
	{
		Rectangle2D r = vg.getFontMetrics().getStringBounds(s, vg);
		vg.drawString(s, (int) (x - (alignmentX == ALIGNMENT_RIGHT ? r.getWidth() : alignmentX == ALIGNMENT_CENTER ? r
				.getWidth() / 2 : 0)), (int) (y + (alignmentY == ALIGNMENT_TOP ? r.getHeight()
				: alignmentY == ALIGNMENT_CENTER ? (r.getHeight() / 2 - 2) : 0)));
	}

	private void determineTimeLimits()
	{
		Calendar cal = Calendar.getInstance();

		maxDate = cal.getTime();
		cal.add(Calendar.YEAR, -1);

		minDate = cal.getTime();

		for (TestResult r : getSortedTestResults())
		{
			if (includeAsViralLoad(r.getTest().getTestType()) || StandardObjects.isCD4(r.getTest().getTestType()))
			{
				expandBounds(r.getTestDate());
			}
		}

		for (Therapy f : filterTherapies(data.getTherapies()))
		{
			expandBounds(f.getStartDate());
			expandBounds(f.getStopDate());
		}

		for (ViralIsolate vi : data.getViralIsolates())
		{
			expandBounds(vi.getSampleDate());
		}

		cal.setTime(minDate);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		minDate = cal.getTime();

		scaleTime = ((double) (IMAGE_WIDTH - 2 * BORDER_H)) / (maxDate.getTime() - minDate.getTime());
	}

	private void expandBounds(Date d)
	{
		if (d != null)
		{
			if (d.before(minDate))
				minDate = d;
			if (d.after(maxDate))
				maxDate = d;
		}
	}
	
	private Set<Therapy> filterTherapies(Set<Therapy> therapies) {		
		for(Iterator<Therapy> i = therapies.iterator(); i.hasNext();) {
			Therapy t = i.next();
			if(t.getStartDate().before(before1900)) {
				i.remove();
			}
		}
		
		return therapies;
	}
}
