
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
import java.sql.SQLException;

import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;

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
public class AtomicWhereClause extends WhereClause implements WordListOwner {
    
    public AtomicWhereClause() {
    }
    
    /** For xml-encoding purposes */
    public AtomicWhereClause(Collection<InputVariable> inputVariables, Collection<OutputVariable> outputVariables, Collection<FromVariable> fromVariables, Collection<Constant> constants, VisualizationClauseList visualizationClauseList, WhereClauseComposer whereClauseComposer) {
        this.inputVariables = inputVariables;
        this.outputVariables = outputVariables;
        this.fromVariables = fromVariables;
        this.constants = constants;
        this.visualizationClauseList = visualizationClauseList;
        this.whereClauseComposer = whereClauseComposer;
    }
    
    ///////////////////////////////////////
    // associations
    
    /**
     * <p>
     *
     * </p>
     */
    private Collection<InputVariable> inputVariables = new HashSet<InputVariable>(); // of type InputVariable
    
    /**
     * <p>
     *
     * </p>
     */
    private Collection<Constant> constants = new HashSet<Constant>(); // of type Constant
    
    /**
     * <p>
     *
     * </p>
     */
    private Collection<OutputVariable> outputVariables = new HashSet<OutputVariable>(); // of type OutputVariable
    
    /**
     * <p>
     *
     * </p>
     */
    private Collection<FromVariable> fromVariables = new HashSet<FromVariable>(); // of type FromVariable
    
    /**
     * <p>
     *
     * </p>
     */
    private WhereClauseComposer whereClauseComposer = new WhereClauseComposer(this);
    
    /**
     * <p>
     *
     * </p>
     */
    private VisualizationClauseList visualizationClauseList = new VisualizationClauseList(this);
    
    private static final Iterator<WhereClause> EMPTY_ITERATOR = Collections.EMPTY_LIST.iterator();
    
    ///////////////////////////////////////
    // access methods for associations
    
    public Collection<InputVariable> getInputVariables() {
        return inputVariables;
    }
    protected void addInputVariable(InputVariable inputVariable) {
        this.inputVariables.add(inputVariable);
    }
    
    public Collection<Constant> getConstants() {
        return constants;
    }
    protected void addConstant(Constant constant) {
        this.constants.add(constant);
    }
    
    public Collection<OutputVariable> getOutputVariables() {
        return outputVariables;
    }
    protected void addOutputVariable(OutputVariable outputVariable) {
        this.outputVariables.add(outputVariable);
    }
    
