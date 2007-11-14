
/** Java class "InclusiveOrClause.java" generated from Poseidon for UML.
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
 * An InclusiveOrClause constrains the rows to be selected to rows
 * satisfying the logical INCLUSIVE OR of the constraints of the
 * subclauses. No tables are relevant for an InclusiveOrClause.
 * </p>
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */
public class InclusiveOrClause extends ComposedWhereClause {
    
    /** For xml-encoding purposes only */
    public InclusiveOrClause() {
    }
    
    
    /**
     * An InclusiveOrClause accepts unlimited children.
     * @return true
     */
    public boolean acceptsAdditionalChild() {
        return true;
    }
    
    protected Collection getExportedOutputVariables() {
        return new ArrayList(0);
    }
    
    // Optimization : the OR clause has no output variables, hence an empty 
    // select clause and therefore no need for from variables ... as long as
    // its children are handled as independent "exists" subclauses
    public String getHibernateFromClause() {
        return new String("");
    }
    
    // Optimization : OR clause now uses "exists" subclauses
    public String getHibernateWhereClause() throws SQLException { //, MoleculeIndexingException {
        StringBuffer sb = new StringBuffer();
        Iterator iterChildren = iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = (WhereClause)iterChildren.next();
            String extraWhereClause = child.getHibernateWhereClause();
            if (extraWhereClause != null && (extraWhereClause.length() > 0)) {
                if (sb.length() > 0) {
                    sb.append(") or (");
                } else {
                    sb.append('(');
                }
                sb.append("EXISTS (SELECT 1 FROM ");
                String extraFromClause = child.getHibernateFromClause();
                if (extraFromClause != null && (extraFromClause.length() > 0)) {
                    sb.append(extraFromClause);
                }
                else {
                    sb.append("DUAL");
                }
                sb.append(" WHERE (");
                sb.append(extraWhereClause);
                sb.append("))");
            }
        }
        if (sb.length() > 0) {
            sb.append(')');
        }
        return sb.toString();
    }
        
    public String toString() {
        return "OR (" + getChildren().size() + ")";
    }
    
    protected Object cloneBasics(Map originalToCloneMap) throws CloneNotSupportedException {
        InclusiveOrClause clone = new InclusiveOrClause();
        Iterator iterChildren = iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = (WhereClause)iterChildren.next();
            clone.addChild((WhereClause)child.cloneBasics(originalToCloneMap), null);
        }
        return clone;
    }
    
} // end InclusiveOrClause
