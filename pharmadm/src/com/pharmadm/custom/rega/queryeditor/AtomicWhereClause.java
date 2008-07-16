
/** Java class "AtomicWhereClause.java" generated from Poseidon for UML.
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

import com.pharmadm.custom.rega.queryeditor.UniqueNameContext.AssignMode;
import com.pharmadm.custom.rega.queryeditor.catalog.AWCPrototypeCatalog;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;

/**
 * <p>
 * An AtomicWhereClause does not contain child WhereClauses. It determines
 * a set of Constants, InputVariables, OutputVariables and FromVariables.
 * It is configurable: its Constants' values can be set and each of its
 * InputVariables can be associated with an available compatible
 * OutputVariable. Every FromVariable defines a relevant table in the
 * database.
 * </p>
 *
 * <p>
 * This class supports xml-encoding.
 * The following new properties are encoded :
 *  inputVariables (via constructor, using AWCPersistenceDelegate)
 *  outputVariables (via constructor, using AWCPersistenceDelegate)
 *  fromVariables (via constructor, using AWCPersistenceDelegate)
 *  constants (via constructor, using AWCPersistenceDelegate)
 *  visualizationClauseList (via constructor, using AWCPersistenceDelegate)
 *  whereClauseComposer (via constructor, using AWCPersistenceDelegate)
 * </p>
 */
public abstract class AtomicWhereClause extends WhereClause implements WordListOwner, Serializable {
    
    private WhereClauseComposer whereClauseComposer = new WhereClauseComposer(this);
    private VisualizationClauseList visualizationClauseList = new VisualizationClauseList(this);
    
    private static final Iterator<WhereClause> EMPTY_ITERATOR = Collections.EMPTY_LIST.iterator();
    
    private Collection<String> groups = new HashSet<String>();
    private CompositionBehaviour compositionBehaviour = new NullComposition();	
	
    
    ///////////////////////////////////////
    // access methods for associations
    
    public Collection<String> getGroups() {
    	return groups;
    }
    
    public void addGroup(String group) {
    	groups.add(group);
    }
   
    public WhereClauseComposer getWhereClauseComposer() {
        return whereClauseComposer;
    }
    
    protected void setWhereClauseComposer(WhereClauseComposer whereClauseComposer) {
        this.whereClauseComposer = whereClauseComposer;
    }
    
    public VisualizationClauseList getVisualizationClauseList() {
        return visualizationClauseList;
    }
    
    protected void setVisualizationClauseList(VisualizationClauseList visualizationClauseList) {
        this.visualizationClauseList = visualizationClauseList;
    }
    
    ///////////////////////////////////////
    // operations
    
    public Collection<OutputVariable> getOutputVariablesAvailableForImport() {
        if (getParent() != null) {
            return getParent().getOutputVariablesAvailableForImport();
        } else {
            return new HashSet<OutputVariable>();
        }
    }
    public Collection<OutputVariable> getOutputVariablesAvailableForImportInContext(WhereClause context) {
        return context.getOutputVariablesAvailableForImport();
    }
    
    protected Collection<OutputVariable> getOutputVariablesAvailableForImport(WhereClause excludeChild) {
        return getOutputVariablesAvailableForImport();
    }
    
    public Collection<AtomicWhereClause> getAvailableAtomicClauses(AWCPrototypeCatalog catalog) {
        return new ArrayList<AtomicWhereClause>(0);
    }
    
    public boolean isAtomic() {
        return true;
    }
    
    public int getChildCount() {
        return 0;
    }
    
    // trivial implementation of the composed WhereClause methods
    public void addChild(WhereClause child, UniqueNameContext namingContext, AssignMode mode) throws IllegalWhereClauseCompositionException {
        throw new IllegalWhereClauseCompositionException();
    }
    
    public void removeChild(WhereClause child) {
        // doesn't have children
    }
    
    public void replaceChild(WhereClause oldChild, WhereClause newChild, UniqueNameContext namingContext) throws IllegalWhereClauseCompositionException {
        // doesn't have children
        throw new IllegalWhereClauseCompositionException();
    }
    
    public boolean acceptsAdditionalChild() {
        return false;
    }
    
    public Iterator<WhereClause> iterateChildren() {
        return EMPTY_ITERATOR;
    }
    
    public Iterator<WhereClause> iterateAtomicChildren() {
        return EMPTY_ITERATOR;
    }
    
    public String toString() {
        return getVisualizationClauseList().getHumanStringValue();
    }

	public void setCompositionBehaviour(CompositionBehaviour compositionBehaviour) {
		this.compositionBehaviour = compositionBehaviour;
	}

	public CompositionBehaviour getCompositionBehaviour() {
		return compositionBehaviour;
	}
	
	public abstract List<OutputVariable> getOutputVariables();
	public abstract List<InputVariable> getInputVariables();
	public abstract List<Constant> getConstants();
	public abstract List<FromVariable> getFromVariables();
	public abstract List<Join> getRelations();
	

	public abstract void addRelation(Join join);
	protected abstract void addConstant(Constant cst);	
	protected abstract void addInputVariable(InputVariable cst);	
	protected abstract void addOutputVariable(OutputVariable cst);	
	protected abstract void addFromVariable(FromVariable cst);	
	
	public String getHash(){
		String hash = "";
		for (ConfigurableWord word : getVisualizationClauseList().getWords()) {
			hash += word.getImmutableStringValue() + " ";
		}
		return hash;
	}	
	
} // end AtomicWhereClause


