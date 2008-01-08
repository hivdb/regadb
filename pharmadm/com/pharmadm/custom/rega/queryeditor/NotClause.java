
/** Java class "NotClause.java" generated from Poseidon for UML.
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
 * A NotClause constrains the rows to be selected to rows satisfying the
 * logical NOT of the constraints of the subclauses. No tables are relevant
 * for a NOTClause.
 * </p>
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */
public class NotClause extends ComposedWhereClause {
    
    /** For xml-encoding purposes only */
    public NotClause() {
    }
    
    
    /**
     * A not clause accepts at most one child.
     * @return true iff this not clause does not yet have a child clause.
     */
    public boolean acceptsAdditionalChild() {
        return (getChildren().size() < 1);
    }
    
    protected Collection getExportedOutputVariables() {
        return new ArrayList(0);
    }
    
    
    // FIXEDYOU
    public String getHibernateWhereClause() throws SQLException { //, MoleculeIndexingException {
        Iterator iterChildren = getChildren().iterator();
        if (iterChildren.hasNext()) {
            WhereClause child = (WhereClause)iterChildren.next();
            StringBuffer sb = new StringBuffer("NOT EXISTS (SELECT 1 FROM ");
            String childFromClause = child.getHibernateFromClause();
            if (childFromClause != null && (childFromClause.length() > 0)) {
                sb.append(childFromClause);
            }
            else {
                sb.append("DUAL");
            }
            sb.append(" WHERE (");
            sb.append(child.getHibernateWhereClause());
            sb.append("))");
            return sb.toString();
        } else {
            return "1=1";  // always true
        }
    }
    
    // FIXEDYOUTOO : the NOT clause has no output variables, hence an empty 
    // select clause and therefore no need for from variables ... as long as
    // its child is handled as an independent "exists" subclause
    public String getHibernateFromClause() {
        return new String("");
    }
    
    
    public String toString() {
        return "NOT (" + getChildren().size() + ")";
    }
    
    protected Object cloneBasics(Map originalToCloneMap) throws CloneNotSupportedException {
        NotClause clone = new NotClause();
        Iterator iterChildren = iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = (WhereClause)iterChildren.next();
            clone.addChild((WhereClause)child.cloneBasics(originalToCloneMap), null);
        }
        return clone;
    }
    
} // end NotClause