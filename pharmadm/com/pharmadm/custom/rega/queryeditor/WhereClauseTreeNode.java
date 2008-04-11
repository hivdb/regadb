/*
 * WhereClauseTreeNode.java
 *
 * Created on August 28, 2003, 11:02 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.util.*;

/**
 *
 * @author  kristof
 */
public class WhereClauseTreeNode extends javax.swing.tree.DefaultMutableTreeNode {
    
    /** Creates a new instance of WhereClauseTreeNode */
    public WhereClauseTreeNode(WhereClause clause) {
        super(clause);
        if (clause instanceof ComposedWhereClause) {
            setAllowsChildren(true);
            Iterator<WhereClause> iter = clause.iterateChildren();
            while (iter.hasNext()) {
                WhereClause childClause = iter.next();
                WhereClauseTreeNode childNode = new WhereClauseTreeNode(childClause);
                this.add(childNode);
            }
        }
        else {
            setAllowsChildren(false);
        }
    }
}
