
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
import java.io.Serializable;
import java.sql.SQLException;

import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;

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
public class InclusiveOrClause extends ComposedWhereClause implements Serializable {
    
    /** For xml-encoding purposes only */
    public InclusiveOrClause() {
    }
    
    public InclusiveOrClause(ArrayList<WhereClause> children) {
    	super(children);
    }
    
    /**
     * An InclusiveOrClause accepts unlimited children.
     * @return true
     */
    public boolean acceptsAdditionalChild() {
        return true;
    }
    
    public List<OutputVariable> getExportedOutputVariables() {
        return new ArrayList<OutputVariable>(0);
    }
    
    // Optimization : the OR clause has no output variables, hence an empty 
    // select clause and therefore no need for from variables ... as long as
    // its children are handled as independent "exists" subclauses
    public String acceptFromClause(QueryVisitor visitor) {
        return visitor.visitFromClauseInclusiveOrClause(this);
    }
    
    // Optimization : OR clause now uses "exists" subclauses
    public String acceptWhereClause(QueryVisitor visitor) throws SQLException { //, MoleculeIndexingException {
        return visitor.visitWhereClauseInclusiveOrClause(this);
    }
        
    public String toString() {
        return "OR (" + getChildren().size() + ")";
    }
    
    protected Object cloneBasics(Map<ConfigurableWord, ConfigurableWord> originalToCloneMap) throws CloneNotSupportedException {
        InclusiveOrClause clone = new InclusiveOrClause();
        Iterator<WhereClause> iterChildren = iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = (WhereClause)iterChildren.next();
            clone.addChild((WhereClause)child.cloneBasics(originalToCloneMap), null, null);
        }
        return clone;
    }
    
    protected Collection<OutputVariable> getOutputVariablesAvailableForImport(WhereClause excludeChild) {
        Collection<OutputVariable> result;
        if (getParent() == null) {
            result = new ArrayList<OutputVariable>();
        } else {
            result = getParent().getOutputVariablesAvailableForImport(this);
        }
        return result;
    }
    
} // end InclusiveOrClause
