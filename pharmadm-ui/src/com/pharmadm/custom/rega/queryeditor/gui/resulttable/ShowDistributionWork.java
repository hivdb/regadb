/*
 * ShowDistributionWork.java
 *
 * Created on May 6, 2004, 5:59 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui.resulttable;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.pharmadm.num.dhb.DhbScientificCurves.Histogram;
import com.pharmadm.num.dhb.DhbStatistics.StatisticalMoments;
import com.pharmadm.custom.rega.queryeditor.DiscreteDistribution;
import com.pharmadm.util.work.WorkAdapter;

/**
 *
 * @author  kdg
 */
public class ShowDistributionWork extends WorkAdapter {
    
    private final TableModel tableModel;
    private final TableColumn column;
    private boolean numeric;
    private final java.text.NumberFormat percentFormat = new java.text.DecimalFormat("##.#");
    
    /** Creates a new instance of ShowDistributionWork */
    public ShowDistributionWork(TableModel tableModel, TableColumn column, boolean numeric) {
        this.tableModel = tableModel;
        this.column = column;
        this.numeric = numeric;
        setDescription("Analysing column...");
        setAbortable(false);
        setPausable(false);
    }
    
    public void execute() {
        int colIndex = column.getModelIndex();
        System.err.println("Distribution for column: " + column.getHeaderValue());
        Class colClass = tableModel.getColumnClass(colIndex);
        // check if it's numeric
        numeric = numeric && Number.class.isAssignableFrom(colClass);
        if (numeric) {
            Histogram histogram = new Histogram(tableModel.getRowCount());
            StatisticalMoments moments = new StatisticalMoments();
            int rowCount = tableModel.getRowCount();
            long nullCount = 0;
            for (int row = 0; row < rowCount; row++) {
                Number number = (Number)tableModel.getValueAt(row, colIndex);
                if (number != null) {
                    double value = number.doubleValue();
                    if (!Double.isNaN(value)) {
                        histogram.accumulate(value);
                        moments.accumulate(value);
                    } else {
                        nullCount++;
                    }
                } else {
                    nullCount++;
                }
            }
            showFrame(histogram, moments, nullCount);
        } else {
            DiscreteDistribution distribution = new DiscreteDistribution();
            int rowCount = tableModel.getRowCount();
            for (int row = 0; row < rowCount; row++) {
                Object item = tableModel.getValueAt(row, colIndex);
                distribution.accumulate(item);
            }
            DiscreteDistribution.ItemOccurrenceCount[] counts = distribution.getMostOccuringItemsAndNull(20);
            for (int i = 0; i < counts.length; i++) {
                DiscreteDistribution.ItemOccurrenceCount count = counts[i];
                System.out.println("   distribution: " + count.getOccurenceCount() + " times " + count.getItem());
            }
            showFrame(distribution, tableModel.getRowCount());
        }
    }
    
    private void showFrame(Histogram histogram, StatisticalMoments moments, long nullCount) {
        JPanel histogramPanel = new HistogramPanel(histogram, moments);
        histogramPanel.setPreferredSize(new java.awt.Dimension(450, 220));
        JFrame frame = new JFrame("Histogram for column " + column.getHeaderValue());
        java.awt.Container contentPane = frame.getContentPane();
        contentPane.add(histogramPanel, BorderLayout.CENTER);
        String nullString;
        if (nullCount > 0) {
            nullString = " NULL or NaN values: " + nullCount + " (" + percentFormat.format(nullCount * 100.0 / (moments.count() + nullCount)) + "%) ";
        } else {
            nullString = " No NULLs or NaNs. ";
        }
        contentPane.add(new javax.swing.JLabel(nullString), BorderLayout.SOUTH);
        frame.pack();
        com.pharmadm.util.gui.FrameShower.showFrameLater(frame);
    }
    
    private void showFrame(DiscreteDistribution distribution, long totalCount) {
        JPanel panel = new DiscreteDistributionChartPanel(distribution, totalCount);
        panel.setPreferredSize(new java.awt.Dimension(450, 220));
        JFrame frame = new JFrame("Distribution for column " + column.getHeaderValue());
        java.awt.Container contentPane = frame.getContentPane();
        contentPane.add(panel);
        frame.pack();
        com.pharmadm.util.gui.FrameShower.showFrameLater(frame);
    }
}
