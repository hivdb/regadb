/*
 * DataGroup.java
 *
 * Created on November 18, 2003, 6:38 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import java.util.*;
import com.pharmadm.custom.rega.queryeditor.*;

/**
 *
 * @author  kristof
 */
/**
 * This class supports xml-encoding. The following properties are encoded.
 *  reportFormat
 *  dataInputVariables (via constructor, using DataGroupPersistenceDelegate)
 *  dataOutputVariables (via constructor, using DataGroupPersistenceDelegate)
 *  constants (via constructor, using DataGroupPersistenceDelegate)
 *  objectListVariables (via constructor, using DataGroupPersistenceDelegate)
 *  visualizationList (via constructor, using DataGroupPersistenceDelegate)
 */
public class DataGroup implements com.pharmadm.custom.rega.queryeditor.WordListOwner, Cloneable {
    
    // observe that a DataGroup is quite similar to an AtomicWhereClause. 
    // Main differences are that DataGroups are organised in Lists, not Trees, 
    // and that they have no independent "result" (only the outputvariables
    // are important).
    // They also use slightly different instances of ConfigurableWords
    
    public static DataGroup dummyGroup(ReportFormat format) {
        DataGroup dummyGroup = new DataGroup();
        dummyGroup.setReportFormat(format);
        dummyGroup.setDummy(true);
        dummyGroup.getVisualizationList().addFixedString(new FixedString("--"));
        return dummyGroup;
    }
    
    private ReportFormat reportFormat;
    
    private Collection dataInputVariables = new HashSet(); // of type DataInputVariable
    
    /**
     * <p>
     *
     * </p>
     */
    private Collection constants = new HashSet(); // of type Constant
    
    /**
     * <p>
     *
     * </p>
     */
    private Collection dataOutputVariables = new HashSet(); // of type DataOutputVariable
    
    /**
     * <p>
     *
     * </p>
     */
    private Collection objectListVariables = new HashSet(); // of type ObjectListVariable
    
    /**
     * <p>
     *
     * </p>
     */
    private OrderedDGWordList visualizationList = new OrderedDGWordList(this);
    
    private static final Iterator EMPTY_ITERATOR = Collections.EMPTY_LIST.iterator();
    
    private boolean dummy = false;
    
     /** Creates a new instance of DataGroup */
    public DataGroup() {
    }
    
