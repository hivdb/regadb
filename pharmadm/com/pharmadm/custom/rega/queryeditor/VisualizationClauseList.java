
/** Java class "VisualizationClauseList.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;


/**
 * <p>
 * A VisualizationClauseList can compose a sentence from its Words that is
 * intelligible to humans.
 * </p>
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */
public class VisualizationClauseList extends OrderedAWCWordList {

    /** For xml-encoding purposes only */
    public VisualizationClauseList() {
    }
    
    public VisualizationClauseList(AtomicWhereClause owner) {
        super(owner);
    }
}