    public Collection<FromVariable> getFromVariables() {
        return fromVariables;
    }
    protected void addFromVariable(FromVariable fromVariable) {
        this.fromVariables.add(fromVariable);
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
    
    /**
     * <p>
     * Unlinks all InputVariables and clears all Constant values.
     * </p>
     *
     */
    public void reset() {
        Iterator<InputVariable> iterInputVars = getInputVariables().iterator();
        while (iterInputVars.hasNext()) {
            InputVariable ivar = (InputVariable)iterInputVars.next();
            ivar.setOutputVariable(null);
        }
        Iterator<Constant> iterConsts = getConstants().iterator();
        while (iterConsts.hasNext()) {
            Constant c = (Constant)iterConsts.next();
            c.setValue(null);
        }
    } // end reset
    
    
    public String acceptFromClause(QueryVisitor visitor) throws SQLException { //, MoleculeIndexingException {
    	return visitor.visitFromClauseAtomicWhereClause(this);
    }
    
    public String acceptWhereClause(QueryVisitor visitor) throws SQLException { //, MoleculeIndexingException {
        return getWhereClauseComposer().composeWhereClause(visitor);
    }
    
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
    protected Collection<OutputVariable> getExportedOutputVariables() {
        return getOutputVariables();
    }
    
    /**
     * Create a deep clone of this AtomicWhereClause (step 1).
     */
    protected Object cloneBasics(Map originalToCloneMap) throws CloneNotSupportedException {
        AtomicWhereClause clone = new AtomicWhereClause();
        Iterator<FromVariable> iterFromVariables = getFromVariables().iterator();
        while (iterFromVariables.hasNext()) {
            FromVariable fromVar = (FromVariable)iterFromVariables.next();
            FromVariable fromVarClone = (FromVariable)fromVar.clone();
            originalToCloneMap.put(fromVar, fromVarClone);
            clone.fromVariables.add(fromVarClone);
        }
        
        Iterator<Constant> iterConstants = getConstants().iterator();
        while (iterConstants.hasNext()) {
            Constant constant = (Constant)iterConstants.next();
            Constant constantClone = (Constant)constant.clone();
            originalToCloneMap.put(constant, constantClone);
            clone.constants.add(constantClone);
        }
        
        // leave inputVariables assigned to original outputvariables for now
        // (set them later to correct outputvars when the outputvar's clones are ready.)
        Iterator<InputVariable> iterInputVariables = getInputVariables().iterator();
        while (iterInputVariables.hasNext()) {
            InputVariable inputVar = (InputVariable)iterInputVariables.next();
            InputVariable inputVarClone = (InputVariable)inputVar.clone();
            originalToCloneMap.put(inputVar, inputVarClone);
            clone.inputVariables.add(inputVarClone);
        }
        
        Iterator<OutputVariable> iterOutputVariables = getOutputVariables().iterator();
        while (iterOutputVariables.hasNext()) {
            OutputVariable outputVar = (OutputVariable)iterOutputVariables.next();
            OutputVariable outputVarClone = (OutputVariable)outputVar.cloneInContext(originalToCloneMap);
            originalToCloneMap.put(outputVar, outputVarClone);
            clone.outputVariables.add(outputVarClone);
        }
        WhereClauseComposer hibClauseCompClone = (WhereClauseComposer)getWhereClauseComposer().cloneInContext(originalToCloneMap, clone);
        clone.setWhereClauseComposer(hibClauseCompClone);
        
        VisualizationClauseList visClauseListClone = (VisualizationClauseList)getVisualizationClauseList().cloneInContext(originalToCloneMap, clone);
        clone.setVisualizationClauseList(visClauseListClone);
        
        return clone;
    }
    
    /**
     * Create a deep clone of this AtomicWhereClause (step 2).
     */
    protected void cloneLinks(Map originalToCloneMap) throws CloneNotSupportedException {
        // now let inputclones point to correct outputclones
        Iterator<InputVariable> iterInputVariables = getInputVariables().iterator();
        while (iterInputVariables.hasNext()) {
            InputVariable inputVar = iterInputVariables.next();
            OutputVariable outputVar = inputVar.getOutputVariable();
            OutputVariable outputVarClone = (OutputVariable)originalToCloneMap.get(outputVar);
            
            // If no outputVarClone is in the map, it means the outputVariable was external to
            // the current clone operation. What we should do then is debatable. Here we choose
            // to retain the link to the existing output variable. Alternatively, we could
            // clear the link. Observe that a link to a clone of the output variable would not
            // make any sense.
            // A nice consequence of this choice is that cut-paste of the same clause does not
            // break any input links of the clause. It does, however, break output links.
            // This can not be avoided in general as output variables have to be unique.
            inputVar.setOutputVariable(outputVarClone == null ? outputVar : outputVarClone);
        }
    }
    
    public Collection<AtomicWhereClause> getAvailableAtomicClauses(AWCPrototypeCatalog catalog) {
        return new ArrayList<AtomicWhereClause>(0);
    }
    
    public boolean isValid() {
        boolean valid = true;
        Iterator<InputVariable> iterInputVars = getInputVariables().iterator();
        while (iterInputVars.hasNext()) {
            InputVariable ivar = (InputVariable)iterInputVars.next();
            valid &=  ((ivar.getOutputVariable() != null) && getOutputVariablesAvailableForImport().contains(ivar.getOutputVariable()));
        }
        Iterator<Constant> iterConsts = getConstants().iterator();
        while (iterConsts.hasNext()) {
            Constant c = (Constant)iterConsts.next();
            valid &= (c.getValue() != null);
        }
        return valid;
    }
    
    public boolean isAtomic() {
        return true;
    }
    
    public int getChildCount() {
        return 0;
    }
    
    // trivial implementation of the composed WhereClause methods
    public void addChild(WhereClause child, UniqueNameContext namingContext) throws IllegalWhereClauseCompositionException {
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
    
    
} // end AtomicWhereClause


