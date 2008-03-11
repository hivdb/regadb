
/** Java class "WhereClause.java" generated from Poseidon for UML.
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

import java.sql.SQLException;
import java.util.*;

//import com.pharmadm.custom.rega.chem.search.MoleculeIndexingException;

/**
 * <p>
 * A WhereClause defines constraints on which tables from the database are
 * relevant and what rows to retrieve from these tables. The relevant
 * tables definition is called the 'from-part' of the clause, and the rows
 * to retrieve is called the 'where-part'. A WhereClause can produce
 * Strings that allow the Query to construct a Hibernate equivalent, which
 * can be executed against the database. Later extention to provide similar
 * Strings for SQL or Prolog should be straightforward.
 * </p>
 * <p>
 * WhereClauses can be configured to adapt the set of constraints till they
 * please the user. A WhereClause can be a composite of other WhereClauses,
 * or it can be atomic. Configuring a ComposedWhereClause involves adding
 * or removing subclauses, while configuring an AtomicWhereClause involves
 * setting values for it's Constants, and associating an OutputVariable
 * with each of its InputVariables.
 * </p>
 * <p>
 * This class supports xml-encoding.
 * The following new properties are encoded :
 *  parent
 * </p>
 *
 */
public abstract class WhereClause implements Cloneable {
    
    private WhereClause parent;
    
    /** For xml-encoding purposes only */
    public WhereClause() {
    }
    
    ///////////////////////////////////////
    // operations
    
    
    /**
     * <p>
     * Calculates a String with the 'where' part (the constraints) of the
     * Hibernate equivalent of the clause.
     * </p>
     * <p>
     *
     * @pre all Works of getQueryPreparationWorks must have been completed.
     * @param visitor TODO
     *
     * @return a String with the 'where' part (the constraints) of the
     * Hibernate equivalent of the clause
     * </p>
     */
    public abstract String acceptWhereClause(QueryVisitor visitor) throws SQLException; //, MoleculeIndexingException;
    
    /**
     * <p>
     * Calculates a String with the 'from' part (the tables to select from) of
     * the Hibernate equivalent of the clause. The from part returned excludes
     * the base Table of the Query.
     * </p>
     * <p>
     *
     * @pre all Works of getQueryPreparationWorks must have been completed.
     * @param visitor TODO
     *
     * @return a String with the 'from' part (the tables to select from, except
     * the base Table) of the Hibernate equivalent of the clause
     * </p>
     */
    public abstract String acceptFromClause(QueryVisitor visitor) throws SQLException; //, MoleculeIndexingException;
    
    /**
     * <p>
     * Returns a collection of Works that have to be performed before the query clauses
     * can be retrieved.
     * </p>
     *
     * @return a Collection with all Works required to prepare the query.
     */
    public Collection getQueryPreparationWorks() {
        Collection preparationWorks = new ArrayList(0);
        Iterator children = iterateChildren();
        while (children.hasNext()) {
            WhereClause child = (WhereClause)children.next();
            preparationWorks.addAll(child.getQueryPreparationWorks());
        }
        return preparationWorks;
    }
    
    /**
     * Gets the parent (or null if top node).
     */
    public WhereClause getParent() {
        return parent;
    }
    
    public void setParent(WhereClause newParent) {
        parent = newParent;
    }
    
    public abstract int getChildCount();
    
    /**
     * <p>
     * Adds a child to this node, if an additional one is supported.
     * </p>
     * <p>
     *
     * @throws IllegalWhereClauseCompositionException iff
     * !acceptsAdditionalChild()
     * </p>
     */
    public abstract void addChild(WhereClause child, UniqueNameContext namingContext) throws IllegalWhereClauseCompositionException;
    
    /**
     * <p>
     * Removes a child from this (Composed)WhereClause.
     * </p>
     * <p>
     *
     * @throws IllegalWhereClauseCompositionException if the given child is not
     * a child of this WhereClause
     * </p>
     */
    public abstract void removeChild(WhereClause child);
    
    public abstract void replaceChild(WhereClause oldChild, WhereClause newChild, UniqueNameContext namingContext) throws IllegalWhereClauseCompositionException;
    
    /**
     * <p>
     * Reports whether an additional child can be added to this
     * (Composed)WhereClause.
     * </p>
     * <p>
     *
     * @return true iff an additional child can be added to this
     * (Composed)WhereClause
     * </p>
     */
    public abstract boolean acceptsAdditionalChild();
    
    /**
     * <p>
     * Iterates through all immediate children of this WhereClause. If this
     * WhereClause is atomic, returns an empty Iterator. Never returns null.
     * </p>
     * <p>
     *
     * @return an Iterator through all immediate children of this WhereClause
     * </p>
     */
    public abstract Iterator iterateChildren();
    
    /**
     * <p>
     * Iterates through all immediate atomic children of this WhereClause. If
     * there are no atomic children, then an empty Iterator is returned. Never
     * returns null.
     * </p>
     * <p>
     *
     * @return an Iterator through all immediate atomic children of this
     * WhereClause
     * </p>
     */
    public abstract Iterator iterateAtomicChildren();
    
    /**
     * <p>
     * Calculates whether this WhereClause is valid, i.e. wether all constants
     * are set and all input variables are bound, for this WhereClause and for
     * all of its descendants.
     * </p>
     *
     * @return whether this WhereClause and all of its descendants are valid.
     */
    public abstract boolean isValid();
    
    /**
     * <p>
     * Returns whether this WhereClause is an AtomicWhereClause.
     * </p>
     *
     * @return whether this WhereClause is an AtomicWhereClause.
     */
    public abstract boolean isAtomic();
    
    
    /**
     * <p>
     * Gets all output variables that are visible to Input variables
     * of this WhereClause or its immediate children.
     * </p>
     *
     * @return a Collection of all OutputVariables available to bind to at
     * this point in the WhereClause composition.
     */
    public Collection getOutputVariablesAvailableForImport() {
        return getOutputVariablesAvailableForImport(null);
    }
    
    /**
     * <p>
     * Calculates a Collection with all available AtomicWhereClauses that can
     * be added to this (Composed)WhereClause if acceptsAdditionalChild().
     * </p>
     * <p>
     * The criterium used to select prototypes from the catalog is whether
     * there are output variables available for all types present amongst the
     * input variables of the prototype. a collection with copies of the
     * selected prototypes are then returned.
     * </p>
     *
     * <p>
     * @return a Collection with all available AtomicWhereClauses that can be
     * added to this (Composed)WhereClause if acceptsAdditionalChild()
     * </p>
     * <p>
     * @param catalog the catalog for the base Table of the query
     * </p>
     */
    public abstract Collection getAvailableAtomicClauses(AWCPrototypeCatalog catalog);
    
    /**
     * Collects all available output variables, excluding those from the mentioned child.
     */
    protected abstract Collection getOutputVariablesAvailableForImport(WhereClause excludeChild);
    
    /**
     * Collects all available output variables that are exported to WhereClauses elsewhere in the tree.
     */
    protected abstract Collection getExportedOutputVariables();
    
    public Object clone() throws CloneNotSupportedException {
        Map originalToCloneMap = new HashMap();
        Object clone = cloneBasics(originalToCloneMap);
        ((WhereClause)clone).cloneLinks(originalToCloneMap);
        return clone;
    }
    
    protected abstract Object cloneBasics(Map originalToCloneMap) throws CloneNotSupportedException;
    protected abstract void cloneLinks(Map originalToCloneMap) throws CloneNotSupportedException;
    
} // end WhereClause
