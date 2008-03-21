
/** Java class "ComposedWhereClause.java" generated from Poseidon for UML.
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
 * A ComposedWhereClause's properties in terms of which rows it will select
 * from the database and which columns it defines as relevant, is
 * determined by the child clauses that compose it.
 * </p>
 *
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded : 
 *  children
 * </p>
 */
public abstract class ComposedWhereClause extends WhereClause {
    
    /** For xml-encoding purposes only */
    public ComposedWhereClause() {
    }
    
    ///////////////////////////////////////
    // attributes
    
    
    /**
     * <p>
     * Represents the subclauses of this ComposedWhereClause
     * </p>
     *
     */
    private ArrayList<WhereClause> children = new ArrayList<WhereClause>();
    
    ///////////////////////////////////////
    // access methods for associations
    
    public ArrayList<WhereClause> getChildren() {
        return children;
    }
    
    /**
     * For XMLdecoder only.
     */
    public void setChildren(ArrayList<WhereClause> children) {
        this.children = children;
    }
    
    public int getChildCount() {
        return children.size();
    }
    
    public void addChild(WhereClause whereClause, UniqueNameContext namingContext) {
        if (! children.contains(whereClause)) {
            if (namingContext != null) {
                namingContext.assignUniqueNamesToOutputs(whereClause);
            }
            children.add(whereClause);
            whereClause.setParent(this);
        }
    }
    
    public void removeChild(WhereClause whereClause) {
        if (children.remove(whereClause)) {
            whereClause.setParent(null);
        }
    }
    
    public void replaceChild(WhereClause oldClause, WhereClause newClause, UniqueNameContext namingContext) {
        int pos = children.indexOf(oldClause);
        if (pos >= 0) {
            oldClause.setParent(null);
            if (namingContext != null) {
                namingContext.assignUniqueNamesToOutputs(newClause);
            }
            children.set(pos, newClause);
            newClause.setParent(this);
        }
    }

    public Iterator<WhereClause> iterateChildren() {
        return children.iterator();
    }
    
    public boolean isValid() {
        Iterator<WhereClause> iterCh = iterateChildren();
        while (iterCh.hasNext()) {
            WhereClause aChild = iterCh.next();
            if (!aChild.isValid()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isAtomic() {
        return false;
    }
    
    public Iterator iterateAtomicChildren() {
        return new com.pharmadm.util.IteratorFilter(iterateChildren()) {
            public boolean includeElement(Object element) {
                return element instanceof AtomicWhereClause;
            }
        };
    }
    
    protected Collection getOutputVariablesAvailableForImport(WhereClause excludeChild) {
        Collection result;
        if (getParent() == null) {
            result = new ArrayList();
        } else {
            result = getParent().getOutputVariablesAvailableForImport(this);
        }
        Iterator<WhereClause> iterCh = iterateChildren();
        while (iterCh.hasNext()) {
            WhereClause aChild = (WhereClause)iterCh.next();
            if (aChild != excludeChild) {
                result.addAll(aChild.getExportedOutputVariables());
            }
        }
        return result;
    }
    
    public String acceptFromClause(QueryVisitor visitor) throws SQLException { //, MoleculeIndexingException {
        return visitor.visitFromClauseComposedWhereClause(this);
    }
    
    public Collection getAvailableAtomicClauses(AWCPrototypeCatalog catalog) {
        if (!acceptsAdditionalChild()) {
            return new ArrayList(0);
        } else {
            Collection prototypes = catalog.getAWCPrototypes(getOutputVariablesAvailableForImport());
            Collection clones = new ArrayList(prototypes.size());
            Iterator iterPrototypes = prototypes.iterator();
            try {
                while (iterPrototypes.hasNext()) {
                    AtomicWhereClause prototype = (AtomicWhereClause)iterPrototypes.next();
                    clones.add(prototype.clone());
                }
            } catch (CloneNotSupportedException cnse) {
                System.err.println();
                cnse.printStackTrace();
            }
            return clones;
        }
    }
    
    protected void cloneLinks(Map originalToCloneMap) throws CloneNotSupportedException {
        Iterator iter = iterateChildren();
        while (iter.hasNext()) {
            ((WhereClause)iter.next()).cloneLinks(originalToCloneMap);
        }
    }
    
} // end ComposedWhereClause