    /* for xml-encoding purposes */
    public DataGroup(Collection dataInputVariables, Collection dataOutputVariables, Collection objectListVariables, Collection constants, OrderedDGWordList visualizationList) {
        this.dataInputVariables = dataInputVariables;
        this.dataOutputVariables = dataOutputVariables;
        this.objectListVariables = objectListVariables;
        this.constants = constants;
        this.visualizationList = visualizationList;
        
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public boolean isDummy() {
        return dummy;
    }
    
    private void setDummy(boolean dummy) {
        this.dummy = dummy;
    }
    
    public ReportFormat getReportFormat() {
        return reportFormat;
    }
    
    public void setReportFormat(ReportFormat reportFormat) {
        this.reportFormat = reportFormat;
    }
    
    public Collection getDataInputVariables() {
        return dataInputVariables;
    }
    protected void addDataInputVariable(DataInputVariable dataInputVariable) {
        this.dataInputVariables.add(dataInputVariable);
    }
    
    public Collection getConstants() {
        return constants;
    }
    protected void addConstant(Constant constant) {
        this.constants.add(constant);
    }
    
    public Collection getDataOutputVariables() {
        return dataOutputVariables;
    }
    protected void addDataOutputVariable(DataOutputVariable dataOutputVariable) {
        this.dataOutputVariables.add(dataOutputVariable);
    }
    
    public Collection getObjectListVariables() {
        return objectListVariables;
    }
    protected void addObjectListVariable(ObjectListVariable objectListVariable) {
        this.objectListVariables.add(objectListVariable);
    }
    
    public OrderedDGWordList getVisualizationList() {
        return visualizationList;
    }
    
    protected void setVisualizationList(OrderedDGWordList visualizationList) {
        this.visualizationList = visualizationList;
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
        Iterator iterInputVars = getDataInputVariables().iterator();
        while (iterInputVars.hasNext()) {
            DataInputVariable ivar = (DataInputVariable)iterInputVars.next();
            ivar.setOutputVariable(null);
        }
        Iterator iterConsts = getConstants().iterator();
        while (iterConsts.hasNext()) {
            Constant c = (Constant)iterConsts.next();
            c.setValue(null);
        }
    } // end reset
    
    

    public String toString() {
        return getHumanStringValue();
    }
    
    public String getHumanStringValue() {
        return getVisualizationList().getHumanStringValue();
    }
    
    public String getHumanStringValue(QueryOutputReportSeeder context) {
        return getVisualizationList().getHumanStringValue(context);
    }
    
    public Collection getOutputVariablesAvailableForImport() {
        int index = (isDummy() ? getReportFormat().getDataGroups().size() : getReportFormat().getIndexOf(this));
        return getReportFormat().getOutputVariablesAvailableForImport(index);
    }
    
    public Collection getOutputVariablesAvailableForImportInContext(DataGroup context) {
        return context.getOutputVariablesAvailableForImport();
    }
    
    public boolean isValid() {
        boolean valid = true;
        Iterator iterInputVars = getDataInputVariables().iterator();
        while (iterInputVars.hasNext()) {
            DataInputVariable ivar = (DataInputVariable)iterInputVars.next();
            valid &=  ((ivar.getOutputVariable() != null) && getOutputVariablesAvailableForImport().contains(ivar.getOutputVariable()));
        }
        Iterator iterConsts = getConstants().iterator();
        while (iterConsts.hasNext()) {
            Constant c = (Constant)iterConsts.next();
            valid &= (c.getValue() != null);
        }
        return valid;
    }
    
    public void updateRows(List dataRows) {
        Iterator iter = getDataOutputVariables().iterator();
        while (iter.hasNext()) {
            DataOutputVariable ovar = (DataOutputVariable)iter.next();
            for (int i = 0; i < dataRows.size(); i++) {
                DataRow row = (DataRow)dataRows.get(i);
                ovar.calculateValue(row);
            }
        }
    }
    
    // Difference with implementation in AtomicWhereClause :
    // here we do not need a global two-step process, since DataGroups can only refer to earlier
    // DataGroups; so we can make a clone of each DataGroup in turn given the context created by
    // cloning all the previous DataGroups (and meanwhile we update the context for this DataGroup)
    public DataGroup cloneInContext(Map originalToCloneMap) throws CloneNotSupportedException {
        DataGroup clone = new DataGroup();
        Iterator oLVarIt = getObjectListVariables().iterator();
        while (oLVarIt.hasNext()) {
            ObjectListVariable objectListVar = (ObjectListVariable)oLVarIt.next();
            ObjectListVariable objectListVarClone = (ObjectListVariable)objectListVar.clone();
            originalToCloneMap.put(objectListVar, objectListVarClone);
            clone.objectListVariables.add(objectListVarClone);
        }
        
        Iterator iterConstants = getConstants().iterator();
        while (iterConstants.hasNext()) {
            Constant constant = (Constant)iterConstants.next();
            Constant constantClone = (Constant)constant.clone();
            originalToCloneMap.put(constant, constantClone);
            clone.constants.add(constantClone);
        }
        
        // Difference with implementation in AtomicWhereClause :
        // here we can immediately assign the right outputVariables to inputVariables,
        // as they should have been put in the originalToCloneMap by a previous DataGroup
        Iterator iterInputVariables = getDataInputVariables().iterator();
        while (iterInputVariables.hasNext()) {
            DataInputVariable inputVar = (DataInputVariable)iterInputVariables.next();
            DataInputVariable inputVarClone = (DataInputVariable)inputVar.clone();
            DataOutputVariable outputVar = inputVar.getOutputVariable();
            DataOutputVariable outputVarClone = (DataOutputVariable)originalToCloneMap.get(outputVar);
            inputVarClone.setOutputVariable(outputVarClone == null ? outputVar : outputVarClone);
            originalToCloneMap.put(inputVar, inputVarClone);
            clone.dataInputVariables.add(inputVarClone);
        }
        
        Iterator iterOutputVariables = getDataOutputVariables().iterator();
        while (iterOutputVariables.hasNext()) {
            DataOutputVariable outputVar = (DataOutputVariable)iterOutputVariables.next();
            DataOutputVariable outputVarClone = (DataOutputVariable)outputVar.cloneInContext(originalToCloneMap);
            originalToCloneMap.put(outputVar, outputVarClone);
            clone.dataOutputVariables.add(outputVarClone);
        }
        OrderedDGWordList visualizationClone = (OrderedDGWordList)getVisualizationList().cloneInContext(originalToCloneMap, clone);
        clone.setVisualizationList(visualizationClone);
       
        return clone; 
    }
    
    public Object clone() throws CloneNotSupportedException {
        return cloneInContext(new HashMap());
    }
    
}
