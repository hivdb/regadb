package com.pharmadm.custom.rega.queryeditor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;

public class SimpleAtomicWhereClause extends AtomicWhereClause {

    private List<InputVariable> inputVariables = new ArrayList<InputVariable>(); // of type InputVariable
    private List<Constant> constants = new ArrayList<Constant>(); // of type Constant
    private List<OutputVariable> outputVariables = new ArrayList<OutputVariable>(); // of type OutputVariable
    private List<FromVariable> fromVariables = new ArrayList<FromVariable>(); // of type FromVariable
    private List<Join> relations = new ArrayList<Join>();

    
    public List<InputVariable> getInputVariables() {
        return inputVariables;
    }
    protected void addInputVariable(InputVariable inputVariable) {
    	if (!this.inputVariables.contains(inputVariable)) {
    		this.inputVariables.add(inputVariable);
    	}
    }
    
    public List<Constant> getConstants() {
        return constants;
    }
    protected void addConstant(Constant constant) {
    	if (!this.constants.contains(constant)) {
    		this.constants.add(constant);
    	}
    }
    
    public List<OutputVariable> getOutputVariables() {
        return outputVariables;
    }
    protected void addOutputVariable(OutputVariable outputVariable) {
    	if (!this.outputVariables.contains(outputVariable)) {
    		this.outputVariables.add(outputVariable);
    	}
    }
    
    public List<FromVariable> getFromVariables() {
        return fromVariables;
    }
    
    public void addFromVariable(FromVariable fromVariable) {
    	if (!this.fromVariables.contains(fromVariable)) {
    		this.fromVariables.add(fromVariable);
    	}
    } 
    
    public List<OutputVariable> getExportedOutputVariables() {
        return getOutputVariables();
    }    

    
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
            c.reset();
        }
    } // end reset
    
    /**
     * Create a deep clone of this AtomicWhereClause (step 1).
     */
    protected Object cloneBasics(Map<ConfigurableWord, ConfigurableWord> originalToCloneMap) throws CloneNotSupportedException {
        SimpleAtomicWhereClause clone = new SimpleAtomicWhereClause();
        Iterator<FromVariable> iterFromVariables = getFromVariables().iterator();
        while (iterFromVariables.hasNext()) {
            FromVariable fromVar = (FromVariable)iterFromVariables.next();
            FromVariable fromVarClone = (FromVariable)fromVar.clone();
            originalToCloneMap.put(fromVar, fromVarClone);
            clone.fromVariables.add(fromVarClone);
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
        
        Iterator<Constant> iterConstants = getConstants().iterator();
        while (iterConstants.hasNext()) {
            Constant constant = (Constant)iterConstants.next();
            Constant constantClone = (Constant) constant.cloneInContext(originalToCloneMap);
            originalToCloneMap.put(constant, constantClone);
            clone.constants.add(constantClone);
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
        
        for (String group : getGroups()) {
        	clone.addGroup(group);
        }
        clone.setCompositionBehaviour(getCompositionBehaviour());

        for (Join join : relations) {
        	clone.addRelation(join.cloneInContext(originalToCloneMap));
        }
        
        
        return clone;
    }
    
    /**
     * Create a deep clone of this AtomicWhereClause (step 2).
     */
    protected void cloneLinks(Map<ConfigurableWord, ConfigurableWord> originalToCloneMap) throws CloneNotSupportedException {
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
        
//        Iterator<OutputVariable> iterOutputVariables = getOutputVariables().iterator();
//        while (iterOutputVariables.hasNext()) {
//            OutputVariable outputVar = iterOutputVariables.next();
//            OutputVariable outputVarClone = (OutputVariable)originalToCloneMap.get(outputVar);
//            outputVarClone.getExpression().cloneInContext(originalToCloneMap, outputVarClone.getExpression().getOwner());
//            
//            // If no outputVarClone is in the map, it means the outputVariable was external to
//            // the current clone operation. What we should do then is debatable. Here we choose
//            // to retain the link to the existing output variable. Alternatively, we could
//            // clear the link. Observe that a link to a clone of the output variable would not
//            // make any sense.
//            // A nice consequence of this choice is that cut-paste of the same clause does not
//            // break any input links of the clause. It does, however, break output links.
//            // This can not be avoided in general as output variables have to be unique.
//            inputVar.setOutputVariable(outputVarClone == null ? outputVar : outputVarClone);
//        }
        
    }
    
    public String acceptFromClause(QueryVisitor visitor) throws SQLException { 
    	return visitor.visitFromClauseAtomicWhereClause(this);
    }
    
    public String acceptWhereClause(QueryVisitor visitor) throws SQLException { 
        return getWhereClauseComposer().composeWhereClause(visitor);
    }
    
    
    public boolean isValid() {
        boolean valid = true;
        Iterator<InputVariable> iterInputVars = getInputVariables().iterator();
        while (iterInputVars.hasNext()) {
            InputVariable ivar = (InputVariable)iterInputVars.next();
            boolean validIvar = ((ivar.getOutputVariable() != null) && getOutputVariablesAvailableForImport().contains(ivar.getOutputVariable()));
            valid &= validIvar;
            ivar.setvalid(validIvar);
        }
        Iterator<Constant> iterConsts = getConstants().iterator();
        while (iterConsts.hasNext()) {
            Constant c = (Constant)iterConsts.next();
            valid &= (c.getValue() != null);
        }
        return valid;
    }
	@Override
	public void addRelation(Join join) {
		relations.add(join);
	}
	@Override
	public List<Join> getRelations() {
		// XStream backwards compatibilty for
		// AWC's from before relations
		if (relations == null) {
			relations = new ArrayList<Join>();
		}
		return relations;
	}    
}
