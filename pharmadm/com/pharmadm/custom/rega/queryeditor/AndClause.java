
/** Java class "AndClause.java" generated from Poseidon for UML.
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

import java.util.*;
import java.sql.SQLException;

//import com.pharmadm.custom.rega.chem.search.MoleculeIndexingException;

/**
 * <p>
 * An AndClause constrains the rows to be selected to rows satisfying the
 * logical AND of the constraints of the subclauses. All tables that are
 * relevant for any of the subclauses of this AndClause, are also relevant
 * for this AndClause.
 * </p>
 * 
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */
public class AndClause extends ComposedWhereClause {
    
    
    /** For xml-encoding purposes only */
    public AndClause() {
    }
    
    /**
     * An AndClause accepts unlimited children.
     * @return true
     */
    public boolean acceptsAdditionalChild() {
        return true;
    }
    
    
    public String getHibernateWhereClause() throws SQLException { //, MoleculeIndexingException {
        StringBuffer sb = new StringBuffer();
        Iterator iterChildren = iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = (WhereClause)iterChildren.next();
            String extraWhereClause = child.getHibernateWhereClause();
            if (extraWhereClause != null && (extraWhereClause.length() > 0)) {
                if (sb.length() > 0) {
                    sb.append(") and (");
                } else {
                    sb.append('(');
                }
                sb.append(extraWhereClause);
            }
        }
        if (sb.length() > 0) {
            sb.append(')');
        }
        return sb.toString();
    }
    
    protected Collection getExportedOutputVariables() {
        Collection result = new ArrayList();
        Iterator iterCh = iterateChildren();
        while (iterCh.hasNext()) {
            WhereClause aChild = (WhereClause)iterCh.next();
            result.addAll(aChild.getExportedOutputVariables());
        }
        return result;
    }
    
    public String toString() {
        return "AND (" + getChildren().size() + ")";
    }
    
    protected Object cloneBasics(Map originalToCloneMap) throws CloneNotSupportedException {
        AndClause clone = new AndClause();
        Iterator iterChildren = iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = (WhereClause)iterChildren.next();
            clone.addChild((WhereClause)child.cloneBasics(originalToCloneMap), null);
        }
        return clone;
    }
    
} // end AndClause


