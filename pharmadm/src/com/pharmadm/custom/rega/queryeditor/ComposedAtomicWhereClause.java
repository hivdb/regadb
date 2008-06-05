package com.pharmadm.custom.rega.queryeditor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;


import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;

public class ComposedAtomicWhereClause extends AtomicWhereClause {

	public enum VisualisationListPolicy {
		/**
		 * add all elements from the visualization list
		 */
		ALL,
		
		/**
		 * add no elements from the visualization list
		 */
		NONE,
		
		/**
		 * add all constants from the visualization list
		 */
		CONSTANTS,
		
		/**
		 * everything until the first InputVariable
		 */
		INPUT
	}
	
	public enum ExportPolicy {
		/**
		 * export all output variables
		 */
		ALL,
		
		/**
		 * export no output variables
		 */
		NONE,
		
		/**
		 * export only the first output variable
		 */
		FIRST,
		
		/**
		 * export only the last output variable
		 */
		LAST
	}
	
	private AndClause root;
	private ExportPolicy exportPolicy = ExportPolicy.ALL;
	
	public ComposedAtomicWhereClause(ExportPolicy policy) {
		super();
		this.root = new AndClause();
		this.exportPolicy = policy;
		root.setParent(this);
	}
	
	public void addChild(AtomicWhereClause clause, VisualisationListPolicy policy) {
		if (root.getChildCount() > 0) {
			WhereClause c = root.getChildren().get(root.getChildCount()-1);
			if (c.isAtomic() && !((AtomicWhereClause) c).getOutputVariables().isEmpty() && !clause.getInputVariables().isEmpty()) {
				OutputVariable ovar = ((AtomicWhereClause) c).getOutputVariables().iterator().next();
				InputVariable ivar = clause.getInputVariables().iterator().next();
				if (ivar.getObject().isCompatible(ovar.getObject())) {
					ivar.setOutputVariable(ovar);
				}
			}
		}
		else {
			for (String group : clause.getGroups()) {
				addGroup(group);
			}
		}
		root.addChild(clause, null, null);
		
		if (policy == VisualisationListPolicy.ALL) {
	    	for (ConfigurableWord word : clause.getVisualizationClauseList().getWords()) {
	    		getVisualizationClauseList().addWord((AWCWord) word);
	    	}
		}
		else if (policy == VisualisationListPolicy.CONSTANTS) {
	    	for (Constant cst : clause.getConstants()) {
    			getVisualizationClauseList().addConstant(cst);
	    	}
		}
		else if (policy == VisualisationListPolicy.INPUT) {
			boolean input = false;
			int i = 0;
			while (input == false && i < clause.getVisualizationClauseList().getWords().size()) {
				AWCWord word = (AWCWord) clause.getVisualizationClauseList().getWords().get(i);
				input = word instanceof InputVariable;
				getVisualizationClauseList().addWord(word);
				i++;
			}
		}
	}
	
	@Override
	protected void addConstant(Constant cst) {}

	@Override
	protected void addFromVariable(FromVariable cst) {}

	@Override
	protected void addInputVariable(InputVariable cst) {}

	@Override
	protected void addOutputVariable(OutputVariable cst) {}

	@Override
	public List<Constant> getConstants() {
		List<Constant> constants = new ArrayList<Constant>();
		for (WhereClause clause : root.getChildren()) {
			if (clause.isAtomic()) {
				constants.addAll(((AtomicWhereClause) clause).getConstants());
			}
		}
		return constants;
	}

	@Override
	public List<FromVariable> getFromVariables() {
		List<FromVariable> fromVars = new ArrayList<FromVariable>();
		for (WhereClause clause : root.getChildren()) {
			if (clause.isAtomic()) {
				fromVars.addAll(((AtomicWhereClause) clause).getFromVariables());
			}
		}
		return fromVars;
	}

	@Override
	public List<InputVariable> getInputVariables() {
		List<InputVariable> inputVars = new ArrayList<InputVariable>();
		for (ConfigurableWord word : getVisualizationClauseList().getWords()) {
			if (word instanceof InputVariable) {
				inputVars.add((InputVariable) word);
			}
		}
		return inputVars;
	}

	@Override
	public List<OutputVariable> getOutputVariables() {
		List<OutputVariable> outputVars = new ArrayList<OutputVariable>();
		for (WhereClause clause : root.getChildren()) {
			if (clause.isAtomic()) {
				outputVars.addAll(((AtomicWhereClause) clause).getOutputVariables());
			}
		}
		return outputVars;
	}

	@Override
	public String acceptFromClause(QueryVisitor visitor) throws SQLException {
		return root.acceptFromClause(visitor);
	}

	@Override
	public String acceptWhereClause(QueryVisitor visitor) throws SQLException {
		return root.acceptWhereClause(visitor);
	}

	@Override
	protected Object cloneBasics(
			Map<ConfigurableWord, ConfigurableWord> originalToCloneMap)
			throws CloneNotSupportedException {
		ComposedAtomicWhereClause clone = new ComposedAtomicWhereClause(exportPolicy);
		clone.root = (AndClause) root.cloneBasics(originalToCloneMap);
		clone.root.setParent(clone);
		
        WhereClauseComposer hibClauseCompClone = (WhereClauseComposer)getWhereClauseComposer().cloneInContext(originalToCloneMap, clone);
        clone.setWhereClauseComposer(hibClauseCompClone);
        
        VisualizationClauseList visClauseListClone = (VisualizationClauseList)getVisualizationClauseList().cloneInContext(originalToCloneMap, clone);
        clone.setVisualizationClauseList(visClauseListClone);
        
        for (String group : getGroups()) {
        	clone.addGroup(group);
        }
        clone.setCompositionBehaviour(getCompositionBehaviour());
		
		return clone;
	}

	@Override
	protected void cloneLinks(
			Map<ConfigurableWord, ConfigurableWord> originalToCloneMap)
			throws CloneNotSupportedException {
		root.cloneLinks(originalToCloneMap);
	}

	@Override
	protected List<OutputVariable> getExportedOutputVariables() {
		if (exportPolicy == ExportPolicy.NONE) {
			return Collections.emptyList();
		}
		List<OutputVariable> result =  root.getExportedOutputVariables();
		if (exportPolicy == ExportPolicy.ALL || result.isEmpty()) {
			return result;
		}
		else if (exportPolicy == ExportPolicy.FIRST) {
			return result.subList(0, 1);
		}
		else if (exportPolicy == ExportPolicy.LAST) {
			return result.subList(result.size()-1, result.size());
		}
		
		return result;
	}

	@Override
	public boolean isValid() {
		return root.isValid();
	}
	
    public Collection<OutputVariable> getOutputVariablesAvailableForImport(WhereClause excludeChild) {
    	if (excludeChild == root) {
    		excludeChild = this;
    	}
        return getParent().getOutputVariablesAvailableForImport(excludeChild);
    }	

}
