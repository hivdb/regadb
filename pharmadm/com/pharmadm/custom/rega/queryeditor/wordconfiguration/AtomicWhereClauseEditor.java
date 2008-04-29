
/** Java class "AtomicWhereClauseEditor.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.*;

import com.pharmadm.custom.rega.queryeditor.AWCWord;
import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.QueryEditor;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;

/**
 * The controller ('Controller' pattern) for editing an AtomicWhereClause.
 *
 */
public class AtomicWhereClauseEditor implements ConfigurationController, ConstantController, InputVariableController {
    
    ///////////////////////////////////////
    // associations
    
    private AtomicWhereClause atomicWhereClause;
    private QueryContext context = null;
    private VisualizationComponentFactory visualizationComponentFactory;
    
    public AtomicWhereClauseEditor(QueryContext context, AtomicWhereClause clause) {
    	visualizationComponentFactory = new VisualizationComponentFactory(this);
    	this.context = context;
    	atomicWhereClause = clause;
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public QueryEditor getQueryEditor() {
    	return context.getEditorModel();
    }
    
    public AtomicWhereClause getAtomicWhereClause() {
        return atomicWhereClause;
    }
    public void setAtomicWhereClause(AtomicWhereClause atomicWhereClause) {
        this.atomicWhereClause = atomicWhereClause;
    }
    public WhereClause getContextClause() {
        return context.getContextClause();
    }
    public VisualizationComponentFactory getVisualizationComponentFactory() {
        return visualizationComponentFactory;
    }
    public void setVisualizationComponentFactory(VisualizationComponentFactory visualizationComponentFactory) {
        this.visualizationComponentFactory = visualizationComponentFactory;
    }
    
    
    ///////////////////////////////////////
    // operations
    
    
    /**
     * <p>
     * Gets all OutputVariables available for the edited AtomicWhereClause
     * in the given context.
     * </p>
     * <p>
     *
     * @return a Collection all available OutputVariables
     * </p>
     */
    public Collection<OutputVariable> getAvailableOutputVariables() {
    	WhereClause contextClause = getContextClause();
        if (getContextClause() == null) {
            return getAtomicWhereClause().getOutputVariablesAvailableForImport();
        }
        else {
            return getAtomicWhereClause().getOutputVariablesAvailableForImportInContext(contextClause);
        }
    } // end getAvailableOutputVariables
    
    
    /**
     * <p>
     * Gets all compatible OutputVariables for a given InputVariable as
     * available for the edited AtomicWhereClause in the current Query.
     * </p>
     * <p>
     *
     * @return a Collection all available compatible OutputVariables for a
     * given InputVariable
     * </p>
     * <p>
     * @param input the InputVariable that the OutPutVariables should be
     * compatible with
     * </p>
     */
    public Collection<OutputVariable> getCompatibleOutputVariables(InputVariable input) {
        Collection<OutputVariable> compatibles = new ArrayList<OutputVariable>();
        Iterator<OutputVariable> iter = getAvailableOutputVariables().iterator();
        while (iter.hasNext()) {
            OutputVariable ov = (OutputVariable)iter.next();
            if (input.isCompatible(ov)) {
                compatibles.add(ov);
            }
        }
        return compatibles;
    } // end getCompatibleOutputVariables
    
    /**
     * <p>
     * Associates the given InputVariable to the given Outputvariable. The
     * Variables must be of compatible types. If the InputVariable was
     * associated with another OutputVariable, then the old association will be
     * broken and replaced by the new. OutputVariables however can be
     * associated with an unlimited number of InputVariables.
     * </p>
     * <p>
     *
     * @param input the InputVariable to associate the OutputVariable with
     * </p>
     * <p>
     * @param output the OutputVariable to associate the InputVariable with
     * </p>
     */
    public void assignOutputVariable(InputVariable input, OutputVariable output) {
        input.setOutputVariable(output);
        notifyQueryEditorDirty();
    } // end assignOutputVariable
    
    /**
     * <p>
     * Gets a String representing and uniquely defining the current value of
     * the constant.
     * </p>
     * <p>
     *
     * @param cst the Constant of which the value is requested
     * </p>
     * <p>
     * @return a String that describes the value of the Constant
     * </p>
     */
    public String getConstantValueString(Constant cst) {
        return cst.getHumanStringValue();
    } // end getConstantValueString
    
    /**
     * <p>
     * Sets the value of a Constant using a String.
     * </p>
     * <p>
     * The String must conform to the Constant's formatter, otherwise no change
     * is made and false is returned. However, conformation to the formatter
     * does not guarantee succes.
     * </p>
     * <p>
     *
     * @param cst the constant of which the value must be set
     * </p>
     * <p>
     * @param value a String that can be parsed to yield the intended value
     * </p>
     * <p>
     * @return True iff setting the value was successful. If false is returned,
     * then no change has been made.
     * </p>
     */
    public boolean setConstantValueString(Constant cst, Object value) {
        try {
            cst.parseValue(value);
            notifyQueryEditorDirty();
            return true;
        } catch (java.text.ParseException pe) {
            return false;
        }
    } // end setConstantValueString
    
    /**
     * <p>
     * Calculates whether the WhereClause being edited is valid, i.e. wether
     * all constants are set and all input variables are bound, for the
     * WhereClause being edited and for all of its descendants.
     * </p>
     * <p>
     *
     * @return whether the WhereClause being edited and all of its descendants
     * are valid.
     * </p>
     */
    public boolean isValid() {
        return atomicWhereClause.isValid();
    } // end isValid
    
    /**
     * <p>
     * Gets the visualization list of the AtomicClauseEditor being edited. The
     * list consists of ACWWords.
     * </p>
     * <p>
     *
     * @return a List with all AWCWords needed for visualization
     * </p>
     */
    public List<AWCWord> getVisualizationList() {
    	
    	List<AWCWord> words = new ArrayList<AWCWord>();
    	List<ConfigurableWord> confWords = getAtomicWhereClause().getVisualizationClauseList().getWords();
    	for (ConfigurableWord word : confWords) {
    		words.add((AWCWord) word);
    	}
        return words;
    } // end getVisualizationList
    
    private void notifyQueryEditorDirty() {
    	QueryEditor editor = getQueryEditor();
        if (editor != null) {
            editor.setDirty(true);
        }
    }
}
