/*
 * QueryTreeCellRenderer.java
 *
 * Created on October 17, 2003, 3:47 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor.gui;

import com.pharmadm.custom.rega.reporteditor.*;
import java.awt.Color;

/**
 *
 * @author  kristof
 */
public class ReportListCellRenderer extends javax.swing.DefaultListCellRenderer {
    
    QueryOutputReportSeeder seedController;
    
    /** Creates a new instance of ReportListCellRenderer */
    public ReportListCellRenderer(QueryOutputReportSeeder seedController) {
        this.seedController = seedController;
    }
    
    
    
    public java.awt.Component getListCellRendererComponent(javax.swing.JList jList, Object value, int index, boolean sel, boolean hasFocus) {
        super.getListCellRendererComponent(jList, value, index, sel, hasFocus);
        if (((DataGroup)value).isValid()) {
        // if (! leaf || ((WhereClause)((WhereClauseTreeNode)value).getUserObject()).isValid()) {
            setForeground(Color.black);
        } else {
            setForeground(Color.red);
        } 
        setText(((DataGroup)value).getHumanStringValue(seedController));

        return this;
    }
   
}
