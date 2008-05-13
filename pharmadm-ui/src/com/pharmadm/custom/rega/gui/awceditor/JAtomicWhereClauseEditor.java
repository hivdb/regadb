
/** Java class "AtomicWhereClauseEditor.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.gui.awceditor;


import com.pharmadm.custom.rega.gui.configurers.JVisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;

import com.pharmadm.custom.rega.queryeditor.wordconfiguration.AtomicWhereClauseEditor;


public class JAtomicWhereClauseEditor extends AtomicWhereClauseEditor {
    
    ///////////////////////////////////////
    // associations
    public JAtomicWhereClauseEditor(QueryContext context, AtomicWhereClause clause) {
    	super(context, clause);
    	super.setVisualizationComponentFactory(new JVisualizationComponentFactory(this));
    }
}
