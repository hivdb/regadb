/*
 * TableSelection.java
 *
 * Created on September 5, 2003, 10:27 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.io.Serializable;
import java.util.*;

import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

/**
 *
 * @author  kristof
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */
public class TableSelection extends ComposedSelection implements Serializable{
    
    private String tableName; // the Table accessed by the FromVariable that is the sole AWCWord in the Expression of this Selection's OutputVariable
    
    /** Creates a new instance of TableSelection */
    public TableSelection(OutputVariable ovar) {
        super(ovar);
        if (ovar.consistsOfSingleFromVariable()) {
            tableName = ((FromVariable)((OutputVariable)getObjectSpec()).getExpression().getWords().get(0)).getTableName();
            initFieldSelections();
        } else {
            System.err.println("TableSelection's OutputVariable does not refer to a single FromVariable. Something is terribly wrong !");
        }
    }
    
    public TableSelection(OutputVariable ovar, boolean selected) {
        this(ovar);
        setSelected(selected);
    }
    
    public Object getObject(Object objectSpec) {
        // the object (an OutputVariable) is specified by itself
        return (OutputVariable)objectSpec;
    }
    
    public Table getTable() {
        return DatabaseManager.getInstance().getTableCatalog().getTable(tableName);
    }
    
    public String getTableName() {
    	return tableName;
    }
    
    public String getVariableName() {
        return ((OutputVariable)getObject()).getUniqueName();
    }
    
    /*
     * JDBC version of the method.
     */
    private void initFieldSelections() {
        ArrayList<Selection> fieldSelections = new ArrayList<Selection>();
        
        Iterator<Field> fieldIter = getTable().getFields().iterator();
        while (fieldIter.hasNext()) {
            Field field = (Field)fieldIter.next();
            FieldSelection fieldSelection = new FieldSelection(field);
            if (field.isPrimaryKey()) {
                fieldSelection.setSelected(true);
            }
            fieldSelections.add(fieldSelection);
        }
        setSubSelections(fieldSelections);
    }
}
