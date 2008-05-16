/*
 * PropertySelection.java
 *
 * Created on November 28, 2003, 6:49 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

/**
 *
 * @author  kristof
 * 
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */
public class DataOutputListSelection extends com.pharmadm.custom.rega.queryeditor.SimpleSelection {
    
    /** Creates a new instance of PropertySelection */
    public DataOutputListSelection(DataOutputVariable ovar) {
        super(ovar);
    }
    
    public DataOutputListSelection(DataOutputVariable ovar, boolean selected) {
        super(ovar, selected);
    }
    
    public Object getObject(Object objectSpec) {
        // the object (a DataOutputVariable) is specified by itself
        return objectSpec;
    }
    
}
