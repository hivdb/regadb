
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
package com.pharmadm.custom.rega.queryeditor;

import java.util.*;
import java.util.Collection;
import java.util.List;

import com.pharmadm.custom.rega.savable.*;
import com.pharmadm.custom.rega.queryeditor.gui.VisualizationComponentFactory;

/**
 * The controller ('Controller' pattern) for editing an AtomicWhereClause.
 *
 */
public class AtomicWhereClauseEditor implements ConfigurationController, ConstantController, InputVariableController {
    
    ///////////////////////////////////////
    // associations
    
    private AtomicWhereClause atomicWhereClause;
    private WhereClause contextClause = null;
    private VisualizationComponentFactory visualizationComponentFactory;
    private QueryEditor queryEditor;
    
    public AtomicWhereClauseEditor(QueryEditor queryEditor) {
        this.queryEditor= queryEditor;
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public AtomicWhereClause getAtomicWhereClause() {
        return atomicWhereClause;
    }
    public void setAtomicWhereClause(AtomicWhereClause atomicWhereClause) {
        this.atomicWhereClause = atomicWhereClause;
    }
    public WhereClause getContextClause() {
        return contextClause;
    }
    public void setContextClause(WhereClause contextClause) {
        this.contextClause = contextClause;
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
    public Collection getAvailableOutputVariables() {
        if (contextClause == null) {
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
    public Collection getCompatibleOutputVariables(InputVariable input) {
        Collection compatibles = new ArrayList();
        Iterator iter = getAvailableOutputVariables().iterator();
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
    public boolean setConstantValueString(Constant cst, String value) {
        try {
            Object o = cst.parseValue(value);
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
    public List getVisualizationList() {
        return getAtomicWhereClause().getVisualizationClauseList().getWords();
    } // end getVisualizationList
    
    private void notifyQueryEditorDirty() {
        if (queryEditor != null) {
            queryEditor.setDirty(true);
        }
    }
}
