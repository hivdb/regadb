/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui.resulttable;

import java.awt.Color;
import java.awt.event.*;
import java.util.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.pharmadm.util.gui.chart.*;
import com.pharmadm.util.settings.*;
import com.pharmadm.custom.rega.queryeditor.DiscreteDistribution;

/**
 * Plot the distribution of discrete valued collections.
 */
public class DiscreteDistributionChartPanel extends javax.swing.JPanel {
    
    private ArrayList arcs;
    private JSplitPane split;
    private JPanel targetNamePanel = new javax.swing.JPanel();
    private JPanel targetCountsPanel = new javax.swing.JPanel();
    private NumberFormat pctFormat = new DecimalFormat("###.#%");
    
    private static final int MAX_NB_ITEMS_SHOWN = 25;  // excludes the 'others' category
    
    public DiscreteDistributionChartPanel(DiscreteDistribution discreteDist, long totalCount) {
        split = new javax.swing.JSplitPane();
        split.setDividerSize(5);
        
        JScrollPane classListScrollPane = new JScrollPane();
        JPanel classListPanel = new JPanel();
        classListPanel.setLayout(new javax.swing.BoxLayout(classListPanel, javax.swing.BoxLayout.X_AXIS));
        
        targetNamePanel.setLayout(new javax.swing.BoxLayout(targetNamePanel, javax.swing.BoxLayout.Y_AXIS));
        targetCountsPanel.setLayout(new javax.swing.BoxLayout(targetCountsPanel, javax.swing.BoxLayout.Y_AXIS));
        classListPanel.add(targetNamePanel);
        classListPanel.add(targetCountsPanel);
        classListScrollPane.setViewportView(classListPanel);
        
        arcs = new ArrayList();
        DiscreteDistribution.ItemOccurrenceCount[] counts = discreteDist.getMostOccuringItemsAndNull(MAX_NB_ITEMS_SHOWN);
        int nrClasses = ((discreteDist.getNbDistinctItems() > MAX_NB_ITEMS_SHOWN) ? (MAX_NB_ITEMS_SHOWN + 1) : MAX_NB_ITEMS_SHOWN);
        Color [] colors = NColors.nColors(nrClasses);
        long totalShownCount = 0;
        
        for (int i=0; i < counts.length; i++) {
            DiscreteDistribution.ItemOccurrenceCount count = counts[i];
            Object item = count.getItem();
            String name = ((item != null) ? item.toString() : "null");
            long currentCount = count.getOccurenceCount();
            Color color = colors[i];
            double arcRatio = (currentCount * 1.0 / totalCount);
            addArc(arcs, name, currentCount, color, arcRatio);
            totalShownCount += currentCount;
        }
        if (nrClasses > MAX_NB_ITEMS_SHOWN) {
            long otherCount = totalCount - totalShownCount;
            addArc(arcs, "Other", otherCount, Color.GRAY, (otherCount * 1.0 / totalCount));
        }
        addLabels("Nr. diff. items (excl. null)", Long.toString(discreteDist.getNbDistinctItems()-1), Color.BLACK);
        split.setLeftComponent(classListScrollPane);
//        JComponent pie = new Pie(arcs);
//        split.setRightComponent(pie);
        setLayout(new java.awt.BorderLayout());
        add(split);
        //classListPanel.revalidate();
    }
    
    private void addArc(List arcs, String name, long count, Color color, double arcRatio) {
        String countString = Long.toString(count);
        String formattedCount = countString + " (" + pctFormat.format(arcRatio) + ")";
        addLabels(name, formattedCount, color);
//        arcs.add(new Arc(name, countString, (float)arcRatio, color));
    }
    
    private void addLabels(String name, String formattedCount, Color color) {
        JLabel nameLabel = new JLabel(name +" ");
        nameLabel.setBorder(new LineBorder(color));
        nameLabel.setForeground(Color.black);
        targetNamePanel.add(nameLabel);
        JLabel countsLabel = new JLabel(formattedCount);
        countsLabel.setBorder(new LineBorder(color));
        countsLabel.setForeground(Color.black);
        countsLabel.setOpaque(true);
        targetCountsPanel.add(countsLabel);
    }
}
