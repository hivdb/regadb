/*
 * AWCPersistenceDelegate.java
 *
 * Created on October 27, 2003, 16:32 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.persist;

import java.beans.*;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;

/**
 *
 * @author  kristof
 */
public class AWCPersistenceDelegate extends java.beans.DefaultPersistenceDelegate {
    
    protected Expression instantiate(Object oldInstance, Encoder out) {
        AtomicWhereClause clause = (AtomicWhereClause)oldInstance;
        return new Expression(clause, clause.getClass(), "new", new Object[]{clause.getInputVariables(), clause.getOutputVariables(), clause.getFromVariables(), clause.getConstants(), clause.getVisualizationClauseList(), clause.getWhereClauseComposer()});
    }
}
