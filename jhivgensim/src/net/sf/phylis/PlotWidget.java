package net.sf.phylis;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.*;

import javax.swing.JPanel;

/**
 * A widget that produces a simple plot of a value graph.
 */
public class PlotWidget extends JPanel {
	private List plotData;
	private List plotLabels;
	private List plotXValues;
	
	public PlotWidget() {
		this.plotData = new ArrayList();
		this.plotLabels = new ArrayList();
		this.plotXValues = new ArrayList();
		
		setBackground(Color.white);
		setForeground(Color.black);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Insets insets = getInsets();
		int currentWidth = getWidth() - insets.left - insets.right;
		int currentHeight = getHeight() - insets.top - insets.bottom;

		Font currentFont = g.getFont();
		FontMetrics currentMetrics = getFontMetrics(currentFont);

		int longestLabelLength = 0;
		for (int i = 0; i < plotLabels.size(); ++i) {
			longestLabelLength = Math.max(longestLabelLength,
										  currentMetrics.stringWidth((String) plotLabels.get(i)));
		}

		final String XLabel = "Position";
		final String YLabel = "Support";
		final int XAxisLeft = insets.left + 15 + currentMetrics.stringWidth(YLabel);
		final int XAxisRight = currentWidth - insets.right - 30 - longestLabelLength;
		final int YAxisBottom = currentHeight - insets.bottom - 15 - currentMetrics.getHeight();
		final int YAxisTop = insets.top + 15;
		final int arrow = 5;

		/*
		 * Axes
		 */

		// axes
		g.drawLine(XAxisLeft, YAxisTop, XAxisLeft, YAxisBottom);
		g.drawLine(XAxisLeft, YAxisBottom, XAxisRight, YAxisBottom);

		// arrow Y axis
		g.drawLine(XAxisLeft - arrow, YAxisTop + 2*arrow, XAxisLeft, YAxisTop);
		g.drawLine(XAxisLeft + arrow, YAxisTop + 2*arrow, XAxisLeft, YAxisTop);
		
		// arrow X axis
		g.drawLine(XAxisRight - 2*arrow, YAxisBottom - arrow, XAxisRight, YAxisBottom);
		g.drawLine(XAxisRight - 2*arrow, YAxisBottom + arrow, XAxisRight, YAxisBottom);

		// axis labels
		g.drawString(YLabel, insets.left + 7, YAxisTop + 7 + currentMetrics.getHeight());
		g.drawString(XLabel, XAxisRight - 10 - currentMetrics.stringWidth(XLabel),
							 YAxisBottom + 7 + currentMetrics.getHeight());
		
		Color colors[] = {
			Color.blue,
			Color.green,
			Color.red,
			Color.cyan,
			Color.magenta,
			Color.orange,
			Color.pink,
			Color.yellow,
			Color.black
		};

		/*
		 * Legend
		 */
		
		final int legendX = XAxisRight + 3;
		final int legendLine = 10;
		final int legendY = 30;
		final int legendStep = currentMetrics.getHeight() + 2;
		for (int i = 0; i < plotLabels.size(); ++i) {
			int thisY = legendY + i * legendStep;
			g.setColor(colors[i % colors.length]);
			g.drawLine(legendX, thisY - 3, legendX + legendLine, thisY - 3);
			g.drawString((String) plotLabels.get(i), legendX + legendLine + 3, thisY);
		}

		/*
		 * Data
		 */
		
		if (!plotData.isEmpty()) {
			
			int numPoints = plotXValues.size();	// assume not X-Y but simple value plot
			float XScale = ((float) (XAxisRight - XAxisLeft)) / numPoints;
			float YScale = (YAxisBottom - YAxisTop);

			for (int i = 0; i < plotData.size(); ++i) {
				List YValues = (List) plotData.get(i);
				int lastX = 0;
				int lastY = 0;
				
				g.setColor(colors[i % colors.length]);
				
				for (int j = 0; j < plotXValues.size(); ++j) {
					int newX = (int) (XAxisLeft + j * XScale);
					int newY = (int) (YAxisBottom - ((Float) YValues.get(j)).floatValue() * YScale);

					if (j != 0) {
						g.drawLine(lastX, lastY, newX, newY);
					}

					lastX = newX;
					lastY = newY;
				}
			}
		}
	}

	protected void setData(List plotData, List plotLabels, List plotXValues) {
		this.plotData = plotData;
		this.plotLabels = plotLabels;
		this.plotXValues = plotXValues;
		
		repaint();
	}

	protected boolean hasPlotData() {
		return !plotData.isEmpty();
	}

	public List getPlotData() {
		return plotData;
	}

	public List getPlotLabels() {
		return plotLabels;
	}

	public List getPlotXValues() {
		return plotXValues;
	}

}
