
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
import java.io.Serializable;
import java.sql.SQLException;

import com.pharmadm.custom.rega.queryeditor.UniqueNameContext.AssignMode;
import com.pharmadm.custom.rega.queryeditor.catalog.AWCPrototypeCatalog;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;

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
public abstract class ComposedWhereClause extends WhereClause implements Serializable {
    
    /** For xml-encoding purposes only */
    public ComposedWhereClause() {
    }
    
    public ComposedWhereClause(ArrayList<WhereClause> children) {
    	setChildren(children);
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
    
    public void addChild(WhereClause whereClause, UniqueNameContext namingContext, AssignMode mode) {
        if (! children.contains(whereClause)) {
            if (namingContext != null) {
            	namingContext.assignUniqueNames(whereClause, mode);
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
    	if (!oldClause.equals(newClause)) {
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
    
    protected Collection<OutputVariable> getOutputVariablesAvailableForImport(WhereClause excludeChild) {
        Collection<OutputVariable> result;
        if (getParent() == null) {
            result = new ArrayList<OutputVariable>();
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
    
    public Collection<AtomicWhereClause> getAvailableAtomicClauses(AWCPrototypeCatalog catalog) {
        if (!acceptsAdditionalChild()) {
            return new ArrayList<AtomicWhereClause>(0);
        } else {
            Collection<AtomicWhereClause> prototypes = catalog.getAWCPrototypes(getOutputVariablesAvailableForImport());
            Collection<AtomicWhereClause> clones = new ArrayList<AtomicWhereClause>(prototypes.size());
            Iterator<AtomicWhereClause> iterPrototypes = prototypes.iterator();
            try {
                while (iterPrototypes.hasNext()) {
                    AtomicWhereClause prototype = (AtomicWhereClause)iterPrototypes.next();
                    clones.add( (AtomicWhereClause) prototype.clone());
                }
            } catch (CloneNotSupportedException cnse) {
                System.err.println();
                cnse.printStackTrace();
            }
            return clones;
        }
    }
    
    protected void cloneLinks(Map<ConfigurableWord, ConfigurableWord> originalToCloneMap) throws CloneNotSupportedException {
        Iterator iter = iterateChildren();
        while (iter.hasNext()) {
            ((WhereClause)iter.next()).cloneLinks(originalToCloneMap);
        }
    }
    
} // end ComposedWhereClause
