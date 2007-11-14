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
package com.pharmadm.custom.rega.queryeditor.gui;

import com.pharmadm.custom.rega.queryeditor.*;
import java.awt.Color;

/**
 *
 * @author  kristof
 */
public class QueryTreeCellRenderer extends javax.swing.tree.DefaultTreeCellRenderer {
    
    /** Creates a new instance of QueryTreeCellRenderer */
    public QueryTreeCellRenderer() {
    }
    
    
    
    public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (! leaf || ((WhereClause)((WhereClauseTreeNode)value).getUserObject()).isValid()) {
            setForeground(Color.black);
        } else {
            setForeground(Color.red);
        } 

        return this;
    }
   
}
