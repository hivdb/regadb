/*
 * DataGroupEditor.java
 *
 * Created on November 25, 2003, 5:26 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor.gui;

import java.util.*;

import com.pharmadm.custom.rega.gui.configurers.JVisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConfigurationController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConstantController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.VisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.reporteditor.DataGroup;
import com.pharmadm.custom.rega.reporteditor.DataInputVariable;
import com.pharmadm.custom.rega.reporteditor.DataInputVariableController;
import com.pharmadm.custom.rega.reporteditor.DataOutputVariable;

/**
 *
 * @author  kristof
 */
public class DataGroupEditor implements ConfigurationController, ConstantController, DataInputVariableController {
    
    /** Creates a new instance of DataGroupEditor */
    public DataGroupEditor() {
    }
    
    
/**
 * <p>
 * 
 * </p>
 */
    private DataGroup dataGroup; 

    private DataGroup contextGroup;
    
/**
 * <p>
 * 
 * </p>
 */
    private VisualizationComponentFactory visualizationComponentFactory; 


   ///////////////////////////////////////
   // access methods for associations

    public DataGroup getDataGroup() {
        return dataGroup;
    }
    public void setDataGroup(DataGroup dataGroup) {
        this.dataGroup = dataGroup;
    }
    
    public DataGroup getContextGroup() {
        return contextGroup;
    }
    public void setContextGroup(DataGroup contextGroup) {
        this.contextGroup = contextGroup;
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
        if (contextGroup == null) {
            return getDataGroup().getOutputVariablesAvailableForImport();
        }
        else {
            return getDataGroup().getOutputVariablesAvailableForImportInContext(contextGroup); 
        }        
    }


/**
 * <p>
 * Gets all compatible DataOutputVariables for a given DataInputVariable as
 * available for the edited DataGroup in the current ReportFormat.
 * </p>
 * <p>
 * 
 * @return a Collection all available compatible DataOutputVariables for a
 * given DataInputVariable
 * </p>
 * <p>
 * @param input the DataInputVariable that the DataOutPutVariables should be
 * compatible with
 * </p>
 */
    public Collection getCompatibleOutputVariables(DataInputVariable input) {        
        Collection compatibles = new ArrayList();
        Iterator iter = getAvailableOutputVariables().iterator();
        while (iter.hasNext()) {
            DataOutputVariable ov = (DataOutputVariable)iter.next();
            if (input.isCompatible(ov)) {
                compatibles.add(ov);
            }    
        }
        return compatibles;
    } // end getCompatibleOutputVariables        

/**
 * <p>
 * Associates the given DataInputVariable to the given DataOutputvariable. The
 * Variables must be of compatible types. If the DataInputVariable was
 * associated with another DataOutputVariable, then the old association will be
 * broken and replaced by the new. DataOutputVariables however can be
 * associated with an unlimited number of DataInputVariables.
 * </p>
 * <p>
 * 
 * @param input the DataInputVariable to associate the DataOutputVariable with
 * </p>
 * <p>
 * @param output the DataOutputVariable to associate the DataInputVariable with
 * </p>
 */
    public void assignOutputVariable(DataInputVariable input, DataOutputVariable output) {        
        input.setOutputVariable(output);
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
        return cst.getHumanStringValue().toString();
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
       	return cst.setValue(value);
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
        return getDataGroup().isValid();
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
    public List<ConfigurableWord> getVisualizationList() {        
        return getDataGroup().getVisualizationList().getWords();
    } // end getVisualizationList        

}
